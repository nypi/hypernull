import random
from dataclasses import dataclass, field

from client.message.extra_types import Mode
from client.message.messages import MatchStarted, Update

bot_name = 'nGragas'
bot_secret = ''
mode = Mode.FRIENDLY


@dataclass
class Bot:
    name: str
    secret: str = None
    mode: Mode = Mode.FRIENDLY
    match_info: MatchStarted = field(init=False)

    def on_match_started(self, match_info: MatchStarted):
        self.match_info = match_info
        print('Match started!')

    def on_update(self, update: Update) -> tuple[int, int]:
        round_number = update.round
        bots = update.bot
        coins = update.coin
        blocks = update.block

        dx = random.choice([-1, 0, 1])
        dy = random.choice([-1, 0, 1])
        return dx, dy

    def on_match_over(self) -> None:
        print('Match over')
