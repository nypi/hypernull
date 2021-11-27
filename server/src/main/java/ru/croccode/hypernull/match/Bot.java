package ru.croccode.hypernull.match;

import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.util.Check;

// bot state
public class Bot<K> {

	private final K key;

	private boolean active;

	private Point position;

	private int numCoins;

	// package-private
	Bot(K key, Point initPosition) {
		Check.notNull(key);

		this.key = key;
		this.active = true;
		this.position = initPosition;
		this.numCoins = 0;
	}

	public K getKey() {
		return key;
	}

	public boolean isActive() {
		return active;
	}

	void deactivate() {
		active = false;
	}

	public Point getPosition() {
		return position;
	}

	void setPosition(Point position) {
		this.position = position;
	}

	public int getNumCoins() {
		return numCoins;
	}

	void addCoins(int numCoins) {
		this.numCoins += numCoins;
	}

	void resetCoins() {
		numCoins = 0;
	}
}
