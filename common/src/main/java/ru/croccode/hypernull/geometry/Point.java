package ru.croccode.hypernull.geometry;

import java.util.Objects;
import java.util.function.Consumer;

import ru.croccode.hypernull.util.Check;

public class Point {

	private final int x;
	private final int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	private static int absMod(int value, int mod) {
		int r = value % mod;
		return r < 0 ? mod + r : r;
	}

	public Point fit(Size size) {
		Check.notNull(size);
		if (x >= 0 && x < size.width()
				&& y >= 0 && y < size.height())
			return this;
		return new Point(
				absMod(x, size.width()),
				absMod(y, size.height()));
	}

	public Point apply(Offset offset) {
		Check.notNull(offset);
		if (offset.dx() == 0 && offset.dy() == 0)
			return this;
		return new Point(x + offset.dx(), y + offset.dy());
	}

	public Point apply(Offset offset, Size size) {
		return apply(offset).fit(size);
	}

	public Offset offsetTo(Point point, Size size) {
		Check.notNull(point);
		Check.notNull(size);

		int w = size.width();
		int h = size.height();

		Point p1 = fit(size);
		Point p2 = point.fit(size);
		int dx = p2.x - p1.x;
		if (Math.abs(dx) > w - Math.abs(dx))
			dx += p2.x > p1.x ? -w : w;
		int dy = p2.y - p1.y;
		if (Math.abs(dy) > h - Math.abs(dy))
			dy += p2.y > p1.y ? -h : h;
		return new Offset(dx, dy);
	}

	public Point reflectX(Size size) {
		Check.notNull(size);
		return new Point(size.width() - x - 1, y).fit(size);
	}

	public Point reflectY(Size size) {
		Check.notNull(size);
		return new Point(x, size.height() - y - 1).fit(size);
	}

	public Point reflectXY(Size size) {
		Check.notNull(size);
		return new Point(size.width() - x - 1, size.height() - y - 1).fit(size);
	}

	public void forRange(int r, Size size, Consumer<Point> consumer) {
		int r2 = r * r;
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				int d2 = dx * dx + dy * dy;
				if (d2 <= r2) {
					consumer.accept(new Point(x + dx, y + dy));
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Point point = (Point)obj;
		return x == point.x && y == point.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public String toLog() {
		return x + " " + y;
	}
}
