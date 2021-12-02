package ru.croccode.hypernull.message;

public class Hello extends Message {

	private Integer protocolVersion = Messages.PROTOCOL_VERSION;

	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(Integer protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
}
