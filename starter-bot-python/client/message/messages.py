from dataclasses import dataclass, field

from . import extra_types
from .base import MessageBase


@dataclass
class Hello(MessageBase):
    protocol_version: int


@dataclass
class Register(MessageBase):
    bot_name: str
    bot_secret: str
    mode: extra_types.Mode


@dataclass
class MatchStarted(MessageBase):
    num_rounds: int
    mode: extra_types.Mode
    map_size: extra_types.XY
    your_id: int
    view_radius: int
    mining_radius: int
    attack_radius: int
    move_time_limit: int
    match_id: int = 0
    num_bots: int = 0

    def __post_init__(self):
        self.map_size.x = int(self.map_size.x)
        self.map_size.y = int(self.map_size.y)


@dataclass
class Update(MessageBase):
    round: int
    bot: list[extra_types.BotInfo] = field(default_factory=list)
    block: list[extra_types.XY] = field(default_factory=list)
    coin: list[extra_types.XY] = field(default_factory=list)


@dataclass
class Move(MessageBase):
    offset: extra_types.XY


@dataclass
class MatchOver(MessageBase):
    pass
