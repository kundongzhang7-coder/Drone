"""对象存储封装：从 COS URL 下载源视频，处理完成后把结果回传 COS。"""
import logging

import httpx
from qcloud_cos import CosConfig, CosS3Client

logger = logging.getLogger(__name__)


class CosStorage:
    def __init__(self, settings):
        self.settings = settings
        config = CosConfig(
            Region=settings.cos_region,
            SecretId=settings.cos_secret_id,
            SecretKey=settings.cos_secret_key,
            Scheme="https",
        )
        self.client = CosS3Client(config)
        self.bucket = settings.cos_bucket

    def download(self, url: str, dest: str) -> str:
        logger.info("downloading source video")
        with httpx.stream("GET", url, follow_redirects=True, timeout=180) as resp:
            resp.raise_for_status()
            with open(dest, "wb") as handle:
                for chunk in resp.iter_bytes():
                    handle.write(chunk)
        return dest

    def upload(self, local_path: str, key: str) -> str:
        logger.info("uploading result video")
        self.client.upload_file(
            Bucket=self.bucket,
            LocalFilePath=local_path,
            Key=key,
        )
        return f"{self.settings.cos_base_url}/{key}"
