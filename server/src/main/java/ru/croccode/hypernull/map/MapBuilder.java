package ru.croccode.hypernull.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.util.Check;

public class MapBuilder {

	public static final int DEFAULT_VIEW_RADIUS = 8;

	public static final int DEFAULT_ATTACK_RADIUS = 4;

	public static final int DEFAULT_MINING_RADIUS = 2;

	private Size size;

	private int viewRadius = DEFAULT_VIEW_RADIUS;

	private int attackRadius = DEFAULT_ATTACK_RADIUS;

	private int miningRadius = DEFAULT_MINING_RADIUS;

	private final Set<Point> blocked = new HashSet<>();

	private final List<Point> spawnPositions = new ArrayList<>();

	public MapBuilder setSize(Size size) {
		Check.notNull(size);
		this.size = size;
		return this;
	}

	public MapBuilder setSize(int width, int height) {
		this.size = new Size(width, height);
		return this;
	}

	public MapBuilder setViewRadius(int viewRadius) {
		Check.condition(viewRadius > 0);
		this.viewRadius = viewRadius;
		return this;
	}

	public MapBuilder setAttackRadius(int attackRadius) {
		Check.condition(viewRadius >= 0);
		this.attackRadius = attackRadius;
		return this;
	}

	public MapBuilder setMiningRadius(int miningRadius) {
		Check.condition(viewRadius >= 0);
		this.miningRadius = miningRadius;
		return this;
	}

	public MapBuilder addBlocked(Point point) {
		Check.notNull(point);
		this.blocked.add(point);
		return this;
	}

	public MapBuilder addBlocked(int x, int y) {
		this.blocked.add(new Point(x, y));
		return this;
	}

	public MapBuilder addSpawnPosition(Point point) {
		Check.notNull(point);
		this.spawnPositions.add(point);
		return this;
	}

	public MapBuilder addSpawnPosition(int x, int y) {
		this.spawnPositions.add(new Point(x, y));
		return this;
	}

	public MatchMap build() {
		return new SimpleMap(this);
	}

	static class SimpleMap implements MatchMap {

		private final Size size;

		private final int viewRadius;

		private final int attackRadius;

		private final int miningRadius;

		private final Set<Point> blocked;

		private final List<Point> spawnPositions;

		SimpleMap(MapBuilder builder) {
			Check.notNull(builder);
			Check.notNull(builder.size);

			size = builder.size;
			viewRadius = builder.viewRadius;
			attackRadius = Math.min(viewRadius - 1, builder.attackRadius);
			miningRadius = Math.min(viewRadius - 1, builder.miningRadius);
			blocked = new HashSet<>();
			builder.blocked.forEach(p -> blocked.add(p.fit(size)));
			spawnPositions = new ArrayList<>();
			builder.spawnPositions.forEach(p -> spawnPositions.add(p.fit(size)));
		}

		@Override
		public Size getSize() {
			return size;
		}

		@Override
		public int getViewRadius() {
			return viewRadius;
		}

		@Override
		public int getAttackRadius() {
			return attackRadius;
		}

		@Override
		public int getMiningRadius() {
			return miningRadius;
		}

		@Override
		public boolean isBlocked(Point point) {
			return blocked.contains(point);
		}

		@Override
		public List<Point> getSpawnPositions() {
			return Collections.unmodifiableList(spawnPositions);
		}
	}
}
