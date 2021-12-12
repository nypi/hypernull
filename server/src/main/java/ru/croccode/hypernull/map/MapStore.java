package ru.croccode.hypernull.map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.util.Check;

public class MapStore implements MapRegistry {

	private static final Random rnd = new Random(System.currentTimeMillis());

	// key: num of spawn positions on a map
	private final Map<Integer, List<MatchMap>> maps = new HashMap<>();

	@Override
	public MatchMap randomMap(int numBots) {
		Check.condition(numBots > 0);
		List<Integer> keys = maps.keySet()
				.stream()
				.filter(k -> k >= numBots)
				.collect(Collectors.toList());
		if (keys.isEmpty())
			return null;
		int rndKey = keys.get(rnd.nextInt(keys.size()));
		List<MatchMap> values = maps.get(rndKey);
		return values.get(rnd.nextInt(values.size()));
	}

	private void add(MatchMap map) {
		Check.notNull(map);
		int numBots = map.getSpawnPositions().size();
		maps.computeIfAbsent(numBots, k -> new ArrayList<>())
				.add(map);
	}

	public static MapStore load(Path path) throws IOException {
		Check.notNull(path);
		MapStore store = new MapStore();
		if (Files.exists(path)) {
			Files.walk(path)
					.filter(Files::isRegularFile)
					.filter(p -> p.toString().toLowerCase().endsWith(".map"))
					.forEach(p -> {
						try (MapReader r = new MapReader(Files.newBufferedReader(p))) {
							System.out.println("Loading map " + p);
							store.add(r.readMap());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
		}
		return store;
	}
}
