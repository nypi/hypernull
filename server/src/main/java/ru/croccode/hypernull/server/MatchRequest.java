package ru.croccode.hypernull.server;

import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.io.SocketSession;
import ru.croccode.hypernull.util.Check;

public class MatchRequest {

	private final String botName;

	private final SocketSession session;

	private final MatchMode mode;

	public MatchRequest(String botName, SocketSession session, MatchMode mode) {
		Check.notEmpty(botName);
		Check.notNull(session);
		Check.notNull(mode);

		this.botName = botName;
		this.session = session;
		this.mode = mode;
	}

	public String getBotName() {
		return botName;
	}

	public SocketSession getSession() {
		return session;
	}

	public MatchMode getMode() {
		return mode;
	}
}
