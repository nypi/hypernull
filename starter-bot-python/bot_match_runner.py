from client import HypernullClient
from message import messages


class BotMatchRunner:
    def __init__(self, bot, client: HypernullClient):
        self.bot = bot
        self.client = client

    def _register(self) -> None:
        self.client.send(
            messages.Register(
                bot_name=self.bot.name,
                bot_secret=self.bot.secret,
                mode=self.bot.mode,
            )
        )

    def run(self) -> None:
        self._register()
        match_started: messages.MatchStarted = self.client.get()

        while True:
            message: messages.Update | messages.MatchOver = self.client.get()
            # if isinstance(message, messages.MatchOver):
            break
