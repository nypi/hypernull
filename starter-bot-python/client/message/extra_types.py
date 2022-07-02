from enum import Enum
from typing import NamedTuple


class Mode(str, Enum):
    FRIENDLY = 'FRIENDLY'
    DEATHMATCH = 'DEATHMATCH'


class XY(NamedTuple):
    x: int
    y: int


class BotInfo(NamedTuple):
    x: int
    y: int
    coins: int
    id: int
