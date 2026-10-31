from ultralytics import YOLO

# 加载你训练好的 best.pt（请修改为实际路径）
model = YOLO(r"D:\JavaKnowledge\JAVA项目\Drone1\models\last.pt")

# 导出为 ONNX 格式
# 参数说明：
#   format="onnx"  导出 ONNX
#   imgsz=640      输入图像尺寸，与训练时保持一致
#   half=False     是否使用 FP16（可选，可提高速度但可能降低精度）
model.export(format="onnx", imgsz=640, half=False)

print("导出完成！生成的 last.onnx 在当前目录下。")