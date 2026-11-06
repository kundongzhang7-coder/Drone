"""FastAPI 应用入口：lifespan 统一管理模型生命周期，提供健康检查、离线检测与实时 WS。"""
import base64
import contextlib
import logging

import cv2
import numpy as np
from fastapi import FastAPI, HTTPException, Request, WebSocket, WebSocketDisconnect
from ultralytics import YOLO

from .config import get_settings
from .detector import OfflineDetector
from .logging_setup import setup_logging
from .schemas import HealthResponse, OfflineRequest, OfflineResponse
from .storage import CosStorage

logger = logging.getLogger(__name__)


@contextlib.asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    setup_logging(settings.log_level)
    logger.info("loading YOLO model")
    # 模型在启动时一次性加载，避免首请求延迟与重复占用内存
    app.state.settings = settings
    app.state.model = YOLO(settings.model_path, task="detect")
    app.state.storage = (
        CosStorage(settings)
        if settings.cos_secret_id and settings.cos_secret_key
        else None
    )
    app.state.detector = OfflineDetector(
        app.state.model, settings, app.state.storage
    )
    logger.info("startup complete")
    yield
    logger.info("service shutting down")


app = FastAPI(title="Drone Detection Service", version="1.0.0", lifespan=lifespan)


@app.get("/health", response_model=HealthResponse)
def health(request: Request):
    return HealthResponse(status="UP", modelLoaded=request.app.state.model is not None)


@app.post("/detect/offline", response_model=OfflineResponse)
def detect_offline(payload: OfflineRequest, request: Request):
    if request.app.state.storage is None:
        raise HTTPException(status_code=503, detail="对象存储未配置，无法处理离线任务")
    logger.info("offline task received")
    result = request.app.state.detector.process(payload.video_url)
    return OfflineResponse(**result)


def _decode_frame(data: str):
    if "," not in data:
        return None
    _, encoded = data.split(",", 1)
    raw = base64.b64decode(encoded)
    np_arr = np.frombuffer(raw, np.uint8)
    return cv2.imdecode(np_arr, cv2.IMREAD_COLOR)


@app.websocket("/ws/video")
async def websocket_video(websocket: WebSocket):
    await websocket.accept()
    model = websocket.app.state.model
    settings = websocket.app.state.settings
    logger.info("frontend connected")
    try:
        while True:
            data = await websocket.receive_text()
            frame = _decode_frame(data)
            if frame is None:
                continue
            results = model(
                frame,
                imgsz=settings.inference_size,
                conf=settings.conf_threshold,
                verbose=False,
            )
            annotated = results[0].plot()
            ok, buffer = cv2.imencode(".jpg", annotated)
            if not ok:
                continue
            processed = base64.b64encode(buffer).decode("utf-8")
            await websocket.send_text(f"data:image/jpeg;base64,{processed}")
    except WebSocketDisconnect:
        logger.info("frontend disconnected")
    except Exception:
        logger.exception("websocket error")
    finally:
        try:
            await websocket.close()
        except Exception:
            pass
