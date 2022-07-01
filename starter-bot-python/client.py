from socket_session import SocketSession
from message import factory, messages


class HypernullClient:
    version: int = 1

    def __init__(self, host: str = 'localhost', port: int = 2021):
        self.session = SocketSession(host, port)
        msg = self.get()
        if not isinstance(msg, messages.Hello):
            raise Exception(
                f'Wanted message {messages.Hello.__name__}, got: {type(msg)}'
            )

        if msg.protocol_version != self.version:
            raise Exception(
                f'Client v{self.version}, but Server v{msg.protocol_version}'
            )

    def get(self) -> factory.Message:
        data = self.session.read()
        return factory.MessageFactory.load(data)

    def send(self, msg: messages.MessageBase) -> None:
        data = msg.dump()
        self.session.write(data)
