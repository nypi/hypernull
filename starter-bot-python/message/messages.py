from dataclasses import dataclass

from .base import MessageBase


@dataclass
class Hello(MessageBase):
    protocol_version: int


@dataclass
class Register(MessageBase):
    bot_name: str
    bot_secret: str
    mode: str
