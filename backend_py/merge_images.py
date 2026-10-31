from PIL import Image, ImageDraw, ImageFont
import os

# 请修改这个路径为你的图片所在文件夹
image_folder = r"D:\存放文件"
output_path = os.path.join(image_folder, "merged_result.png")

# 定义图片顺序和对应的文字说明（按上面表格）
image_info = [
    ("系统架构.png", "系统架构"),
    ("检测前.png", "检测前界面 "),
    ("检测后.png", "实时检测结果"),
    ("检测帧率.png", "检测帧率"),
    ("检测记录.png", "检测记录"),
    ("训练结果1.jpeg", "训练结果1"),
    ("训练结果2.png", "训练结果2"),
    ("八种类型.png", "八种类型"),
    ("WebSocket_实时通信——每一帧图像.png", "WebSocket_实时通信——每一帧图像")
]

# 加载图片
images = []
for filename, caption in image_info:
    path = os.path.join(image_folder, filename)
    if not os.path.exists(path):
        print(f"警告：文件不存在 {path}，已跳过")
        continue
    img = Image.open(path).convert("RGB")
    images.append((img, caption))

if not images:
    print("没有找到任何图片")
    exit()

# 设置文字样式（使用默认字体，大小根据图片宽度自适应）
try:
    # 尝试加载一个中文字体，如果没有就用默认
    font_path = "C:/Windows/Fonts/simhei.ttf"  # Windows 黑体
    font = ImageFont.truetype(font_path, size=20)
except:
    font = ImageFont.load_default()

# 计算合并后的总高度（图片高度 + 文字区域高度）
text_height = 40  # 每张图片下方预留的文字区域高度
total_height = sum(img.size[1] for img, _ in images) + len(images) * text_height
max_width = max(img.size[0] for img, _ in images)

# 创建空白画布（白色背景）
merged = Image.new("RGB", (max_width, total_height), "white")

# 逐张粘贴
y_offset = 0
for img, caption in images:
    # 粘贴图片
    merged.paste(img, (0, y_offset))
    y_offset += img.size[1]
    
    # 绘制文字背景和文字
    draw = ImageDraw.Draw(merged)
    # 画一个浅灰色背景条
    draw.rectangle([(0, y_offset), (max_width, y_offset + text_height)], fill="#f0f0f0")
    # 写文字（居中）
    bbox = draw.textbbox((0, 0), caption, font=font)
    text_width = bbox[2] - bbox[0]
    text_x = (max_width - text_width) // 2
    text_y = y_offset + (text_height - (bbox[3] - bbox[1])) // 2
    draw.text((text_x, text_y), caption, fill="black", font=font)
    
    y_offset += text_height

# 保存合并后的图片
merged.save(output_path)
print(f"合并完成，保存至：{output_path}")