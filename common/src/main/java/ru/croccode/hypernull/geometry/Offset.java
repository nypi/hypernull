package ru.croccode.hypernull.geometry;

public class Offset {

	private final int dx;
	private final int dy;

	public Offset(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public int dx() {
		return dx;
	}

	public int dy() {
		return dy;
	}

	public int length2() {
		return dx * dx + dy * dy;
	}

	public double length() {
		return Math.sqrt(length2());
	}
}
