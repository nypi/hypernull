package ru.croccode.hypernull.player.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.player.Styles;
import ru.croccode.hypernull.player.model.MatchLog;
import ru.croccode.hypernull.player.model.MatchModel;
import ru.croccode.hypernull.player.model.PlaybackModel;
import ru.croccode.hypernull.util.Check;

public class InfoView extends HBox {

	private final MatchModel matchModel;

	private final PlaybackModel playbackModel;

	public InfoView(MatchModel matchModel, PlaybackModel playbackModel) {
		Check.notNull(matchModel);
		Check.notNull(playbackModel);
		this.matchModel = matchModel;
		this.playbackModel = playbackModel;

		matchModel.getMatch().addListener(((observable, oldValue, newValue)
				-> Platform.runLater(this::update)));
		playbackModel.getRound().addListener(((observable, oldValue, newValue)
				-> Platform.runLater(this::update)));
		update();

		setSpacing(Styles.PADDING_HORIZONTAL);
		setFillHeight(true);
		setAlignment(Pos.CENTER);
	}

	private void update() {
		MatchLog match = matchModel.getMatch().get();
		if (match == null) {
			getChildren().clear();
			Label dropFiles = new Label("Drag and drop match log file(s) to start replay");
			dropFiles.setTextFill(Styles.TEXT_COLOR);
			dropFiles.setFont(Styles.DEFAULT_FONT);
			getChildren().add(dropFiles);
		} else {
			List<Bot> bots = new ArrayList<>();
			int roundNumber = playbackModel.getRound().get();
			MatchLog.Round round = match.getRounds().get(roundNumber);
			if (round != null) {
				match.getBotNames().forEach((k, v) -> {
					Bot bot = new Bot();
					bot.id = k;
					bot.name = v;
					bot.color = Styles.botColor(k);
					bot.numCoins = round.getBotCoins().getOrDefault(k, 0);
					bots.add(bot);
				});
				bots.sort(Comparator
						.comparingInt((ToIntFunction<Bot>)(x -> x.numCoins))
						.reversed());

				getChildren().clear();
				if (match.getMode() == MatchMode.DEATHMATCH) {
					Label deathmatch = new Label("\u2620");
					deathmatch.setTextFill(Styles.TEXT_COLOR);
					deathmatch.setFont(Styles.INFO_FONT);
					getChildren().add(deathmatch);
				}
				for (Bot bot : bots) {
					String score = round.getDeadBots().contains(bot.id)
							? "x_x"
							: Integer.toString(bot.numCoins);
					Label label = new Label(bot.name + " : " + score);
					label.setTextFill(bot.color.desaturate());
					label.setFont(Styles.INFO_FONT);
					getChildren().add(label);
				}
			}
		}
	}

	static class Bot {

		Integer id;

		String name;

		int numCoins;

		Color color;
	}
}
