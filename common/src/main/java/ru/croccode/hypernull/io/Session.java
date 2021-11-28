package ru.croccode.hypernull.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.croccode.hypernull.message.Message;
import ru.croccode.hypernull.message.Messages;
import ru.croccode.hypernull.util.Check;

public class Session implements Closeable {

	private final BufferedReader reader;

	private final BufferedWriter writer;

	private volatile boolean closed;

	public Session(Reader reader, Writer writer) {
		Check.notNull(reader);
		Check.notNull(writer);

		this.reader = reader instanceof BufferedReader
				? (BufferedReader)reader
				: new BufferedReader(reader);
		this.writer = writer instanceof BufferedWriter
				? (BufferedWriter)writer
				: new BufferedWriter(writer);
	}

	public void write(Message message) throws IOException {
		if (message == null)
			return;
		List<String> lines = Messages.format(message);
		for (String line : lines) {
			writer.write(line);
			writer.write("\n");
		}
		writer.flush();
	}

	public <T extends Message> T read(Class<T> type) throws IOException {
		Check.notNull(type);

		while (true) {
			Message message = read();
			if (message == null)
				return null; // end of stream
			if (message.getClass() == type)
				return (T)message;
		}
	}

	public Message read() throws IOException {
		List<String> lines = new ArrayList<>();
		while (true) {
			String line = reader.readLine();
			if (line == null)
				return null;
			if (line.isEmpty())
				continue;
			lines.add(line);
			if (Objects.equals(line.trim(), "end")) {
				return Messages.parse(lines);
			}
		}
	}

	@Override
	public void close() throws IOException {
		writer.close();
		reader.close();
		closed = true;
	}
}
