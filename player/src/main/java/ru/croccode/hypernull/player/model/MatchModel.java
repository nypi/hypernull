package ru.croccode.hypernull.player.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ru.croccode.hypernull.player.MatchLogParser;

public class MatchModel {

	// active match log
	private final ObjectProperty<MatchLog> match = new SimpleObjectProperty<>(null);

	private final Queue<Path> queue = new ArrayDeque<>();

	public ObjectProperty<MatchLog> getMatch() {
		return match;
	}

	public void queueReplays(Collection<Path> paths) {
		if (paths == null ||  paths.isEmpty())
			return;
		queue.clear();
		queue.addAll(paths);
		advance();
	}

	public void advance() {
		while (!queue.isEmpty()) {
			Path path = queue.poll();
			try (InputStream in = Files.newInputStream(path)) {
				MatchLog log = MatchLogParser.parse(in);
				match.setValue(log);
				return;
			} catch (IOException e) {
				e.printStackTrace();
				// try next path
			}
		}
	}
}
