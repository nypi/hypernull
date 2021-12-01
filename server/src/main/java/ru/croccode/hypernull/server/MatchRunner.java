package ru.croccode.hypernull.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Offset;
import ru.croccode.hypernull.io.SocketSession;
import ru.croccode.hypernull.match.Bot;
import ru.croccode.hypernull.match.Match;
import ru.croccode.hypernull.match.MatchConfig;
import ru.croccode.hypernull.message.MatchOver;
import ru.croccode.hypernull.message.MatchStarted;
import ru.croccode.hypernull.message.Move;
import ru.croccode.hypernull.message.Update;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Silent;

public class MatchRunner implements Runnable {

	private final Match<Integer> match;

	private final Map<Integer, SocketSession> botSessions;

	public MatchRunner(Match<Integer> match, Map<Integer, SocketSession> botSessions) {
		Check.notNull(match);
		Check.notNull(botSessions);

		this.match = match;
		this.botSessions = botSessions;
	}

	@Override
	public void run() {

		for (Integer botKey : botSessions.keySet()) {
			MatchStarted matchStarted = buildMatchStarted(botKey);
			try {
				botSessions.get(botKey).write(matchStarted);
			} catch (IOException e) {
				match.deactivateBot(botKey);
				e.printStackTrace();
			}
		}

		match:
		while (match.isActive()) {

			// close inactive bot sessions
			List<Integer> inactive = new ArrayList<>();
			botSessions.forEach((k, v) -> {
				if (!match.isActive(k))
					inactive.add(k);
			});
			inactive.forEach(this::closeSession);

			Map<Integer, CompletableFuture<Move>> responses = new HashMap<>();
			for (Integer botKey : botSessions.keySet()) {
				Update update = buildUpdate(botKey);
				try {
					botSessions.get(botKey).write(update);
					// wait for a move message
					CompletableFuture<Move> response = waitForMove(botKey);
					responses.put(botKey, response);
				} catch (IOException e) {
					match.deactivateBot(botKey);
					e.printStackTrace();
				}
			}

			Map<Integer, Offset> botMoves = new HashMap<>();
			for (Map.Entry<Integer, CompletableFuture<Move>> entry : responses.entrySet()) {
				Integer botKey = entry.getKey();
				CompletableFuture<Move> response = entry.getValue();
				Move botMove = null;
				try {
					botMove = response.get();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break match;
				}
				if (botMove != null)
					botMoves.put(botKey, botMove.getOffset());
			}
			match.completeRound(botMoves);
		}
		new ArrayList<>(botSessions.keySet()).forEach(this::closeSession);
	}

	private void closeSession(Integer botKey) {
		SocketSession session = botSessions.remove(botKey);
		if (session == null)
			return;
		ThreadPools.defaultPool().submit(Silent.runnableOf(() -> {
			if (session.isOpen())
				session.write(new MatchOver());
			session.close();
		}));
	}

	private CompletableFuture<Move> waitForMove(Integer botKey) throws IOException {
		SocketSession session = botSessions.get(botKey);
		if (session == null)
			return CompletableFuture.completedFuture(null);
		CompletableFuture<Move> future = CompletableFuture.supplyAsync(
				Silent.supplierOf(() -> session.read(Move.class)),
				ThreadPools.defaultPool());
		future = future.completeOnTimeout(null,
				match.getConfig().getMoveTimeLimit(), TimeUnit.MILLISECONDS);
		return future;
	}

	private MatchStarted buildMatchStarted(Integer botKey) {
		MatchMap map = match.getMap();
		MatchConfig config = match.getConfig();

		MatchStarted matchStarted = new MatchStarted();
		matchStarted.setNumRounds(config.getNumRounds());
		matchStarted.setMode(config.getMode());
		matchStarted.setMapSize(map.getSize());
		matchStarted.setYourId(botKey);
		matchStarted.setViewRadius(map.getViewRadius());
		matchStarted.setMiningRadius(map.getMiningRadius());
		matchStarted.setAttackRadius(map.getAttackRadius());
		matchStarted.setMoveTimeLimit(config.getMoveTimeLimit());
		return matchStarted;
	}

	private Update buildUpdate(Integer botKey) {
		Update update = new Update();
		update.setRound(match.getRound());
		Set<Bot<Integer>> visibleBots = match.getVisibleBots(botKey);
		if (!visibleBots.isEmpty()) {
			update.setBots(new HashMap<>());
			update.setBotCoins(new HashMap<>());
			for (Bot<Integer> bot : visibleBots) {
				if (!bot.isActive())
					continue;
				update.getBots().put(bot.getKey(), bot.getPosition());
				update.getBotCoins().put(bot.getKey(), bot.getNumCoins());
			}
		}
		update.setBlocks(match.getVisibleBlocks(botKey));
		update.setCoins(match.getVisibleCoins(botKey));
		return update;
	}
}
