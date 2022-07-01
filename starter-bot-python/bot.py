from enum import Enum


class Mode(str, Enum):
    FRIENDLY = 'FRIENDLY'
    DEATHMATCH = 'DEATHMATCH'


class Bot:
    def __init__(self, name: str, secret: str, mode: Mode = 'FRIENDLY'):
        self.name = name
        self.secret = secret
        self.mode = mode
