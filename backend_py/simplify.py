import torch
from ultralytics import YOLO
from ultralytics.nn.tasks import DetectionModel   # 方法二需要导入

# 方法一：直接添加 weights_only=False
original_ckpt = torch.load(
    r"D:\JavaKnowledge\JAVA项目\Drone1\models\best.pt", 
    map_location='cpu',
    weights_only=False   # 关键修改
)

# 方法二（可选）：使用安全白名单，与 weights_only=False 二选一
# torch.serialization.add_safe_globals([DetectionModel])
# original_ckpt = torch.load(r"D:\JavaKnowledge\JAVA项目\Drone1\models\best.pt", map_location='cpu')

# 加载模型
model = YOLO(r"D:\JavaKnowledge\JAVA项目\Drone1\models\best.pt")

# 创建精简 checkpoint
lite_ckpt = {
    'model': model.model.state_dict(),
    'epoch': original_ckpt.get('epoch', -1),
    'best_fitness': original_ckpt.get('best_fitness', None),
    'ema': None,
    'updates': None,
    'optimizer': None,
    'train_args': original_ckpt.get('train_args', {}),
    'version': original_ckpt.get('version', '8.4.21'),
}

# 保存
torch.save(lite_ckpt, 'lite_best.pt')
print("精简模型已保存为 lite_best.pt")