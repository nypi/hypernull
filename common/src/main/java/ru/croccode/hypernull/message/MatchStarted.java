package ru.croccode.hypernull.message;

import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.domain.MatchMode;

public class MatchStarted extends Message {

	private String matchId;

	private Integer numRounds;

	private MatchMode mode;

	private Size mapSize;

	private Integer numBots;

	private Integer yourId;

	private Integer viewRadius;

	private Integer miningRadius;

	private Integer attackRadius;

	private Long moveTimeLimit;

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public Integer getNumRounds() {
		return numRounds;
	}

	public void setNumRounds(Integer numRounds) {
		this.numRounds = numRounds;
	}

	public MatchMode getMode() {
		return mode;
	}

	public void setMode(MatchMode mode) {
		this.mode = mode;
	}

	public Size getMapSize() {
		return mapSize;
	}

	public void setMapSize(Size mapSize) {
		this.mapSize = mapSize;
	}

	public Integer getNumBots() {
		return numBots;
	}

	public void setNumBots(Integer numBots) {
		this.numBots = numBots;
	}

	public Integer getYourId() {
		return yourId;
	}

	public void setYourId(Integer yourId) {
		this.yourId = yourId;
	}

	public Integer getViewRadius() {
		return viewRadius;
	}

	public void setViewRadius(Integer viewRadius) {
		this.viewRadius = viewRadius;
	}

	public Integer getMiningRadius() {
		return miningRadius;
	}

	public void setMiningRadius(Integer miningRadius) {
		this.miningRadius = miningRadius;
	}

	public Integer getAttackRadius() {
		return attackRadius;
	}

	public void setAttackRadius(Integer attackRadius) {
		this.attackRadius = attackRadius;
	}

	public Long getMoveTimeLimit() {
		return moveTimeLimit;
	}

	public void setMoveTimeLimit(Long moveTimeLimit) {
		this.moveTimeLimit = moveTimeLimit;
	}
}
