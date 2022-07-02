from dataclasses import dataclass, asdict


@dataclass
class MessageBase:
    @classmethod
    def type(cls) -> str:
        return cls.__name__.lower()

    def dump(self) -> str:
        command = self.type()
        params = '\n'.join(
            f'{k} {" ".join(map(str, v.values())) if isinstance(v, dict) else v}'
            for k, v in asdict(self).items() if v not in [None, '']
        )
        return f'{command}\n' \
               f'{params}\n' \
               f'end\n'
