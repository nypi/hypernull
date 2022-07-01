from client import HypernullClient
from message import messages


class BotMatchRunner:
    def __init__(self, bot, client: HypernullClient):
        self.bot = bot
        self.client = client

    def _register(self):
        self.client.send(
            messages.Register(
                bot_name=self.bot.name,
                bot_secret=self.bot.secret,
                mode=self.bot.mode,
            )
        )

    def run(self):
        self._register()
