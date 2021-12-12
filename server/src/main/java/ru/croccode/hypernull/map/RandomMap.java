package ru.croccode.hypernull.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Offset;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;

public class RandomMap implements MatchMap {

	private static final int MIN_WIDTH = 48;
	private static final int MAX_WIDTH = 80;
	private static final int MIN_HEIGHT = 20;
	private static final int MAX_HEIGHT = 40;

	private static final int MIN_BLOCKS = 12;
	private static final int MAX_BLOCKS = 32;
	private static final int MIN_BLOCK_WIDTH = 2;
	private static final int MAX_BLOCK_WIDTH = 8;
	private static final int MIN_BLOCK_HEIGHT = 2;
	private static final int MAX_BLOCK_HEIGHT = 8;

	private static final int MIN_VIEW_RADIUS = 8;
	private static final int MAX_VIEW_RADIUS = 15;
	private static final int MIN_ATTACK_RADIUS = 3;
	private static final int MAX_ATTACK_RADIUS = 5;
	private static final int MIN_MINING_RADIUS = 1;
	private static final int MAX_MINING_RADIUS = 3;

	private static final Random rnd = new Random(System.currentTimeMillis());

	private final Set<Point> blocked = new HashSet<>();

	private final Size size;

	private final int viewRadius;

	private final int miningRadius;

	private final int attackRadius;

	private final List<Point> spawnPositions;

	public RandomMap(int numSpawnPositions) {
		size = new Size(
				MIN_WIDTH + rnd.nextInt(MAX_WIDTH - MIN_WIDTH),
				MIN_HEIGHT + rnd.nextInt(MAX_HEIGHT - MIN_HEIGHT)
		);
		viewRadius = MIN_VIEW_RADIUS + rnd.nextInt(MAX_VIEW_RADIUS - MIN_VIEW_RADIUS);
		attackRadius = Math.min(viewRadius - 1,
				MIN_ATTACK_RADIUS + rnd.nextInt(MAX_ATTACK_RADIUS - MIN_ATTACK_RADIUS));
		miningRadius = Math.min(attackRadius - 1,
				MIN_MINING_RADIUS + rnd.nextInt(MAX_MINING_RADIUS - MIN_MINING_RADIUS));
		int n = MIN_BLOCKS + rnd.nextInt(MAX_BLOCKS - MIN_BLOCKS);
		for (int i = 0; i < n; i++) {
			Point rectPoint = new Point(
					rnd.nextInt(size.width()),
					rnd.nextInt(size.height())
			);
			Size rectSize = new Size(
					MIN_BLOCK_WIDTH + rnd.nextInt(MAX_BLOCK_WIDTH - MIN_BLOCK_WIDTH),
					MIN_BLOCK_HEIGHT + rnd.nextInt(MAX_BLOCK_HEIGHT - MIN_BLOCK_HEIGHT)
			);
			blockRect(rectPoint, rectSize);
		}
		spawnPositions = new ArrayList<>();
		generate:
		while (spawnPositions.size() < numSpawnPositions) {
			Point point = new Point(
					rnd.nextInt(size.width()),
					rnd.nextInt(size.height())
			);
			if (!blocked.contains(point)) {
				int r2 = attackRadius * attackRadius;
				for (Point generated : spawnPositions) {
					int d2 = point.offsetTo(generated, size).length2();
					if (d2 <= r2)
						continue generate;
				}
				spawnPositions.add(point);
			}
		}
	}

	private void blockRect(Point point, Size size) {
		for (int dx = 0; dx < size.width(); dx++) {
			for (int dy = 0; dy < size.height(); dy++) {
				blocked.add(point.apply(new Offset(dx, dy), this.size));
			}
		}
	}

	@Override
	public Size getSize() {
		return size;
	}

	@Override
	public boolean isBlocked(Point point) {
		return blocked.contains(point);
	}

	@Override
	public int getViewRadius() {
		return viewRadius;
	}

	@Override
	public int getMiningRadius() {
		return miningRadius;
	}

	@Override
	public int getAttackRadius() {
		return attackRadius;
	}

	@Override
	public List<Point> getSpawnPositions() {
		return spawnPositions;
	}
}
