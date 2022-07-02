from dataclasses import dataclass
from enum import Enum


class Mode(str, Enum):
    FRIENDLY = 'FRIENDLY'
    DEATHMATCH = 'DEATHMATCH'


@dataclass
class XY:
    x: int
    y: int


@dataclass
class BotInfo(XY):
    coins: int
    id: int
