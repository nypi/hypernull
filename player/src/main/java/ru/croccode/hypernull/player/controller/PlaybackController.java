package ru.croccode.hypernull.player.controller;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import ru.croccode.hypernull.player.App;
import ru.croccode.hypernull.player.model.MatchLog;
import ru.croccode.hypernull.player.model.MatchModel;
import ru.croccode.hypernull.player.model.PlaybackModel;
import ru.croccode.hypernull.util.Check;

public class PlaybackController {

	private final static long NEXT_ROUND_PERIOD_MILLIS = 200;

	private final MatchModel matchModel;

	private final PlaybackModel playbackModel;

	private ScheduledFuture<?> scheduledNextRound;

	public PlaybackController(MatchModel matchModel, PlaybackModel playbackModel) {
		Check.notNull(matchModel);
		Check.notNull(playbackModel);
		this.matchModel = matchModel;
		this.playbackModel = playbackModel;

		matchModel.getMatch().addListener((observable, oldValue, newValue)
				-> Platform.runLater(this::onMatchChanged));
		playbackModel.getPaused().addListener((observable, oldValue, newValue)
				-> Platform.runLater(this::onPausedChanged));
		onMatchChanged();
		onPausedChanged();
	}

	private void onMatchChanged() {
		MatchLog match = matchModel.getMatch().get();
		if (match == null) {
			playbackModel.getNumRounds().set(0);
			playbackModel.getRound().set(0);
			playbackModel.getPaused().set(true);
		} else {
			playbackModel.getNumRounds().set(match.getNumRounds());
			playbackModel.getRound().set(0);
			playbackModel.getPaused().set(false);
		}
	}

	private void onPausedChanged() {
		boolean paused = playbackModel.getPaused().get();
		if (!paused) {
			if (scheduledNextRound != null)
				scheduledNextRound.cancel(true);
			scheduledNextRound = App.SCHEDULER.scheduleAtFixedRate(
					this::nextRound,
					0L,
					NEXT_ROUND_PERIOD_MILLIS,
					TimeUnit.MILLISECONDS);
		} else {
			if (scheduledNextRound != null) {
				scheduledNextRound.cancel(true);
				scheduledNextRound = null;
			}
		}
	}

	private void nextRound() {
		int round = playbackModel.getRound().get();
		int numRounds = playbackModel.getNumRounds().get();
		if (round >= numRounds) {
			playbackModel.getRound().set(numRounds);
			playbackModel.getPaused().set(true);
			// proceed to a next replay
			matchModel.advance();
		} else {
			playbackModel.getRound().set(round + 1);
		}
	}
}
