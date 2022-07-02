import dataclasses
import inspect
import types
from collections import defaultdict
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

        command = cls._to_camel_case(data[0])
        if command not in cls._known_message_types:
            raise Exception(f'unknown command: {command}')

        message_class = cls._known_message_types[command]
        field_name_type_mapping = {
            field.name: field.type for field in dataclasses.fields(message_class)
        }

        params = defaultdict(list)
        for row in data[1:-1]:
            name, *value = row.split()

            container, real_type = cls._get_type_and_container(
                field_type=field_name_type_mapping[name]
            )
            if container is None:
                params[name] = real_type(*value)
            elif container is list:
                params[name].append(real_type(*value))
            else:
                raise Exception(f'cannot handle {command}:{name}:{container}')

        return message_class(**params)

    @staticmethod
    def _get_type_and_container(field_type) -> tuple[type | None, type]:
        if isinstance(field_type, types.GenericAlias):
            container = field_type.__origin__
            real_type = field_type.__args__[0]
        else:
            container = None
            real_type = field_type
        return container, real_type

    @staticmethod
    def _to_camel_case(snake_case: str) -> str:
        return ''.join(t.title() for t in snake_case.split('_'))
