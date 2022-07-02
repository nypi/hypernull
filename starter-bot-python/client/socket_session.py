import logging
import socket


class SocketSession:
    _buffer_size = 8192

    def __init__(self, host: str, port: int):
        self.socket = socket.socket()
        self.socket.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        self.socket.connect((host, port))
        self.buffer = bytearray()

    def __del__(self):
        self.socket.close()

    def read(self) -> list[str]:
        end_index = self._find_end_index()
        while end_index == -1:
            self.buffer += self.socket.recv(self._buffer_size)
            end_index = self._find_end_index()

        data = self.buffer[:end_index + 3]
        self.buffer = self.buffer[end_index + 4:]

        if len(self.buffer) > 0:
            logging.warning('skipping round, seems like your bot had timed out')
            return self.read()

        return data.decode().split('\n')

    def _find_end_index(self):
        return self.buffer.find(b'end\n', len(self.buffer) - self._buffer_size)

    def write(self, data: str) -> None:
        self.socket.sendall(data.encode())
