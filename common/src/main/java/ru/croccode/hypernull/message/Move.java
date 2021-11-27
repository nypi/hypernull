package ru.croccode.hypernull.message;

import ru.croccode.hypernull.geometry.Offset;

public class Move extends Message {

	private Offset offset;

	public Offset getOffset() {
		return offset;
	}

	public void setOffset(Offset offset) {
		this.offset = offset;
	}
}
