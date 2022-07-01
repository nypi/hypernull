from dataclasses import dataclass, asdict


@dataclass
class MessageBase:
    @classmethod
    def type(cls) -> str:
        return cls.__name__.lower()

    def dump(self) -> str:
        command = self.type()
        params = '\n'.join(
            f'{k} {" ".join(map(str, v)) if isinstance(v, list) else v}'
            for k, v in asdict(self).items() if v
        )
        return '\n'.join((command, params, 'end')) + '\n'
