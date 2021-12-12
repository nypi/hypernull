package ru.croccode.hypernull.map;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.util.Check;

public class MapWriter implements Closeable {

	private final Writer w;

	public MapWriter(Writer w) {
		Check.notNull(w);
		this.w = w;
	}

	public void write(MatchMap map) throws IOException {
		Check.notNull(map);
		writeParameters("map_size", map.getWidth(), map.getHeight());
		writeParameters("view_radius", map.getViewRadius());
		writeParameters("mining_radius", map.getMiningRadius());
		writeParameters("attack_radius", map.getAttackRadius());
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				if (map.isBlocked(x, y)) {
					writeParameters("block", x, y);
				}
			}
		}
		for (Point point : map.getSpawnPositions()) {
			writeParameters("spawn_position", point.x(), point.y());
		}
	}

	private void writeParameters(String name, Object... values) throws IOException {
		Check.notEmpty(name);
		w.write(name);
		if (values != null) {
			for (Object value : values) {
				w.write(" ");
				w.write(String.valueOf(value));
			}
		}
		w.write("\n");
	}

	@Override
	public void close() throws IOException {
		w.close();
	}
}
