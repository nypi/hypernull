import dataclasses
import inspect
import types
from collections import defaultdict
from typing import TypeVar, Type

from . import messages


Message = TypeVar('Message', bound=messages.MessageBase)


class MessageFactory:
    _known_message_types: dict[str, Type[Message]] = dict(
        inspect.getmembers(messages, inspect.isclass)
    )

    @classmethod
    def load(cls, data: list[str]) -> Message:
        if not data:
            raise Exception('got empty data')

        command = cls._to_camel_case(data[0])
        if command not in cls._known_message_types:
            raise Exception(f'unknown command: {command}')

        message_class = cls._known_message_types[command]
        field_type_mapping: dict[str, tuple[type | None, type]] = {
            field.name: cls._get_field_types(field)
            for field in dataclasses.fields(message_class)
        }

        params = defaultdict(list)
        for row in data[1:-1]:
            name, *value = row.split()

            container, real_type = field_type_mapping[name]
            if container is None:
                params[name] = real_type(*value)
            elif container is list:
                # map(int, ) assumes that all nested types has only int fields
                params[name].append(real_type(*map(int, value)))
            else:
                raise Exception(f'cannot handle {command}:{name}:{container}')

        return message_class(**params)

    @staticmethod
    def _get_field_types(field: dataclasses.Field) -> tuple[type | None, type]:
        if isinstance(field.type, types.GenericAlias):
            container = field.type.__origin__
            real_type = field.type.__args__[0]
        else:
            container = None
            real_type = field.type
        return container, real_type

    @staticmethod
    def _to_camel_case(snake_case: str) -> str:
        return ''.join(t.title() for t in snake_case.split('_'))
