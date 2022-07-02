from dataclasses import dataclass
from enum import Enum


class Mode(str, Enum):
    FRIENDLY = 'FRIENDLY'
    DEATHMATCH = 'DEATHMATCH'


@dataclass
class XY:
    x: int
    y: int

    def __post_init__(self):
        self.x = int(self.x)
        self.y = int(self.y)


@dataclass
class BotInfo(XY):
    coins: int
    id: int

    def __post_init__(self):
        super().__post_init__()
        self.coins = int(self.coins)
        self.id = int(self.id)
