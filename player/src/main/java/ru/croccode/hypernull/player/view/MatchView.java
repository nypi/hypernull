package ru.croccode.hypernull.player.view;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.player.Styles;
import ru.croccode.hypernull.player.model.MatchLog;
import ru.croccode.hypernull.player.model.PlaybackModel;
import ru.croccode.hypernull.player.model.MatchModel;
import ru.croccode.hypernull.util.Check;

public class MatchView extends StackPane {

	private final MatchModel matchModel;

	private final PlaybackModel playbackModel;

	private final Canvas canvas;

	public MatchView(MatchModel matchModel, PlaybackModel playbackModel) {
		Check.notNull(matchModel);
		Check.notNull(playbackModel);
		this.matchModel = matchModel;
		this.playbackModel = playbackModel;

		canvas = new Canvas();
		canvas.setWidth(getWidth() - 2 * Styles.CANVAS_PADDING);
		widthProperty().addListener((observable, old, value) -> {
			canvas.setWidth(getWidth() - 2 * Styles.CANVAS_PADDING);
			Platform.runLater(this::render);
		});
		canvas.setHeight(getHeight() - 2 * Styles.CANVAS_PADDING);
		heightProperty().addListener((observable, old, value) -> {
			canvas.setHeight(getHeight() - 2 * Styles.CANVAS_PADDING);
			Platform.runLater(this::render);
		});

		// listeners
		matchModel.getMatch().addListener((observable, old, value)
				-> Platform.runLater(this::render));
		playbackModel.getRound().addListener((observable, old, value)
				-> Platform.runLater(this::render));
		playbackModel.getFog().addListener((observable, old, value)
				-> Platform.runLater(this::render));

		// canvas pane
		getChildren().add(canvas);
		setAlignment(Pos.CENTER);
		render();
	}

	private synchronized void render() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		CanvasSize size = new CanvasSize(canvas.getWidth(), canvas.getHeight());

		gc.clearRect(0, 0, size.w, size.h);
		MatchLog match = matchModel.getMatch().get();
		if (match == null)
			return;
		int roundNumber = playbackModel.getRound().get();
		MatchLog.Round round = match.getRounds().get(roundNumber);
		if (round == null)
			return; // no round state

		Size mapSize = match.getMapSize();
		// cell length
		double l = Math.min(
				size.w / mapSize.width(),
				size.h / mapSize.height());
		CanvasPoint origin = new CanvasPoint(
				0.5 * (size.w - l * mapSize.width()),
				0.5 * (size.h - l * mapSize.height())
		);

		CanvasTransform transform = new CanvasTransform();
		transform.mapSize = mapSize;
		transform.size = size;
		transform.origin = origin;
		transform.cellSize = new CanvasSize(l, l);

		boolean fog = playbackModel.getFog().get();

		// draw blocks
		for (Point block : match.getBlocks()) {
			Color color = Styles.BLOCK_COLOR;
			if (fog && inFog(match, round, block)) {
				color = color.darker().darker();
			}
			gc.setFill(color);
			CanvasPoint p = transform.toCanvasPoint(block);
			gc.fillRect(p.x, p.y, l, l);
		}
		// draw coins
		Map<Point, Color> coins = new HashMap<>();
		round.getCoins().forEach(p -> coins.put(p, Styles.COIN_COLOR));
		round.getCollectedCoins().forEach(p -> coins.put(p, Styles.COLLECTED_COIN_COLOR));
		coins.forEach((coin, color) -> {
			if (fog && inFog(match, round, coin)) {
				color = color.darker().darker();
			}
			gc.setFill(color);
			CanvasPoint p = transform.toCanvasPoint(coin);
			double r = 0.25 * l;
			double d = 0.5 * l - r;
			gc.fillRoundRect(p.x + d, p.y + d, 2 * r, 2 * r,
					1.5 * r, 1.5 * r);
		});
		// draw bots
		round.getBots().forEach((k, v) -> {
			if (round.getDeadBots().contains(k))
				return;
			gc.setFill(Styles.botColor(k));
			CanvasPoint p = transform.toCanvasPoint(v);
			gc.fillRect(p.x, p.y, l, l);
		});
	}

	private boolean inFog(MatchLog log, MatchLog.Round round, Point point) {
		int r2 = log.getViewRadius() * log.getViewRadius();
		for (Map.Entry<Integer, Point> entry : round.getBots().entrySet()) {
			int botId = entry.getKey();
			if (round.getDeadBots().contains(botId))
				continue;
			Point bot = entry.getValue();
			int d2 = bot.offsetTo(point, log.getMapSize()).length2();
			if (d2 <= r2)
				return false;
		}
		return true;
	}

	static class CanvasTransform {

		Size mapSize;

		CanvasSize size;

		CanvasPoint origin;

		CanvasSize cellSize;

		CanvasPoint toCanvasPoint(Point mapPoint) {
			return new CanvasPoint(
					origin.x + cellSize.w * mapPoint.x(),
					origin.y + cellSize.h * (mapSize.height() - mapPoint.y() - 1)
			);
		}
	}

	static class CanvasPoint {

		double x;

		double y;

		CanvasPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	static class CanvasSize {

		double w;

		double h;

		public CanvasSize(double w, double h) {
			this.w = w;
			this.h = h;
		}
	}
}
