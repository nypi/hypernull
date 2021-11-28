package ru.croccode.hypernull.match;

import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.util.Check;

// bot state
public class Bot<K> {

	private final K key;

	private final String name;

	private boolean active;

	private Point position;

	private int numCoins;

	// package-private
	Bot(K key, String name, Point initPosition) {
		Check.notNull(key);

		this.key = key;
		this.name = name;
		this.active = true;
		this.position = initPosition;
		this.numCoins = 0;
	}

	public K getKey() {
		return key;
	}

	public String getName() {
		return name;
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
