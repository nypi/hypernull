import socket


class SocketSession:
    def __init__(self, host: str, port: int):
        self.socket = socket.socket()
        self.socket.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        self.socket.connect((host, port))

    def __del__(self):
        self.socket.close()

    def read(self) -> list[str]:
        data = bytearray()
        while not data.endswith(b'end\n'):
            data += self.socket.recv(4096)
        return data.removesuffix(b'\n').decode().split('\n')

    def write(self, data: str) -> None:
        self.socket.sendall(data.encode())
