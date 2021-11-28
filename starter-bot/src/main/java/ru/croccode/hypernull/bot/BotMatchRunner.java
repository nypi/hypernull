package ru.croccode.hypernull.bot;

import java.io.IOException;

import ru.croccode.hypernull.geometry.Offset;
import ru.croccode.hypernull.io.SocketSession;
import ru.croccode.hypernull.message.Hello;
import ru.croccode.hypernull.message.MatchOver;
import ru.croccode.hypernull.message.MatchStarted;
import ru.croccode.hypernull.message.Message;
import ru.croccode.hypernull.message.Move;
import ru.croccode.hypernull.message.Register;
import ru.croccode.hypernull.message.Update;
import ru.croccode.hypernull.util.Check;

public class BotMatchRunner implements Runnable {

	private final Bot bot;

	private final SocketSession session;

	public BotMatchRunner(Bot bot, SocketSession session) {
		Check.notNull(bot);
		Check.notNull(session);

		this.bot = bot;
		this.session = session;
	}

	@Override
	public void run() {
		try {
			runImpl();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				session.close();
			} catch (IOException ignore) {
			}
		}
	}

	public void runImpl() throws IOException {
		// wait for a server hello
		session.read(Hello.class);
		// send register
		Register register = bot.registerAs();
		session.write(register);
		// wait for a match start
		MatchStarted matchStarted = session.read(MatchStarted.class);
		bot.matchStarted(matchStarted);
		// wait for update
		while (true) {
			Message message = session.read();
			if (message instanceof MatchOver) {
				bot.matchOver((MatchOver)message);
				session.close();
				break;
			}
			if (message instanceof Update) {
				Move move = bot.makeMove((Update)message);
				if (move == null) {
					move = new Move();
					move.setOffset(new Offset(0, 0));
				}
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				session.write(move);
			}
		}
	}
}
