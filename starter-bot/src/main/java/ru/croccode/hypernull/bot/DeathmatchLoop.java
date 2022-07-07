package ru.croccode.hypernull.bot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Paths;

import ru.croccode.hypernull.io.SocketSession;

public class DeathmatchLoop {

	public static void main(String[] args) throws IOException {
		String configPath = args.length > 0
				? args[0]
				: "bot.properties";
		BotConfig botConfig = BotConfig.load(Paths.get(configPath));

		while (true) {
			try {
				Socket socket = new Socket();
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(300_000);
				socket.connect(new InetSocketAddress(
						botConfig.getServerHost(),
						botConfig.getServerPort()));

				SocketSession session = new SocketSession(socket);
				StarterBot bot = new StarterBot(botConfig.getBotName(), botConfig.getMode());
				new BotMatchRunner(bot, session).run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
