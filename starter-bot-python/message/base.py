from dataclasses import dataclass, asdict
from typing import Any


@dataclass
class MessageBase:
    @classmethod
    def type(cls) -> str:
        return cls.__name__.lower()

    def fields(self) -> dict[str, Any]:
        return asdict(self)

    def dump(self) -> str:
        command = self.type()
        params = '\n'.join(
            f'{k} {" ".join(map(str, v)) if isinstance(v, list) else v}'
            for k, v in self.fields().items()
        )
        return '\n'.join((command, params, 'end'))
