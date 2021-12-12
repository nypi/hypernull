package ru.croccode.hypernull.player.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ru.croccode.hypernull.player.Styles;
import ru.croccode.hypernull.player.model.PlaybackModel;
import ru.croccode.hypernull.player.model.MatchModel;
import ru.croccode.hypernull.util.Check;

public class PlaybackView extends HBox {

	private static final String PLAY_TEXT = "\uF034"; // webdings
	private static final String PAUSE_TEXT = "\uF03B"; // webdings

	private final MatchModel matchModel;
	private final PlaybackModel playbackModel;

	// controls
	private final Button playPause;
	private final Slider slider;
	private final Label status;
	private final CheckBox fog;

	public PlaybackView(MatchModel matchModel, PlaybackModel playbackModel) {
		Check.notNull(matchModel);
		Check.notNull(playbackModel);
		this.matchModel = matchModel;
		this.playbackModel = playbackModel;

		// play/pause button
		playPause = new Button();
		playPause.setBackground(null);
		playPause.setMinWidth(50);
		playPause.setAlignment(Pos.CENTER);
		playPause.setFont(Styles.WEBDINGS_FONT);
		playPause.setTextFill(Styles.TEXT_COLOR);
		playPause.setOnAction(e -> {
			Platform.runLater(this::togglePause);
		});

		// slider
		slider = new Slider();
		slider.setId("playback-slider");
		slider.setMin(0);
		slider.setValue(0);
		slider.setBlockIncrement(1);
		slider.setShowTickMarks(false);
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> {
				int value = (int)Math.round(slider.getValue());
				slider.setValue(value);
				playbackModel.getRound().set(value);
			});
		});

		// status
		status = new Label();
		status.setMinWidth(80);
		status.setAlignment(Pos.CENTER);
		status.setFont(Styles.DEFAULT_FONT);
		status.setTextFill(Styles.TEXT_COLOR);

		// fog
		fog = new CheckBox("Fog");
		fog.setMinWidth(60);
		fog.setFont(Styles.DEFAULT_FONT);
		fog.setTextFill(Styles.TEXT_COLOR);
		fog.selectedProperty().bindBidirectional(playbackModel.getFog());

		// listeners
		playbackModel.getPaused().addListener((observable, oldValue, newValue)
				-> Platform.runLater(this::onPausedChanged));
		playbackModel.getNumRounds().addListener((observable, oldValue, newValue)
				-> Platform.runLater(this::onNumRoundsChanged));
		playbackModel.getRound().addListener((observable, oldValue, newValue)
				-> Platform.runLater(this::onRoundChanged));

		// initial callbacks
		onPausedChanged();
		onNumRoundsChanged();
		onRoundChanged();

		// hbox
		setAlignment(Pos.CENTER);
		setFillHeight(true);
		getChildren().add(playPause);
		getChildren().add(slider);
		getChildren().add(status);
		getChildren().add(fog);
		HBox.setHgrow(slider, Priority.ALWAYS);
	}

	private void togglePause() {
		boolean paused = playbackModel.getPaused().get();
		if (paused) {
			int round = playbackModel.getRound().get();
			int numRounds = playbackModel.getNumRounds().get();
			if (round >= numRounds)
				playbackModel.getRound().set(0);
		}
		playbackModel.getPaused().set(!paused);
	}

	private void onPausedChanged() {
		boolean paused = playbackModel.getPaused().get();
		playPause.setText(paused ? PLAY_TEXT : PAUSE_TEXT);
	}

	private void onNumRoundsChanged() {
		int round = playbackModel.getRound().get();
		int numRounds = playbackModel.getNumRounds().get();
		slider.setMax(numRounds);
		status.setText(round + " / " + numRounds);
	}

	private void onRoundChanged() {
		int round = playbackModel.getRound().get();
		int numRounds = playbackModel.getNumRounds().get();
		int value = (int)Math.round(slider.getValue());
		if (value != round)
			slider.setValue(round);
		status.setText(round + " / " + numRounds);
	}
}
