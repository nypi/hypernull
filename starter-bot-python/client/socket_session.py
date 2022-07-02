import logging
import socket


class SocketSession:
    def __init__(self, host: str, port: int):
        self.socket = socket.socket()
        self.socket.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        self.socket.connect((host, port))
        self.buffer = bytearray()

    def __del__(self):
        self.socket.close()

    def read(self) -> list[str]:
        end_index = self.buffer.find(b'end\n')
        while end_index == -1:
            self.buffer += self.socket.recv(4096)
            end_index = self.buffer.find(b'end\n')

        data = self.buffer[:end_index + 3]
        self.buffer = self.buffer[end_index + 4:]

        if len(self.buffer) > 0:
            logging.warning('skipping round, your bot had timed out')
            return self.read()

        return data.decode().split('\n')

    def write(self, data: str) -> None:
        self.socket.sendall(data.encode())
