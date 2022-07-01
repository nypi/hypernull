from message.extra_types import Mode


class Bot:
    def __init__(self, name: str, secret: str = '', mode: Mode = Mode.FRIENDLY):
        self.name = name
        self.secret = secret
        self.mode = mode
