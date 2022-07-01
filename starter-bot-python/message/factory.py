import inspect

from . import messages


class MessageFactory:
    _known_message_types: dict[str, type(messages.MessageBase)] = dict(
        inspect.getmembers(messages, inspect.isclass)
    )

    @classmethod
    def load(cls, data: list[str]) -> messages.MessageBase:
        if not data:
            raise Exception('got empty data')
        # print(data)  # TODO delete

        command = data[0].capitalize()
        if command not in cls._known_message_types:
            raise Exception(f'unknown command: {command}')

        params = {}
        for row in data[1:-1]:
            key, value = row.split(' ', 1)
            if len(value) == 1:
                params[key] = int(value)
            else:
                params[key] = list(map(int, value.split()))

        return cls._known_message_types[command](**params)
