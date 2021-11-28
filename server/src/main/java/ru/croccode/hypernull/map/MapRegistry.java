package ru.croccode.hypernull.map;

import ru.croccode.hypernull.domain.MatchMap;

public interface MapRegistry {

	MatchMap randomMap(int numBots);
}
