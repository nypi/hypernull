package ru.croccode.hypernull.player.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;

public class MatchLog {

	private String matchId;

	private int numBots;

	private MatchMode mode;

	private int numRounds;

	private Size mapSize;

	private int viewRadius;

	private int miningRadius;

	private int attackRadius;

	private Set<Point> blocks = new HashSet<>();

	private Map<Integer, String> botNames = new HashMap<>();

	private SortedMap<Integer, Round> rounds = new TreeMap<>();

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public int getNumBots() {
		return numBots;
	}

	public void setNumBots(int numBots) {
		this.numBots = numBots;
	}

	public MatchMode getMode() {
		return mode;
	}

	public void setMode(MatchMode mode) {
		this.mode = mode;
	}

	public int getNumRounds() {
		return numRounds;
	}

	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public Size getMapSize() {
		return mapSize;
	}

	public void setMapSize(Size mapSize) {
		this.mapSize = mapSize;
	}

	public int getViewRadius() {
		return viewRadius;
	}

	public void setViewRadius(int viewRadius) {
		this.viewRadius = viewRadius;
	}

	public int getMiningRadius() {
		return miningRadius;
	}

	public void setMiningRadius(int miningRadius) {
		this.miningRadius = miningRadius;
	}

	public int getAttackRadius() {
		return attackRadius;
	}

	public void setAttackRadius(int attackRadius) {
		this.attackRadius = attackRadius;
	}

	public Set<Point> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Point> blocks) {
		this.blocks = blocks;
	}

	public Map<Integer, String> getBotNames() {
		return botNames;
	}

	public void setBotNames(Map<Integer, String> botNames) {
		this.botNames = botNames;
	}

	public SortedMap<Integer, Round> getRounds() {
		return rounds;
	}

	public void setRounds(SortedMap<Integer, Round> rounds) {
		this.rounds = rounds;
	}

	public static class Round {

		private int round;

		private Map<Integer, Point> bots = new HashMap<>();

		private Map<Integer, Integer> botCoins = new HashMap<>();

		private Set<Integer> deadBots = new HashSet<>();

		private Set<Point> coins = new HashSet<>();

		private Set<Point> collectedCoins = new HashSet<>();

		public int getRound() {
			return round;
		}

		public void setRound(int round) {
			this.round = round;
		}

		public Map<Integer, Point> getBots() {
			return bots;
		}

		public void setBots(Map<Integer, Point> bots) {
			this.bots = bots;
		}

		public Map<Integer, Integer> getBotCoins() {
			return botCoins;
		}

		public Set<Integer> getDeadBots() {
			return deadBots;
		}

		public void setDeadBots(Set<Integer> deadBots) {
			this.deadBots = deadBots;
		}

		public void setBotCoins(Map<Integer, Integer> botCoins) {
			this.botCoins = botCoins;
		}

		public Set<Point> getCoins() {
			return coins;
		}

		public void setCoins(Set<Point> coins) {
			this.coins = coins;
		}

		public Set<Point> getCollectedCoins() {
			return collectedCoins;
		}

		public void setCollectedCoins(Set<Point> collectedCoins) {
			this.collectedCoins = collectedCoins;
		}
	}
}
