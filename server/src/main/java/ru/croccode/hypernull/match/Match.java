package ru.croccode.hypernull.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.ToIntFunction;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.geometry.Offset;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Strings;

// K - bot key type
public class Match<K> {

	private static final int MOVE_DX_LIMIT = 1;
	private static final int MOVE_DY_LIMIT = 1;

	private final Random rnd;

	private final String id;

	private final MatchConfig config;

	private final MatchMap map;

	private final MapIndex mapIndex;

	// bot states
	private final Map<K, Bot<K>> bots = new HashMap<>();

	// active coins
	private final Set<Point> coins = new HashSet<>();

	private final List<MatchListener<K>> listeners = new ArrayList<>();

	// current round, starting from 1
	private int round;

	public Match(String id, MatchMap map, MatchConfig config, Map<K, String> botNames) {
		this(id, map, config, botNames, Collections.emptyList());
	}

	public Match(String id, MatchMap map, MatchConfig config, Map<K, String> botNames,
			List<MatchListener<K>> listeners) {
		Check.notNull(map);
		Check.notNull(config);
		Check.condition(!botNames.isEmpty());

		this.rnd = new Random(config.getRandomSeed());
		this.id = Strings.emptyToNull(id);
		this.config = config;
		this.map = map;
		this.mapIndex = new MapIndex(map);
		if (listeners != null)
			this.listeners.addAll(listeners);

		// notify: match started
		this.listeners.forEach(l -> l.matchStarted(id, map, config, botNames));
		// init bots
		initBots(botNames);
		// spawn initial coins
		spawnCoins(config.getCoinSpawnVolume());
		// ready for round 1
		round = 1;
		// notify: round
		this.listeners.forEach(l -> l.matchRound(round));
	}

	public String getId() {
		return id;
	}

	public MatchConfig getConfig() {
		return config;
	}

	public MatchMap getMap() {
		return map;
	}

	public Map<K, Bot<K>> getBots() {
		return Collections.unmodifiableMap(bots);
	}

	public Set<Point> getCoins() {
		return Collections.unmodifiableSet(coins);
	}

	public int getRound() {
		return round;
	}

	public boolean isActive() {
		// check: round <= num_rounds
		if (round > config.getNumRounds())
			return false;
		// check: at least one bot is active
		for (Bot<K> bot : bots.values()) {
			if (bot.isActive())
				return true;
		}
		return false;
	}

	public boolean isActive(K botKey) {
		Check.notNull(botKey);
		Bot<K> bot = bots.get(botKey);
		return bot != null && bot.isActive();
	}

	private void initBots(Map<K, String> botNames) {
		List<Point> spawnPositions = map.getSpawnPositions();
		Check.condition(botNames.size() <= spawnPositions.size());

		int i = 0;
		for (Map.Entry<K, String> entry : botNames.entrySet()) {
			Bot<K> bot = new Bot<>(
					entry.getKey(),
					entry.getValue(),
					spawnPositions.get(i++));
			bots.put(bot.getKey(), bot);
			listeners.forEach(l -> l.botSpawned(bot.getKey(), bot.getPosition()));
			listeners.forEach(l -> l.botCoinsChanged(bot.getKey(), bot.getNumCoins()));
		}
	}

	private void spawnCoins(int numCoins) {
		Set<Point> exclude = new HashSet<>(coins);
		for (Bot<K> bot : bots.values()) {
			if (bot.isActive())
				exclude.add(bot.getPosition());
		}
		Point reflect = null;
		for (int i = 0; i < numCoins; i++) {
			Point coin = reflect != null
					? reflect.reflectXY(map.getSize())
					: new Point(rnd.nextInt(map.getWidth()), rnd.nextInt(map.getHeight()));
			coin = mapIndex.nearestFree(coin, exclude);
			if (coin == null)
				break;
			coins.add(coin);
			final Point coinRef = coin;
			listeners.forEach(l -> l.coinSpawned(coinRef));
			exclude.add(coin);
			reflect = i % 2 == 0 ? coin : null;
		}
	}

	public Set<K> getActiveBotKeys() {
		Set<K> activeBotKeys = new HashSet<>(bots.size());
		for (Bot<K> bot : bots.values()) {
			if (bot.isActive())
				activeBotKeys.add(bot.getKey());
		}
		return activeBotKeys;
	}

	public Set<Point> getVisibleBlocks(K botKey) {
		Check.notNull(botKey);
		Bot<K> bot = bots.get(botKey);
		if (bot == null || !bot.isActive())
			return Collections.emptySet();

		return mapIndex.blockedInRange(bot.getPosition(), map.getViewRadius());
	}

	public Set<Point> getVisibleCoins(K botKey) {
		Check.notNull(botKey);
		Bot<K> bot = bots.get(botKey);
		if (bot == null || !bot.isActive())
			return Collections.emptySet();

		Set<Point> visibleCoins = new HashSet<>();
		int r2 = map.getViewRadius() * map.getViewRadius();
		for (Point coin : coins) {
			int d2 = bot.getPosition().offsetTo(coin, map.getSize()).length2();
			if (d2 <= r2)
				visibleCoins.add(coin);
		}
		return visibleCoins;
	}

	public Set<Bot<K>> getVisibleBots(K botKey) {
		Check.notNull(botKey);
		Bot<K> bot = bots.get(botKey);
		if (bot == null || !bot.isActive())
			return Collections.emptySet();

		Set<Bot<K>> visibleBots = new HashSet<>();
		visibleBots.add(bot);

		int r2 = map.getViewRadius() * map.getViewRadius();
		for (Bot<K> target : bots.values()) {
			if (!target.isActive() || Objects.equals(target, bot))
				continue;
			int d2 = bot.getPosition().offsetTo(target.getPosition(), map.getSize()).length2();
			if (d2 <= r2)
				visibleBots.add(target);
		}
		return visibleBots;
	}

	public void deactivateBot(K botKey) {
		Check.notNull(botKey);
		Bot<K> bot = bots.get(botKey);
		if (bot != null && bot.isActive()) {
			bot.deactivate();
			listeners.forEach(l -> l.matchOver(botKey));
		}
	}

	public void completeRound(Map<K, Offset> moves) {
		// move bots
		moveBots(moves);
		// attack
		if (config.getMode() == MatchMode.DEATHMATCH) {
			attack();
		}
		// collect coins
		collectCoins();
		// spawn new coins
		if (round % config.getCoinSpawnPeriod() == 0) {
			spawnCoins(config.getCoinSpawnVolume());
		}
		if (round < config.getNumRounds()) {
			round++;
			listeners.forEach(l -> l.matchRound(round));
		} else {
			for (Bot<K> bot : bots.values()) {
				if (bot.isActive()) {
					bot.deactivate();
					listeners.forEach(l -> l.matchOver(bot.getKey()));
				}
			}
		}
	}

	private static <K> void sortByCoins(List<Bot<K>> bots) {
		if (bots == null)
			return;
		bots.sort(Comparator
				.comparingInt((ToIntFunction<Bot<K>>) Bot::getNumCoins)
				.reversed());
	}

	private void moveBots(Map<K, Offset> moves) {
		// convert bot moves to target positions
		Map<K, Point> targets = resolveMoves(moves);
		// apply targets
		targets.forEach((botKey, position) -> {
			Bot<K> bot = bots.get(botKey);
			Check.notNull(bot); // guaranteed by resolveMoves
			bot.setPosition(position);
			listeners.forEach(l -> l.botMoved(bot.getKey(), bot.getPosition()));
		});
	}

	private Map<K, Point> resolveMoves(Map<K, Offset> moves) {
		if (moves == null)
			return Collections.emptyMap();

		Map<K, Point> targets = new HashMap<>(moves.size());
		Map<Point, List<K>> collisions = new HashMap<>(moves.size());
		for (Map.Entry<K, Offset> entry : moves.entrySet()) {
			Offset offset = entry.getValue();
			if (offset == null || offset.length2() == 0)
				continue; // no move

			K botKey = entry.getKey();
			Bot<K> bot = bots.get(botKey);
			if (bot == null)
				continue; // invalid bot key (error?)
			if (!bot.isActive())
				continue; // bot not active

			offset = limitMoveOffset(offset);
			Point target = bot.getPosition().apply(offset, map.getSize());
			// check: target not blocked
			if (map.isBlocked(target))
				continue;
			targets.put(botKey, target);
			// update candidates list for the target position
			List<K> botKeys = collisions.computeIfAbsent(target,
					k -> new ArrayList<>(1));
			botKeys.add(botKey);
		}
		// resolve possible collisions
		collisions.forEach((target, botKeys) -> {
			// if there are two or more bots aiming
			// for the same target point, remove moves
			// for all of them
			if (botKeys.size() > 1)
				botKeys.forEach(targets::remove);
		});
		return targets;
	}

	private Offset limitMoveOffset(Offset offset) {
		int dx = Math.max(-MOVE_DX_LIMIT, Math.min(MOVE_DX_LIMIT, offset.dx()));
		int dy = Math.max(-MOVE_DY_LIMIT, Math.min(MOVE_DY_LIMIT, offset.dy()));
		return new Offset(dx, dy);
	}

	private void attack() {
		List<Bot<K>> botsByWeight = new ArrayList<>(bots.values());
		sortByCoins(botsByWeight);

		// squared attack radius
		int r2 = map.getAttackRadius() * map.getAttackRadius();
		for (int i = 0; i < botsByWeight.size(); i++) {
			Bot<K> attacking = botsByWeight.get(i);
			if (!attacking.isActive())
				continue;
			for (int j = i + 1; j < botsByWeight.size(); j++) {
				Bot<K> defending = botsByWeight.get(j);
				if (!defending.isActive())
					continue;

				// squared distance between bots
				int d2 = attacking.getPosition()
						.offsetTo(defending.getPosition(), map.getSize())
						.length2();
				if (d2 <= r2) {
					// attack
					listeners.forEach(l -> l.attack(attacking.getKey(), defending.getKey()));
					int winCoins = defending.getNumCoins();
					defending.resetCoins();
					attacking.addCoins(winCoins);
					listeners.forEach(l -> l.botCoinsChanged(defending.getKey(), defending.getNumCoins()));
					listeners.forEach(l -> l.botCoinsChanged(attacking.getKey(), attacking.getNumCoins()));
					defending.deactivate();
					listeners.forEach(l -> l.matchOver(defending.getKey()));
				}
			}
		}
	}

	private void collectCoins() {
		int r2 = map.getMiningRadius() * map.getMiningRadius();
		Iterator<Point> it = coins.iterator();
		while (it.hasNext()) {
			Point coin = it.next();
			List<Bot<K>> candidates = new ArrayList<>();
			for (Bot<K> bot : bots.values()) {
				if (!bot.isActive())
					continue;
				int d2 = bot.getPosition()
						.offsetTo(coin, map.getSize())
						.length2();
				if (d2 <= r2)
					candidates.add(bot);
			}
			if (!candidates.isEmpty()) {
				if (candidates.size() > 1)
					sortByCoins(candidates);
				// pickup coin
				Bot<K> looter = candidates.get(0);
				it.remove();
				listeners.forEach(l -> l.coinCollected(coin, looter.getKey()));
				looter.addCoins(1);
				listeners.forEach(l -> l.botCoinsChanged(looter.getKey(), looter.getNumCoins()));
			}
		}
	}
}
