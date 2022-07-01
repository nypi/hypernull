from bot import Bot
from bot_match_runner import BotMatchRunner
from client import HypernullClient
from message.extra_types import Mode

server_host = 'localhost'
server_port = 2021

bot_name = 'nGragas'
bot_secret = ''
mode = Mode.FRIENDLY


client = HypernullClient(server_host, server_port)
bot = Bot(bot_name, bot_secret, mode)
match_runner = BotMatchRunner(bot, client)
match_runner.run()
