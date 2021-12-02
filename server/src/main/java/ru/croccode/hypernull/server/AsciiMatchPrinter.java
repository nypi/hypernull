package ru.croccode.hypernull.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.match.MatchConfig;
import ru.croccode.hypernull.match.MatchListener;
import ru.croccode.hypernull.util.Strings;

public class AsciiMatchPrinter implements MatchListener<Integer> {

	private static final char FREE = ' ';
	private static final char BLOCK = 'X';
	private static final char COIN = '¤';
	private static final char VIEW_MASK = '.';
	private static final char MINING_MASK = '+';
	private static final char ATTACK_MASK = '-';

	private String matchId;

	private MatchMap map;

	private MatchConfig config;

	private Map<Integer, BotState> bots = new HashMap<>();

	private Set<Point> coins = new HashSet<>();

	private int round;

	static class BotState {

		String name = Strings.empty();

		Point position;

		int numCoins = 0;

		boolean alive = true;
	}

	@Override
	public void matchStarted(String id, MatchMap map, MatchConfig config, Map<Integer, String> botNames) {
		this.matchId = id;
		this.map = map;
		this.config = config;
		this.bots = new HashMap<>(botNames.size());
		botNames.forEach((k, v) -> {
			BotState bot = new BotState();
			bot.name = v;
			bots.put(k, bot);
		});
	}

	@Override
	public void matchRound(int round) {
		this.round = round;
		printState();
	}

	@Override
	public void coinSpawned(Point position) {
		coins.add(position);
	}

	@Override
	public void coinCollected(Point position, Integer botKey) {
		coins.remove(position);
	}

	@Override
	public void botSpawned(Integer botKey, Point position) {
		bots.computeIfAbsent(botKey, k -> new BotState())
				.position = position;
	}

	@Override
	public void botMoved(Integer botKey, Point position) {
		bots.computeIfAbsent(botKey, k -> new BotState())
				.position = position;
	}

	@Override
	public void attack(Integer attackingKey, Integer defendingKey) {
	}

	@Override
	public void botCoinsChanged(Integer botKey, int numCoins) {
		bots.computeIfAbsent(botKey, k -> new BotState())
				.numCoins = numCoins;
	}

	@Override
	public void matchOver(Integer botKey) {
		bots.computeIfAbsent(botKey, k -> new BotState())
				.alive = false;
		boolean hasAlive = false;
		for (BotState bot : bots.values()) {
			if (bot.alive) {
				hasAlive = true;
				break;
			}
		}
		if (!hasAlive)
			printState();
	}

	private void printState() {
		System.out.print("\033[H\033[2J");
		if (!Strings.isNullOrEmpty(matchId))
			System.out.println("| MATCH " + matchId );
		System.out.println("| ROUND " + round );
		List<Integer> botKeys = new ArrayList<>(bots.keySet());
		botKeys.sort(Comparator.naturalOrder());
		for (Integer botKey : botKeys) {
			BotState bot = bots.get(botKey);
			System.out.print("| " + botKey);
			if (!Strings.isNullOrEmpty(bot.name))
				System.out.print(" " + bot.name);
			if (bot.alive)
				System.out.print(": " + bot.numCoins);
			if (!bot.alive)
				System.out.print(": X_X");
			System.out.print(" ");
		}
		System.out.println();
		System.out.println();

		int viewRadius2 = map.getViewRadius() * map.getViewRadius();
		int miningRadius2 = map.getMiningRadius() * map.getMiningRadius();
		int attackRadius2 = map.getAttackRadius() * map.getAttackRadius();
		for (int y = map.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < map.getWidth(); x++) {
				Point p = new Point(x, y);
				char c = FREE;
				if (map.isBlocked(p)) {
					c = BLOCK;
				} else {
					if (coins.contains(p)) {
						c = COIN;
					} else {
						boolean isBot = false;
						boolean inViewRadius = false;
						boolean inMiningRadius = false;
						boolean inAttackRadius = false;
						for (Map.Entry<Integer, BotState> entry : bots.entrySet()) {
							BotState bot = entry.getValue();
							if (!bot.alive || bot.position == null)
								continue;
							if (bot.position.equals(p)) {
								isBot = true;
								c = entry.getKey().toString().charAt(0);
								break;
							}
							int d2 = bot.position.offsetTo(p, map.getSize()).length2();
							inViewRadius |= d2 <= viewRadius2;
							inMiningRadius |= d2 <= miningRadius2;
							inAttackRadius |= d2 <= attackRadius2;
						}
						if (!isBot) {
							if (inMiningRadius)
								c = MINING_MASK;
							else if (inAttackRadius)
								c = ATTACK_MASK;
							else if (inViewRadius)
								c = VIEW_MASK;
						}
					}
				}
				System.out.print(c);
			}
			System.out.println();
		}
		System.out.println();
	}
}
