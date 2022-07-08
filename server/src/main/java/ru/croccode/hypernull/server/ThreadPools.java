package ru.croccode.hypernull.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadPools {

	private static final int MAX_PARALLEL_MATCHES = 20;

	private static final ExecutorService DEFAULT_POOL = Executors.newCachedThreadPool();

	private static final ExecutorService MATCH_POOL = Executors.newFixedThreadPool(MAX_PARALLEL_MATCHES);

	private ThreadPools() {
	}

	public static ExecutorService defaultPool() {
		return DEFAULT_POOL;
	}

	public static ExecutorService matchPool() {
		return MATCH_POOL;
	}

	public static void shutdownAll() {
		MATCH_POOL.shutdownNow();
		DEFAULT_POOL.shutdownNow();
	}
}
