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


@dataclass
class Update(MessageBase):
    round: int
    bot: list[list[int]] = field(default_factory=list)
    block: list[extra_types.XY] = field(default_factory=list)
    coin: list[extra_types.XY] = field(default_factory=list)


@dataclass
class MatchOver(MessageBase):
    pass
