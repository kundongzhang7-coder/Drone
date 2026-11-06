"""Pydantic v2 数据模型，字段名与 Java 端契约严格一致。"""
from pydantic import BaseModel, ConfigDict, Field


class OfflineRequest(BaseModel):
    # 接收 Java 发出的驼峰 JSON
    model_config = ConfigDict(populate_by_name=True)

    task_id: int = Field(alias="taskId")
    video_url: str = Field(alias="videoUrl")


class OfflineResponse(BaseModel):
    # 输出字段必须与 Java 的 PythonDetectionResult 记录组件同名
    resultVideoUrl: str
    totalFrames: int
    droneCount: int


class HealthResponse(BaseModel):
    status: str
    modelLoaded: bool
