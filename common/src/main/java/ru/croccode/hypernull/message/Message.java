package ru.croccode.hypernull.message;

import java.util.List;

public abstract class Message {

	@Override
	public String toString() {
		List<String> lines = Messages.format(this);
		return String.join("\n", lines);
	}
}
