package ru.croccode.hypernull.util;

public final class Strings {

	private static final String EMPTY_STRING = "";

	private Strings() {
	}

	public static String empty() {
		return EMPTY_STRING;
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static String nullToEmpty(String str) {
		return str == null ? EMPTY_STRING : str;
	}

	public static String emptyToNull(String str) {
		return str != null && str.isEmpty() ? null : str;
	}
}
