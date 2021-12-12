package ru.croccode.hypernull.player.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PlaybackModel {

	private final SimpleIntegerProperty round = new SimpleIntegerProperty(0);

	private final SimpleIntegerProperty numRounds = new SimpleIntegerProperty(0);

	private final SimpleBooleanProperty paused = new SimpleBooleanProperty(true);

	private final SimpleBooleanProperty fog = new SimpleBooleanProperty(false);

	public SimpleIntegerProperty getRound() {
		return round;
	}

	public SimpleIntegerProperty getNumRounds() {
		return numRounds;
	}

	public SimpleBooleanProperty getPaused() {
		return paused;
	}

	public SimpleBooleanProperty getFog() {
		return fog;
	}
}
