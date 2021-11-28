package ru.croccode.hypernull.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.io.SocketSession;
import ru.croccode.hypernull.message.Hello;
import ru.croccode.hypernull.message.Register;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Strings;

public class Server implements Closeable {

	private static final int NUM_ACCEPT_THREADS = 4;
	private static final int SOCKET_READ_TIMEOUT_MILLIS = 300_000;

	private final ServerSocket serverSocket;

	// match request queues
	private final Map<MatchMode, Deque<MatchRequest>> queues = new HashMap<>();

	private final Lock lock = new ReentrantLock();

	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		for (int i = 0; i < NUM_ACCEPT_THREADS; i++) {
			ThreadPools.defaultPool().submit(() -> accept(serverSocket));
		}
	}

	private void accept(ServerSocket serverSocket) {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(SOCKET_READ_TIMEOUT_MILLIS);
				SocketSession session = new SocketSession(socket);
				sessionOpened(session);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean authenticate(String botName, String botSecret) {
		// TODO check bot secret
		return true;
	}

	private void sessionOpened(SocketSession session) {
		ThreadPools.defaultPool().submit(() -> {
			try {
				// send hello
				session.write(new Hello());
				// wait for register
				Register register = session.read(Register.class);
				if (register == null) {
					session.close();
					return;
				}
				MatchMode mode = register.getMode() != null
						? register.getMode()
						: MatchMode.FRIENDLY;
				String botName = Strings.emptyToNull(register.getBotName());
				String botSecret = Strings.emptyToNull(register.getBotSecret());
				if (!authenticate(botName, botSecret)) {
					session.close();
					return;
				}
				MatchRequest request = new MatchRequest(botName, session, mode);
				offerRequest(request);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					session.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		});
	}

	private void offerRequest(MatchRequest request) {
		lock.lock();
		try {
			queues
					.computeIfAbsent(request.getMode(), k -> new ArrayDeque<>())
					.offer(request);
		} finally {
			lock.unlock();
		}
	}

	public List<MatchRequest> pollRequests(MatchMode mode, int min, int max) {
		Check.notNull(mode);
		lock.lock();
		try {
			Deque<MatchRequest> queue = queues.get(mode);
			if (queue == null || queue.size() < min)
				return Collections.emptyList();

			List<MatchRequest> polled = new ArrayList<>(max);
			while (!queue.isEmpty() && polled.size() < max) {
				MatchRequest request = queue.poll();
				if (!request.getSession().isOpen())
					continue;
				polled.add(request);
			}
			if (polled.size() < min) {
				for (int i = polled.size() - 1; i >= 0; i--)
					queue.addFirst(polled.get(i));
				polled.clear();
			}
			return polled;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
	}
}
