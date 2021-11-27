package ru.croccode.hypernull.message;

import ru.croccode.hypernull.domain.MatchMode;

public class Register extends Message {

	private String botName;

	private String botSecret;

	private MatchMode mode;

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getBotSecret() {
		return botSecret;
	}

	public void setBotSecret(String botSecret) {
		this.botSecret = botSecret;
	}

	public MatchMode getMode() {
		return mode;
	}

	public void setMode(MatchMode mode) {
		this.mode = mode;
	}
}
