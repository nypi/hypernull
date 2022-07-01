import dataclasses
import inspect
import types
import typing
from typing import TypeVar

from . import messages


Message = TypeVar('Message', bound=messages.MessageBase)


class MessageFactory:
    _known_message_types: dict[str, type(messages.MessageBase)] = dict(
        inspect.getmembers(messages, inspect.isclass)
    )

    @classmethod
    def load(cls, data: list[str]) -> Message:
        if not data:
            raise Exception('got empty data')
        print(data)  # TODO delete

        command = cls._to_camel_case(data[0])
        if command not in cls._known_message_types:
            raise Exception(f'unknown command: {command}')

        message_class = cls._known_message_types[command]
        field_name_type_mapping = {
            field.name: field.type for field in dataclasses.fields(message_class)
        }

        params = {}
        for row in data[1:-1]:
            name, *value = row.split()
            param_type = field_name_type_mapping[name]

            if isinstance(param_type, types.GenericAlias):
                param_type = param_type.__origin__

            if param_type is not list:
                params[name] = param_type(*value)
            else:
                if name not in params:
                    params[name] = [value]
                else:
                    params[name].append(value)
        print(params)
        return message_class(**params)

    @staticmethod
    def _to_camel_case(snake_case: str) -> str:
        return ''.join(t.title() for t in snake_case.split('_'))
