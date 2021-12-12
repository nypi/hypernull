package ru.croccode.hypernull.match;

import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.util.Check;

public class MatchConfig {

	private final MatchMode mode;

	private final int numRounds;

	private final long randomSeed;

	private final long moveTimeLimit;

	private final int coinSpawnPeriod;

	private final int coinSpawnVolume;

	private MatchConfig(Builder builder) {
		Check.notNull(builder);

		this.randomSeed = builder.randomSeed;
		this.numRounds = builder.numRounds;
		this.mode = builder.mode;
		this.moveTimeLimit = builder.moveTimeLimit;
		this.coinSpawnPeriod = builder.coinSpawnPeriod;
		this.coinSpawnVolume = builder.coinSpawnVolume;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public int getNumRounds() {
		return numRounds;
	}

	public MatchMode getMode() {
		return mode;
	}

	public long getMoveTimeLimit() {
		return moveTimeLimit;
	}

	public int getCoinSpawnPeriod() {
		return coinSpawnPeriod;
	}

	public int getCoinSpawnVolume() {
		return coinSpawnVolume;
	}

	public static class Builder {

		private long randomSeed = System.currentTimeMillis();

		private int numRounds = 500;

		private MatchMode mode = MatchMode.FRIENDLY;

		private long moveTimeLimit = 1000;

		private int coinSpawnPeriod = 3;

		private int coinSpawnVolume = 4;

		private Builder() {
		}

		public Builder setRandomSeed(long randomSeed) {
			this.randomSeed = randomSeed;
			return this;
		}

		public Builder setNumRounds(int numRounds) {
			Check.condition(numRounds > 0);
			this.numRounds = numRounds;
			return this;
		}

		public Builder setMode(MatchMode mode) {
			Check.notNull(mode);
			this.mode = mode;
			return this;
		}

		// 0 - no time limit
		public Builder setMoveTimeLimit(long moveTimeLimit) {
			Check.condition(moveTimeLimit >= 0L);
			this.moveTimeLimit = moveTimeLimit;
			return this;
		}

		public Builder setCoinSpawnPeriod(int coinSpawnPeriod) {
			Check.condition(coinSpawnPeriod > 0);
			this.coinSpawnPeriod = coinSpawnPeriod;
			return this;
		}

		public Builder setCoinSpawnVolume(int coinSpawnVolume) {
			Check.condition(coinSpawnVolume > 0);
			this.coinSpawnVolume = coinSpawnVolume;
			return this;
		}

		public MatchConfig build() {
			return new MatchConfig(this);
		}
	}
}
