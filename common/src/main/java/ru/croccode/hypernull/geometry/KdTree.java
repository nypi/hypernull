package ru.croccode.hypernull.geometry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import ru.croccode.hypernull.util.Check;

public class KdTree<V> {

	private final Node<V> root;
	private final int size;

	private KdTree(Node<V> root, int size) {
		this.root = root;
		this.size = size;
	}

	public static <V> Builder<V> builder() {
		return new Builder<>();
	}

	private static int compare(Point p1, Point p2, int level) {
		int k = level % 2;
		return k == 0 ? Integer.compare(p1.x(), p2.x()) : Integer.compare(p1.y(), p2.y());
	}

	private static int axisDistance2(Point p1, Point p2, int level) {
		int k = level % 2;
		int d = k == 0 ? p1.x() - p2.x() : p1.y() - p2.y();
		return d * d;
	}

	private static int distance2(Point p1, Point p2) {
		int dx = p1.x() - p2.x();
		int dy = p1.y() - p2.y();
		return dx * dx + dy * dy;
	}

	public int size() {
		return size;
	}

	public Neighbor<V> nearestNeighbor(Point point, Set<V> exclude) {
		Check.notNull(point);
		if (root == null)
			return null;

		Neighbor<V> nearest = new Neighbor<>();
		nearest.distance2 = Integer.MAX_VALUE;
		NearestIterator it = new NearestIterator(point, exclude, nearest.distance2);
		while (it.hasNext()) {
			Node<V> x = it.next();
			int distance2 = distance2(point, x.point);
			if (distance2 < nearest.distance2) {
				nearest.distance2 = distance2;
				nearest.point = x.point;
				nearest.value = x.value;
				it.updateWorst2(distance2);
			}
		}
		return nearest;
	}

	public List<Neighbor<V>> kNearestNeighbors(Point point, int k, Set<V> exclude) {
		Check.notNull(point);
		Check.condition(k > 0);
		if (root == null)
			return Collections.emptyList();

		NeighborComparator<V> comparator = new NeighborComparator<>();
		List<Neighbor<V>> neighbors = new ArrayList<>(k);
		NearestIterator it = new NearestIterator(point, exclude);
		while (it.hasNext()) {
			Node<V> x = it.next();
			int distance2 = distance2(point, x.point);
			if (distance2 <= it.worst2()) {
				Neighbor<V> neighbor = new Neighbor<>();
				neighbor.distance2 = distance2;
				int i = Collections.binarySearch(neighbors, neighbor, comparator);
				if (i < 0)
					i = -i - 1;
				if (i < k) { // insert
					neighbor.point = x.point;
					neighbor.value = x.value;
					if (i < neighbors.size()) {
						if (neighbors.size() == k)
							neighbors.remove(k - 1);
						neighbors.add(i, neighbor);
					} else {
						neighbors.add(neighbor);
					}
					if (neighbors.size() == k)
						it.updateWorst2(neighbors.get(k - 1).distance2);
				}
			}
		}
		return neighbors;
	}

	public List<Neighbor<V>> neighborsInRange(Point point, int radius) {
		Check.notNull(point);
		Check.condition(radius >= 0.0);
		if (root == null)
			return Collections.emptyList();

		List<Neighbor<V>> neighbors = new ArrayList<>();
		NearestIterator it = new NearestIterator(point, null, radius * radius);
		while (it.hasNext()) {
			Node<V> x = it.next();
			int distance2 = distance2(point, x.point);
			if (distance2 <= it.worst2()) {
				Neighbor<V> neighbor = new Neighbor<>();
				neighbor.distance2 = distance2;
				neighbor.point = x.point;
				neighbor.value = x.value;
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
	
	/* nearest iterator */

	static class NodeHolder<V> {

		final Node<V> node;

		final int level;

		final int parentAxisDistance2;

		NodeHolder(Node<V> node, int level) {
			this.node = node;
			this.level = level;
			this.parentAxisDistance2 = -1;
		}

		NodeHolder(Node<V> node, int level, int parentAxisDistance2) {
			this.node = node;
			this.level = level;
			this.parentAxisDistance2 = parentAxisDistance2;
		}
	}

	private class NearestIterator implements Iterator<Node<V>> {

		private final Point point;

		private final Set<V> exclude;

		private int worst2;

		private final Deque<NodeHolder<V>> stack;

		private Node<V> next;

		public NearestIterator(Point point) {
			this(point, null, Integer.MAX_VALUE);
		}

		public NearestIterator(Point point, Set<V> exclude) {
			this(point, exclude, Integer.MAX_VALUE);
		}

		public NearestIterator(Point point, Set<V> exclude, int worst2) {
			Check.notNull(point);

			this.point = point;
			this.exclude = exclude;
			this.worst2 = worst2;
			this.stack = new ArrayDeque<>();
			if (root != null)
				this.stack.addFirst(new NodeHolder<>(root, 0));
			next = nextMatch();
		}

		public int worst2() {
			return worst2;
		}

		public void updateWorst2(int worst2) {
			this.worst2 = worst2;
		}

		private Node<V> nextMatch() {
			while (!stack.isEmpty()) {
				NodeHolder<V> holder = stack.removeFirst();
				// cut by the parent axis distance
				if (holder.parentAxisDistance2 != -1 && holder.parentAxisDistance2 > worst2)
					continue;

				Node<V> x = holder.node;
				Node<V> next;
				Node<V> opposite;
				int level = holder.level;
				int cmp = compare(point, x.point, level);
				if (cmp < 0) {
					next = x.left;
					opposite = x.right;
				} else {
					next = x.right;
					opposite = x.left;
				}
				if (opposite != null) {
					if (next == null) {
						stack.addFirst(new NodeHolder<>(opposite, level + 1));
					} else {
						int axisDistance2 = axisDistance2(point, x.point, level);
						if (axisDistance2 <= worst2)
							stack.addFirst(new NodeHolder<>(opposite, level + 1, axisDistance2));
					}
				}
				if (next != null)
					stack.addFirst(new NodeHolder<>(next, level + 1));

				// match
				if (distance2(point, x.point) <= worst2) {
					if (exclude == null || !exclude.contains(x.value))
						return x;
				}
			}
			return null;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Node<V> next() {
			Node<V> e = next;
			if (e == null)
				throw new NoSuchElementException();
			next = nextMatch();
			return e;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/* nearest iterator (end) */

	static class Node<V> {

		Point point;

		V value;

		Node<V> left;

		Node<V> right;
	}

	public static class Neighbor<V> {

		private Point point;

		private V value;

		private int distance2;

		public Point point() {
			return point;
		}

		public V value() {
			return value;
		}

		public int distance2() {
			return distance2;
		}
	}

	public static class NeighborComparator<V> implements Comparator<Neighbor<V>> {

		@Override
		public int compare(Neighbor<V> x, Neighbor<V> y) {
			return Integer.compare(x.distance2, y.distance2);
		}
	}

	public static class Builder<V> {

		private final List<Node<V>> nodes = new ArrayList<>();

		public Builder<V> add(Point point, V value) {
			Check.notNull(point);

			Node<V> node = new Node<>();
			node.point = point;
			node.value = value;
			nodes.add(node);
			return this;
		}

		public KdTree<V> build() {
			Node<V> root = !nodes.isEmpty()
					? buildTree(nodes, 0, nodes.size(), 0)
					: null;
			return new KdTree<>(root, nodes.size());
		}

		private static <V> Node<V> buildTree(List<Node<V>> nodes, int from, int to, int level) {
			int k = medianSelect(nodes, from, to, level);
			Node<V> node = nodes.get(k);
			// left sub-tree
			int left = k - from;
			if (left > 0)
				node.left = buildTree(nodes, from, k, level + 1);
			else
				node.left = null;
			// right sub-tree
			int right = to - k - 1;
			if (right > 0)
				node.right = buildTree(nodes, k + 1, to, level + 1);
			else
				node.right = null;
			return node;
		}

		private static <V> int medianSelect(List<Node<V>> values, int from, int to, int level) {
			// split [from, to) array into the two equal by size parts
			// and return index of the median;
			// all values from left are less and values from right are
			// greater then the median value;
			// items comparison is level-based

			int n = from + (to - from) / 2;
			while (from < to) {
				if (from == to - 1)
					return from;
				int i = partition(values, from, to, level);
				if (i == n)
					return n;
				if (n < i)
					to = i;
				else
					from = i + 1;
			}
			assert (false);
			return -1;
		}

		private static <V> int partition(List<Node<V>> nodes, int from, int to, int level) {
			assert (from < to);

			Point pivot = nodes.get(to - 1).point; // last element is a pivot
			int i = from;
			for (int j = from; j < to - 1; ++j) {
				Point l = nodes.get(j).point;
				if (compare(l, pivot, level) < 0) {
					// swap will not occur for sorted sequence
					Collections.swap(nodes, i, j);
					i++;
				}
			}
			// place pivot in the middle
			Collections.swap(nodes, i, to - 1);
			return i;
		}
	}
}
