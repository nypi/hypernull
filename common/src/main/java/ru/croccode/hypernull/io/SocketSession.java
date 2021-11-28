package ru.croccode.hypernull.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;

import ru.croccode.hypernull.message.Message;
import ru.croccode.hypernull.util.Check;

public class SocketSession extends Session {

	private final Socket socket;

	public SocketSession(Socket socket) throws IOException {
		super(socketReader(socket), socketWriter(socket));
		this.socket = socket;
	}

	private static Reader socketReader(Socket socket) throws IOException {
		Check.notNull(socket);
		return new InputStreamReader(socket.getInputStream());
	}

	private static Writer socketWriter(Socket socket) throws IOException {
		Check.notNull(socket);
		return new OutputStreamWriter(socket.getOutputStream());
	}

	public boolean isOpen() {
		return socket.isConnected() && !socket.isClosed();
	}

	@Override
	public void close() throws IOException {
		if (!socket.isClosed())
			socket.close();
	}
}
