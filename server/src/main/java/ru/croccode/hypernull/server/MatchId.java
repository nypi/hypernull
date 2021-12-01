package ru.croccode.hypernull.server;

import java.util.concurrent.atomic.AtomicInteger;

public final class MatchId {

	private final static AtomicInteger COUNTER = new AtomicInteger(0);

	private MatchId() {
	}

	public static String nextId() {
		// format: current time (epoch millis)
		// + 2 chars of sync match counter reminder
		// separated by underscore
		int n = 'z' - 'a' + 1;
		int mod = n * n;
		int k = COUNTER.getAndIncrement() % mod;
		if (k < 0)
			k += mod;
		long now = System.currentTimeMillis();
		return now
				+ "_"
				+ (char)('a' + (k / n))
				+ (char)('a' + (k % n));
	}
}
