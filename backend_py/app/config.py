"""配置外置：所有参数通过环境变量注入，默认值仅用于本地开发。"""
from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_prefix="DRONE_",
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    # 服务
    host: str = "0.0.0.0"
    port: int = 8000
    log_level: str = "INFO"

    # 模型（默认基础模型，首次运行自动下载；可挂载自定义权重覆盖）
    model_path: str = "yolov8n.pt"
    inference_size: int = 640
    conf_threshold: float = 0.25

    # 腾讯云 COS
    cos_secret_id: str = ""
    cos_secret_key: str = ""
    cos_region: str = "ap-chengdu"
    cos_bucket: str = "drone-1482941883"
    cos_base_url: str = "https://drone-1482941883.cos.ap-chengdu.myqcloud.com"

    # 离线处理
    work_dir: str = "/tmp/drone_work"
    result_prefix: str = "video/result"


@lru_cache
def get_settings() -> Settings:
    return Settings()
