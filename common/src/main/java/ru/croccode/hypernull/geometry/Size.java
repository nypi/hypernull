package ru.croccode.hypernull.geometry;

import ru.croccode.hypernull.util.Check;

public class Size {

	private final int width;
	private final int height;

	public Size(int width, int height) {
		Check.condition(width >= 0);
		Check.condition(height >= 0);

		this.width = width;
		this.height = height;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}
}
