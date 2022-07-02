from bot import Bot, bot_name, bot_secret, mode
from client.bot_match_runner import BotMatchRunner
from client.client import HypernullClient

server_host = 'localhost'
server_port = 2021

if __name__ == '__main__':
    BotMatchRunner(
        bot=Bot(bot_name, bot_secret, mode),
        client=HypernullClient(server_host, server_port),
    ).run()
