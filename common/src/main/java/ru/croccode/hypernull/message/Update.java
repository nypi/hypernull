package ru.croccode.hypernull.message;

import java.util.Map;
import java.util.Set;

import ru.croccode.hypernull.geometry.Point;

public class Update extends Message {

	private Integer round;

	// bot positions; key - bot id
	private Map<Integer, Point> bots;

	// bot coins; key - bot id
	private Map<Integer, Integer> botCoins;

	// blocked map positions
	private Set<Point> blocks;

	// coin positions
	private Set<Point> coins;

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
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

	public void setBotCoins(Map<Integer, Integer> botCoins) {
		this.botCoins = botCoins;
	}

	public Set<Point> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Point> blocks) {
		this.blocks = blocks;
	}

	public Set<Point> getCoins() {
		return coins;
	}

	public void setCoins(Set<Point> coins) {
		this.coins = coins;
	}
}
