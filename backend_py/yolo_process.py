import sys
import cv2
from ultralytics import YOLO
model_path = r"D:\JavaKnowledge\JAVA项目\Drone1\models\best.pt"  # 替换为你的模型实际路径
def process_video(input_path, output_path, model_path):
    # 1. 加载模型
    model = YOLO(model_path)

    # 2. 打开输入视频
    cap = cv2.VideoCapture(input_path)
    if not cap.isOpened():
        print(f"无法打开视频文件: {input_path}")
        return

    # 3. 获取视频信息
    fps = int(cap.get(cv2.CAP_PROP_FPS))
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    print(f"视频信息: {width}x{height}, {fps} fps")

    # 4. 准备写入器（输出视频编码格式为 mp4v）
    fourcc = cv2.VideoWriter_fourcc(*'acv1')
    out = cv2.VideoWriter(output_path, fourcc, fps, (width, height))

    frame_count = 0
    while True:
        ret, frame = cap.read()
        if not ret:
            break

        # 5. 对当前帧进行 YOLO 推理
        results = model(frame)

        # 6. 绘制检测结果（results[0].plot() 返回带框的图像）
        annotated_frame = results[0].plot()

        # 7. 写入输出视频
        out.write(annotated_frame)

        frame_count += 1
        if frame_count % 100 == 0:
            print(f"已处理 {frame_count} 帧")

    cap.release()
    out.release()
    print(f"处理完成！输出视频保存在: {output_path}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("使用方法: python yolo_process.py <输入视频路径> <输出视频路径>")
        sys.exit(1)

    input_video = sys.argv[1]
    output_video = sys.argv[2]
    model_path = r"D:\JavaKnowledge\JAVA项目\Drone1\models\best.pt"  # 替换为你的模型实际路径

    process_video(input_video, output_video, model_path)