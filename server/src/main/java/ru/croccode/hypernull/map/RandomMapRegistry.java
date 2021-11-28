package ru.croccode.hypernull.map;

import ru.croccode.hypernull.domain.MatchMap;

public class RandomMapRegistry implements MapRegistry {

	@Override
	public MatchMap randomMap(int numBots) {
		return new RandomMap(numBots);
	}
}
