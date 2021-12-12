package ru.croccode.hypernull.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.io.SocketSession;
import ru.croccode.hypernull.map.MapRegistry;
import ru.croccode.hypernull.map.MapStore;
import ru.croccode.hypernull.map.RandomMap;
import ru.croccode.hypernull.match.Match;
import ru.croccode.hypernull.match.MatchConfig;
import ru.croccode.hypernull.match.MatchListener;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Silent;

public class HyperNull implements Runnable, Closeable {

	private static final int MIN_FRIENDLY_BOTS = 1;
	private static final int MAX_FRIENDLY_BOTS = 4;
	private static final int MIN_DEATHMATCH_BOTS = 2;
	private static final int MAX_DEATHMATCH_BOTS = 4;

	private final MapRegistry mapRegistry;

	private final Server server;
	private final String matchLogsFolder;

	public HyperNull(Properties properties) throws IOException {
		Check.notNull(properties);
		// start server
		int serverPort = Integer.parseInt(
				properties.getProperty("server.port", "2021"));
		matchLogsFolder = properties.getProperty("match.log.folder","./matchlogs/");
		System.out.println("Match logs folder was set to: " + matchLogsFolder);
		String mapsFolder = properties.getProperty("maps.folder","./maps/");
		System.out.println("Maps folder was set to: " + mapsFolder);
		mapRegistry = MapStore.load(Paths.get(mapsFolder));
		this.server = new Server(serverPort);
		System.out.println("Server started on port: " + serverPort);
		System.out.println("Server logs output: STDOUT");
	}

	@Override
	public void run() {
		List<MatchMode> modes = Arrays.asList(MatchMode.FRIENDLY, MatchMode.DEATHMATCH);
		while (true) {
			List<MatchRequest> matchRequests = Collections.emptyList();
			Collections.shuffle(modes);
			for (MatchMode mode : modes) {
				matchRequests = server.pollRequests(
						mode,
						mode == MatchMode.FRIENDLY ? MIN_FRIENDLY_BOTS : MIN_DEATHMATCH_BOTS,
						mode == MatchMode.FRIENDLY ? MAX_FRIENDLY_BOTS : MAX_DEATHMATCH_BOTS);
				if (!matchRequests.isEmpty()) {
					runMatch(mode, matchRequests);
				}
			}
			if (matchRequests.isEmpty()) {
				try {
					Thread.sleep(1_000L);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}

	private void runMatch(MatchMode mode, List<MatchRequest> matchRequests) {
		int numBots = matchRequests.size();
		if (numBots == 0)
			return;

		Map<Integer, String> botNames = new HashMap<>();
		Map<Integer, SocketSession> botSessions = new HashMap<>();
		Integer botKey = 0;
		for (MatchRequest matchRequest : matchRequests) {
			botNames.put(botKey, matchRequest.getBotName());
			botSessions.put(botKey, matchRequest.getSession());
			botKey++;
		}

		String matchId = MatchId.nextId();
		MatchMap map = mapRegistry.randomMap(numBots);
		if (map == null) {
			// generate random map
			map = new RandomMap(numBots);
		}
		MatchConfig config = buildMatchConfig(mode, map);
		try (MatchFileLogger<Integer> fileLogger = new MatchFileLogger<>(matchId, this.matchLogsFolder)) {
			List<MatchListener<Integer>> listeners = Arrays.asList(
				new AsciiMatchPrinter(),
				fileLogger
			);
			Match<Integer> match = new Match<>(matchId, map, config, botNames, listeners);
			new MatchRunner(match, botSessions).run();
		}
	}

	private MatchConfig buildMatchConfig(MatchMode mode, MatchMap map) {
		return MatchConfig.newBuilder()
				.setNumRounds(500)
				.setMode(mode)
				.setMoveTimeLimit(1_000L)
				.setCoinSpawnPeriod(5)
				.setCoinSpawnVolume(2)
				.build();
	}

	@Override
	public void close() throws IOException {
		server.close();
	}

	public static void main(String[] args) throws IOException {
		System.out.println("¤ ¤ ¤ HyperNull STARTING... ¤ ¤ ¤");
		String configPath = args.length > 0
				? args[0]
				: "hypernull.properties";
		Properties properties = new Properties();
		Path path = Paths.get(configPath);
		if (Files.exists(path)) {
			try (InputStream in = Files.newInputStream(Paths.get(configPath))) {
				properties.load(in);
			}
		}

		HyperNull app = new HyperNull(properties);
		Runtime.getRuntime().addShutdownHook(new Thread(Silent.runnableOf(() -> {
			app.close();
			ThreadPools.shutdownAll();
		})));
		System.out.println("¤ ¤ ¤ HyperNull READY ¤ ¤ ¤ ");
		app.run();
	}
}
