package ru.croccode.hypernull.match;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.KdTree;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.util.Check;

public class MapIndex {

	private final KdTree<Point> blocked;
	private final KdTree<Point> free;

	public MapIndex(MatchMap map) {
		Check.notNull(map);

		KdTree.Builder<Point> blockedBuilder = KdTree.builder();
		KdTree.Builder<Point> freeBuilder = KdTree.builder();

		int w = map.getWidth();
		int h = map.getHeight();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				KdTree.Builder<Point> target = map.isBlocked(x, y)
						? blockedBuilder
						: freeBuilder;
				Point value = new Point(x, y);
				target.add(new Point(x, y), value);
				int dx = x < w / 2 ? w : -w;
				target.add(new Point(x + dx, y), value);
				int dy = y < h / 2 ? h : -h;
				target.add(new Point(x, y + dy), value);
			}
		}

		this.blocked = blockedBuilder.build();
		this.free = freeBuilder.build();
	}

	public Point nearestFree(Point point, Set<Point> exclude) {
		Check.notNull(point);
		KdTree.Neighbor<Point> nearest = free.nearestNeighbor(point, exclude);
		return nearest != null
				? nearest.value()
				: null; // no free cells
	}

	public Set<Point> blockedInRange(Point point, int radius) {
		Check.notNull(point);
		List<KdTree.Neighbor<Point>> nodes = blocked.neighborsInRange(point, radius);
		Set<Point> points = new HashSet<>(nodes.size());
		for (KdTree.Neighbor<Point> node : nodes)
			points.add(node.value());
		return points;
	}
}
