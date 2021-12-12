package ru.croccode.hypernull.player;

import java.io.InputStream;
import java.util.Scanner;

import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.player.model.MatchLog;
import ru.croccode.hypernull.util.Check;
import ru.croccode.hypernull.util.Strings;

public final class MatchLogParser {

	private MatchLogParser() {
	}

	public static MatchLog parse(InputStream in) {
		Check.notNull(in);

		MatchLog match = new MatchLog();
		MatchLog.Round round = new MatchLog.Round(); // round zero
		round.setRound(0);
		match.getRounds().put(0, round);
		Scanner s = new Scanner(in);
		while (s.hasNext()) {
			String line = s.nextLine();
			if (line != null)
				line = line.trim();
			if (Strings.isNullOrEmpty(line) || line.startsWith("#"))
				continue;
			String[] tokens = line.split(" ");
			String key = tokens[0];

			switch (key) {
				case "match":
					// do nothing
					break;

				case "match_id":
					match.setMatchId(tokens[1]);
					break;

				case "num_bots":
					match.setNumBots(Integer.parseInt(tokens[1]));
					break;

				case "mode":
					match.setMode(MatchMode.valueOf(tokens[1]));
					break;

				case "num_rounds":
					match.setNumRounds(Integer.parseInt(tokens[1]));
					break;

				case "random_seed":
				case "move_time_limit":
				case "coin_spawn_period":
				case "coin_spawn_volume":
					// ignore
					break;

				case "map_size":
					match.setMapSize(new Size(
							Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2])));
					break;

				case "view_radius":
					match.setViewRadius(Integer.parseInt(tokens[1]));
					break;

				case "mining_radius":
					match.setMiningRadius(Integer.parseInt(tokens[1]));
					break;

				case "attack_radius":
					match.setAttackRadius(Integer.parseInt(tokens[1]));
					break;

				case "block":
					match.getBlocks().add(new Point(
							Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2])));
					break;

				case "bot_name":
					match.getBotNames().put(
							Integer.parseInt(tokens[1]),
							tokens[2]);
					break;

				case "bot":
					Point bot = new Point(
							Integer.parseInt(tokens[2]),
							Integer.parseInt(tokens[3]));
					round.getBots().put(Integer.parseInt(tokens[1]), bot);
					break;

				case "bot_coins":
					round.getBotCoins().put(
							Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]));
					break;

				case "coin":
					Point coin = new Point(
							Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]));
					round.getCoins().add(coin);
					break;

				case "coin_collected":
					Point collected = new Point(
							Integer.parseInt(tokens[1]),
							Integer.parseInt(tokens[2]));
					round.getCoins().remove(collected);
					round.getCollectedCoins().add(collected);
					break;

				case "round":
					MatchLog.Round nextRound = new MatchLog.Round();
					nextRound.getBots().putAll(round.getBots());
					nextRound.getBotCoins().putAll(round.getBotCoins());
					nextRound.getDeadBots().addAll(round.getDeadBots());
					nextRound.getCoins().addAll(round.getCoins());
					round = nextRound;
					int num = Integer.parseInt(tokens[1]);
					round.setRound(num);
					match.getRounds().put(num, round);
					break;

				case "attack":
					round.getDeadBots().add(Integer.parseInt(tokens[2]));
					break;

				case "match_over":
					// do nothing
					break;
			}
		}

		return match;
	}
}
