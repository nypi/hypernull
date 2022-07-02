from .bot_base import BotImpl
from .client import HypernullClient
from .message import messages


class BotMatchRunner:
    def __init__(self, bot: BotImpl, client: HypernullClient):
        self.bot = bot
        self.client = client

    def run(self) -> None:
        self.client.register(self.bot)

        match_info: messages.MatchStarted = self.client.get()
        self.bot.on_match_start(match_info)

        while update := self.client.get_update():
            dx, dy = self.bot.on_update(update)
            self.client.move(dx, dy)

        self.bot.on_match_over()
