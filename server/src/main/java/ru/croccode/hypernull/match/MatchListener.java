package ru.croccode.hypernull.match;

import java.util.Map;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Point;

public interface MatchListener<K> {

	void matchStarted(String id, MatchMap map, MatchConfig config, Map<K, String> botNames);

	void matchRound(int round);

	void coinSpawned(Point position);

	void coinCollected(Point position, K botKey);

	void botSpawned(K botKey, Point position);

	void botMoved(K botKey, Point position);

	void attack(K attackingKey, K defendingKey);

	void botCoinsChanged(K botKey, int numCoins);

	void matchOver(K botKey);
}
