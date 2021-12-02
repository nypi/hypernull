package ru.croccode.hypernull.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.croccode.hypernull.geometry.Offset;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Strings;

public final class Messages {

	// current protocol version
	public static final int PROTOCOL_VERSION = 1;

	private Messages() {
	}

	public static List<String> format(Message message) {
		if (message == null)
			return Collections.emptyList();
		if (message instanceof Hello)
			return formatHello((Hello)message);
		if (message instanceof Register)
			return formatRegister((Register)message);
		if (message instanceof MatchStarted)
			return formatMatchStarted((MatchStarted)message);
		if (message instanceof Update)
			return formatUpdate((Update)message);
		if (message instanceof Move)
			return formatMove((Move)message);
		if (message instanceof MatchOver)
			return formatMatchOver((MatchOver)message);
		return Collections.emptyList(); // unknown message
	}

	public static Message parse(List<String> lines) {
		if (lines == null)
			return null;
		// remove empty lines
		lines.removeIf(Strings::isNullOrEmpty);
		// first line - message name
		String messageName = lines.get(0).trim();
		switch (messageName) {
			case "hello":
				return parseHello(lines);
			case "register":
				return parseRegister(lines);
			case "match_started":
				return parseMatchStarted(lines);
			case "update":
				return parseUpdate(lines);
			case "move":
				return parseMove(lines);
			case "match_over":
				return parseMatchOver(lines);
			default:
				return null; // unknown message
		}
	}

	private static void checkMessage(List<String> lines, String messageName) {
		// message has at least two lines
		Check.notNull(lines);
		Check.condition(lines.size() >= 2);
		// first line contains message name
		Check.condition(Objects.equals(lines.get(0), messageName));
		// last line contains end token
		Check.condition(Objects.equals(lines.get(lines.size() - 1), "end"));
	}

	private static String formatParameter(String name, Object... values) {
		Check.notEmpty(name);
		Stream<String> tokens = Stream.of(name);
		if (values != null) {
			tokens = Stream.concat(
					tokens,
					Arrays.stream(values).map(v -> String.valueOf(v).trim()));
		}
		return tokens.collect(Collectors.joining(" "));
	}

	private static void parseParameter(String line, BiConsumer<String, List<String>> parse) {
		line = Strings.nullToEmpty(line).trim();
		if (line.isEmpty())
			return;
		String[] tokens = line.split(" ");
		String name = tokens[0];
		List<String> values = tokens.length > 1
				? Arrays.stream(tokens, 1, tokens.length).collect(Collectors.toList())
				: Collections.emptyList();
		parse.accept(name, values);
	}

	private static void parseParameters(List<String> lines, BiConsumer<String, List<String>> parse) {
		if (lines == null)
			return;
		int i = 0;
		for (String line : lines) {
			if (i > 0 && i < lines.size() - 1)
				parseParameter(line, parse);
			i++;
		}
	}

	// hello

	public static List<String> formatHello(Hello message) {
		List<String> lines = new ArrayList<>();
		lines.add("hello");
		if (message.getProtocolVersion() != null)
			lines.add(formatParameter("protocol_version", message.getProtocolVersion()));
		lines.add("end");
		return lines;
	}

	public static Hello parseHello(List<String> lines) {
		checkMessage(lines, "hello");
		Hello message = new Hello();
		parseParameters(lines, (name, values) -> {
			switch (name) {
				case "protocol_version":
					message.setProtocolVersion(Integer.parseInt(values.get(0)));
					break;
			}
		});
		return message;
	}

	// register

	public static List<String> formatRegister(Register message) {
		List<String> lines = new ArrayList<>();
		lines.add("register");
		if (!Strings.isNullOrEmpty(message.getBotName()))
			lines.add(formatParameter("bot_name", message.getBotName()));
		if (!Strings.isNullOrEmpty(message.getBotSecret()))
			lines.add(formatParameter("bot_secret", message.getBotSecret()));
		if (message.getMode() != null)
			lines.add(formatParameter("mode", message.getMode()));
		lines.add("end");
		return lines;
	}

	public static Register parseRegister(List<String> lines) {
		checkMessage(lines, "register");
		Register message = new Register();
		parseParameters(lines, (name, values) -> {
			switch (name) {
				case "bot_name":
					message.setBotName(values.get(0));
					break;
				case "bot_secret":
					message.setBotSecret(values.get(0));
					break;
				case "mode":
					message.setMode(MatchMode.valueOf(values.get(0)));
					break;
			}
		});
		return message;
	}

	// match_started

	public static List<String> formatMatchStarted(MatchStarted message) {
		List<String> lines = new ArrayList<>();
		lines.add("match_started");
		if (!Strings.isNullOrEmpty(message.getMatchId()))
			lines.add(formatParameter("match_id", message.getMatchId()));
		if (message.getNumRounds() != null)
			lines.add(formatParameter("num_rounds", message.getNumRounds()));
		if (message.getMode() != null)
			lines.add(formatParameter("mode", message.getMode()));
		if (message.getMapSize() != null)
			lines.add(formatParameter("map_size",
					message.getMapSize().width(), message.getMapSize().height()));
		if (message.getNumBots() != null)
			lines.add(formatParameter("num_bots", message.getNumBots()));
		if (message.getYourId() != null)
			lines.add(formatParameter("your_id", message.getYourId()));
		if (message.getViewRadius() != null)
			lines.add(formatParameter("view_radius", message.getViewRadius()));
		if (message.getMiningRadius() != null)
			lines.add(formatParameter("mining_radius", message.getMiningRadius()));
		if (message.getAttackRadius() != null)
			lines.add(formatParameter("attack_radius", message.getAttackRadius()));
		if (message.getMoveTimeLimit() != null)
			lines.add(formatParameter("move_time_limit", message.getMoveTimeLimit()));
		lines.add("end");
		return lines;
	}

	public static MatchStarted parseMatchStarted(List<String> lines) {
		checkMessage(lines, "match_started");
		MatchStarted message = new MatchStarted();
		parseParameters(lines, (name, values) -> {
			switch (name) {
				case "match_id":
					message.setMatchId(values.get(0));
					break;
				case "num_rounds":
					message.setNumRounds(Integer.parseInt(values.get(0)));
					break;
				case "mode":
					message.setMode(MatchMode.valueOf(values.get(0)));
					break;
				case "map_size":
					message.setMapSize(new Size(
							Integer.parseInt(values.get(0)),
							Integer.parseInt(values.get(1))));
					break;
				case "num_bots":
					message.setNumBots(Integer.parseInt(values.get(0)));
					break;
				case "your_id":
					message.setYourId(Integer.parseInt(values.get(0)));
					break;
				case "view_radius":
					message.setViewRadius(Integer.parseInt(values.get(0)));
					break;
				case "mining_radius":
					message.setMiningRadius(Integer.parseInt(values.get(0)));
					break;
				case "attack_radius":
					message.setAttackRadius(Integer.parseInt(values.get(0)));
					break;
				case "move_time_limit":
					message.setMoveTimeLimit(Long.parseLong(values.get(0)));
					break;
			}
		});
		return message;
	}

	// update

	public static List<String> formatUpdate(Update message) {
		List<String> lines = new ArrayList<>();
		lines.add("update");
		if (message.getRound() != null)
			lines.add(formatParameter("round", message.getRound()));
		if (message.getBots() != null && message.getBotCoins() != null) {
			Set<Integer> botIds = new HashSet<>(message.getBots().keySet());
			botIds.retainAll(message.getBotCoins().keySet());
			for (Integer botId : botIds) {
				Point bot = message.getBots().get(botId);
				Integer numBotCoins = message.getBotCoins().get(botId);
				lines.add(formatParameter("bot", bot.x(), bot.y(), numBotCoins, botId));
			}
		}
		if (message.getBlocks() != null) {
			for (Point block : message.getBlocks()) {
				lines.add(formatParameter("block", block.x(), block.y()));
			}
		}
		if (message.getCoins() != null) {
			for (Point coin : message.getCoins()) {
				lines.add(formatParameter("coin", coin.x(), coin.y()));
			}
		}
		lines.add("end");
		return lines;
	}

	public static Update parseUpdate(List<String> lines) {
		checkMessage(lines, "update");
		Update message = new Update();
		parseParameters(lines, (name, values) -> {
			switch (name) {
				case "round":
					message.setRound(Integer.parseInt(values.get(0)));
					break;
				case "bot":
					if (message.getBots() == null)
						message.setBots(new HashMap<>());
					if (message.getBotCoins() == null)
						message.setBotCoins(new HashMap<>());

					Integer botId = Integer.parseInt(values.get(3));
					message.getBots().put(botId, new Point(
							Integer.parseInt(values.get(0)),
							Integer.parseInt(values.get(1))));
					message.getBotCoins().put(botId, Integer.parseInt(values.get(2)));
					break;
				case "block":
					if (message.getBlocks() == null)
						message.setBlocks(new HashSet<>());

					message.getBlocks().add(new Point(
							Integer.parseInt(values.get(0)),
							Integer.parseInt(values.get(1))));
					break;
				case "coin":
					if (message.getCoins() == null)
						message.setCoins(new HashSet<>());

					message.getCoins().add(new Point(
							Integer.parseInt(values.get(0)),
							Integer.parseInt(values.get(1))));
					break;
			}
		});
		return message;
	}

	// move

	public static List<String> formatMove(Move message) {
		List<String> lines = new ArrayList<>();
		lines.add("move");
		if (message.getOffset() != null)
			lines.add(formatParameter("offset", message.getOffset().dx(), message.getOffset().dy()));
		lines.add("end");
		return lines;
	}

	public static Move parseMove(List<String> lines) {
		checkMessage(lines, "move");
		Move message = new Move();
		parseParameters(lines, (name, values) -> {
			switch (name) {
				case "offset":
					message.setOffset(new Offset(
							Integer.parseInt(values.get(0)),
							Integer.parseInt(values.get(1))));
					break;
			}
		});
		return message;
	}

	// match_over

	public static List<String> formatMatchOver(MatchOver message) {
		List<String> lines = new ArrayList<>();
		lines.add("match_over");
		lines.add("end");
		return lines;
	}

	public static MatchOver parseMatchOver(List<String> lines) {
		checkMessage(lines, "match_over");
		return new MatchOver();
	}
}
