from client.bot_base import BotBase
from client.message.extra_types import Mode
from client.message.messages import MatchStarted, Update

# Параметры регистрации бота на сервере
bot_name = 'SuperStarter'
bot_secret = ''
mode = Mode.FRIENDLY


class Bot(BotBase):
    # Старт
    def on_match_start(self, match_info: MatchStarted) -> None:
        # Правила матча
        self.match_info = match_info
        self.id = match_info.your_id
        print(match_info)
        print(f'Матч стартовал! Бот <{self.name}> готов')

    # Каждый ход
    def on_update(self, update: Update) -> tuple[int, int]:
        # Данные раунда: что бот "видит"
        round_number = update.round
        coins = update.coin
        blocks = update.block
        my_bot = next((bot for bot in update.bot if bot.id == self.id), None)
        opponents = [bot for bot in update.bot if bot.id != self.id]

        # Выбираем направление движения
        import random
        dx = random.choice([-1, 0, 1])
        dy = random.choice([-1, 0, 1])

        return dx, dy  # Отправляем ход серверу

    # Конец матча
    def on_match_over(self) -> None:
        print('Матч окончен')
