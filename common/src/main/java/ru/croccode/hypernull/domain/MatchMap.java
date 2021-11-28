package ru.croccode.hypernull.domain;

import java.util.List;

import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;

public interface MatchMap {

	int DEFAULT_VIEW_RADIUS = 6;
	int DEFAULT_MINING_RADIUS = 2;
	int DEFAULT_ATTACK_RADIUS = 4;

	Size getSize();

	default int getWidth() {
		return getSize().width();
	}

	default int getHeight() {
		return getSize().height();
	}

	boolean isBlocked(Point point);

	default boolean isBlocked(int x, int y) {
		return isBlocked(new Point(x, y));
	}

	default int getViewRadius() {
		return DEFAULT_VIEW_RADIUS;
	}

	default int getMiningRadius() {
		return DEFAULT_MINING_RADIUS;
	}

	default int getAttackRadius() {
		return DEFAULT_ATTACK_RADIUS;
	}

	List<Point> getSpawnPositions();
}
