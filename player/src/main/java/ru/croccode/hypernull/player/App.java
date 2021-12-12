package ru.croccode.hypernull.player;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.croccode.hypernull.player.controller.PlaybackController;
import ru.croccode.hypernull.player.model.MatchModel;
import ru.croccode.hypernull.player.model.PlaybackModel;
import ru.croccode.hypernull.player.view.InfoView;
import ru.croccode.hypernull.player.view.MatchView;
import ru.croccode.hypernull.player.view.PlaybackView;

public class App extends Application {

	public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

	private final MatchModel matchModel;
	private final PlaybackModel playbackModel;
	private final PlaybackController playbackController;

	public static void main(String[] args) {
		launch(args);
	}

	public App() {
		matchModel = new MatchModel();
		playbackModel = new PlaybackModel();
		playbackController = new PlaybackController(matchModel, playbackModel);
	}

	@Override
	public void start(Stage primaryStage) {

		// info pane
		InfoView infoView = new InfoView(matchModel, playbackModel);
		StackPane infoPane = new StackPane(infoView);
		infoPane.setMinHeight(60);
		infoPane.setAlignment(Pos.CENTER_LEFT);
		infoPane.setPadding(Styles.defaultPadding());

		// canvas pane
		MatchView matchView = new MatchView(matchModel, playbackModel);
		StackPane canvasPane = matchView;
		canvasPane.setMinSize(200, 200);

		// playback pane
		PlaybackView playbackView = new PlaybackView(matchModel, playbackModel);
		StackPane playbackPane = new StackPane(playbackView);
		playbackPane.setMinHeight(50);
		playbackPane.setBackground(Styles.solidBackground(Styles.CLEAR_COLOR.brighter()));
		playbackPane.setPadding(Styles.defaultPadding());
		StackPane.setAlignment(playbackView, Pos.CENTER);

		// layout
		VBox vbox = new VBox(infoPane, canvasPane, playbackPane);
		vbox.setFillWidth(true);
		VBox.setVgrow(canvasPane, Priority.ALWAYS);
		vbox.setBackground(Styles.solidBackground(Styles.CLEAR_COLOR));
		vbox.setOnDragOver(e -> {
			if (e.getGestureSource() != vbox && e.getDragboard().hasFiles()) {
				e.acceptTransferModes(TransferMode.COPY);
			}
			e.consume();
		});
		vbox.setOnDragDropped(e -> {
			Dragboard board = e.getDragboard();
			boolean done = false;
			if (board.hasFiles()) {
				try {
					queueReplays(board.getFiles());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				done = true;
			}
			e.setDropCompleted(done);
			e.consume();
		});

		Scene scene = new Scene(vbox);
		URL style = getClass().getResource("/style.css");
		if (style != null) {
			scene.getStylesheets().add(style.toExternalForm());
		}
		primaryStage.setTitle("¤ HyperNull Player ¤");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);
		primaryStage.show();
	}

	@Override
	public void stop() {
		SCHEDULER.shutdownNow();
	}

	private void queueReplays(List<File> files) throws IOException {
		Predicate<Path> isReplay = f -> Files.isRegularFile(f)
				&& f.getFileName().toString().toLowerCase().endsWith(".log");
		List<Path> paths = new ArrayList<>();
		if (files != null) {
			for (File file : files) {
				Path path = file.toPath();
				if (!Files.exists(path))
					continue;
				if (Files.isDirectory(path)) {
					Files.walk(path)
							.filter(isReplay)
							.forEach(paths::add);
				} else {
					if (isReplay.test(path))
						paths.add(path);
				}
			}
		}
		matchModel.queueReplays(paths);
	}
}
