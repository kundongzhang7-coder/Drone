import cv2
import numpy as np
import base64
import time
import os
import sys
from fastapi import FastAPI, WebSocket
from ultralytics import YOLO

app = FastAPI()


# 获取可执行文件所在目录（兼容开发环境和打包后的 exe）
if getattr(sys, 'frozen', False):
    # 打包成 exe 后，sys.executable 是 exe 的路径
    base_dir = os.path.dirname(sys.executable)
else:
    # 开发环境直接运行 .py 文件时，使用当前脚本所在目录
    base_dir = os.path.dirname(os.path.abspath(__file__))

# 模型文件路径（假设模型名为 last.pt，与 exe 同目录）
model_path = os.path.join(base_dir, "last.pt")

try:
    model = YOLO(model_path,task='detect')
except Exception as e:
    print(f"模型加载失败，使用默认yolov8n: {e}")
    model = YOLO("yolov8n.pt")
    
# 装饰器将路径映射到函数，并将该函数和路径绑定起来，使得客户端访问该路径的时候就会调用该函数
@app.websocket("/ws/video")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    print("前端已连接")
    try:
        while True:
            try:
                data = await websocket.receive_text()
            except Exception as e:
                print(f"接收消息失败: {e}")
                break  # 接收失败，退出循环

            try:
                # 去除 Base64 头部
                if "," not in data:
                    print("无效的 Base64 数据格式")
                    continue
                header, encoded = data.split(",", 1)
                img_bytes = base64.b64decode(encoded)
                np_arr = np.frombuffer(img_bytes, np.uint8)
                frame = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
                if frame is None:
                    print("解码图像失败")
                    continue
                #计时开始
                t0=time.perf_counter()
                # 推理
                results = model(frame)
                t1=time.perf_counter()
                elapsed_ms=(t1-t0)*1000
                print(f"推理耗时: {elapsed_ms:.1f} ms -> 实际FPS: {1000/elapsed_ms:.1f}")
                annotated_fr ame = results[0].plot()

                # 编码
                _, buffer = cv2.imencode('.jpg', annotated_frame)
                processed_base64 = base64.b64encode(buffer).decode('utf-8')
                response_data = f"data:image/jpeg;base64,{processed_base64}"

                # 发送前检查连接是否仍然打开
                if websocket.client_state.name == "CONNECTED":
                    await websocket.send_text(response_data)
                else:
                    print("WebSocket 已关闭，停止发送")
                    break
            except Exception as inner_e:
                print(f"处理单帧时出错: {inner_e}")
                # 继续处理下一帧，不中断连接
                continue
    except Exception as e:
        print(f"连接出现错误: {e}")
    finally:
        print("连接已关闭")
        try:
            await websocket.close()
        except:
            pass

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000) 