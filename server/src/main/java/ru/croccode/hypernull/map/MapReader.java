package ru.croccode.hypernull.map;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Strings;

public class MapReader implements Closeable {

	private final Reader r;


	public MapReader(Reader r) {
		Check.notNull(r);
		this.r = r;
	}

	public MatchMap readMap() {
		MapBuilder builder = new MapBuilder();
		Scanner s = new Scanner(r);
		while (s.hasNext()) {
			String line = s.nextLine();
			if (line != null)
				line = line.trim();
			if (Strings.isNullOrEmpty(line) || line.startsWith("#"))
				continue;
			String[] tokens = line.split(" ");
			String key = tokens[0];

			switch (key) {
				case "map_size":
					builder.setSize(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
					break;
				case "view_radius":
					builder.setViewRadius(Integer.parseInt(tokens[1]));
					break;
				case "attack_radius":
					builder.setAttackRadius(Integer.parseInt(tokens[1]));
					break;
				case "mining_radius":
					builder.setMiningRadius(Integer.parseInt(tokens[1]));
					break;
				case "block":
					builder.addBlocked(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
					break;
				case "spawn_position":
					builder.addSpawnPosition(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
					break;
			}
		}
		return builder.build();
	}

	@Override
	public void close() throws IOException {
		r.close();
	}
}
