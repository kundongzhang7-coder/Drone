"""离线视频检测：下载源视频 -> 逐帧推理 -> 合成结果视频 -> 上传 COS。"""
import logging
import subprocess
import uuid
from pathlib import Path

import cv2

logger = logging.getLogger(__name__)


class OfflineDetector:
    def __init__(self, model, settings, storage):
        self.model = model
        self.settings = settings
        self.storage = storage

    def process(self, video_url: str) -> dict:
        work = Path(self.settings.work_dir)
        work.mkdir(parents=True, exist_ok=True)
        job_id = uuid.uuid4().hex
        source_path = work / f"{job_id}_source.mp4"
        temp_avi = work / f"{job_id}_temp.avi"
        result_path = work / f"{job_id}_result.mp4"

        try:
            self.storage.download(video_url, str(source_path))

            cap = cv2.VideoCapture(str(source_path))
            if not cap.isOpened():
                raise RuntimeError("无法打开源视频")
            fps = cap.get(cv2.CAP_PROP_FPS) or 15
            width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
            height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
            writer = cv2.VideoWriter(
                str(temp_avi),
                cv2.VideoWriter_fourcc(*"MJPG"),
                fps,
                (width, height),
            )

            total_frames = 0
            peak_drones = 0
            while True:
                ok, frame = cap.read()
                if not ok:
                    break
                results = self.model(
                    frame,
                    imgsz=self.settings.inference_size,
                    conf=self.settings.conf_threshold,
                    verbose=False,
                )
                boxes = results[0].boxes
                frame_drones = 0 if boxes is None else len(boxes)
                peak_drones = max(peak_drones, frame_drones)
                writer.write(results[0].plot())
                total_frames += 1

            cap.release()
            writer.release()

            # 转码为浏览器友好的 H.264 MP4
            subprocess.run(
                [
                    "ffmpeg", "-y", "-i", str(temp_avi),
                    "-vcodec", "libx264", "-pix_fmt", "yuv420p",
                    str(result_path),
                ],
                check=True,
                capture_output=True,
            )

            key = f"{self.settings.result_prefix}/{job_id}.mp4"
            result_url = self.storage.upload(str(result_path), key)
            logger.info("offline detection finished")
            return {
                "resultVideoUrl": result_url,
                "totalFrames": total_frames,
                "droneCount": peak_drones,
            }
        finally:
            for path in (source_path, temp_avi, result_path):
                try:
                    path.unlink(missing_ok=True)
                except OSError:
                    logger.warning("cleanup failed for %s", path)
