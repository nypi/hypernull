package ru.croccode.hypernull.bot;

import ru.croccode.hypernull.message.MatchOver;
import ru.croccode.hypernull.message.MatchStarted;
import ru.croccode.hypernull.message.Move;
import ru.croccode.hypernull.message.Register;
import ru.croccode.hypernull.message.Update;

public interface Bot {

	Register registerAs();

	void matchStarted(MatchStarted matchStarted);

	void matchOver(MatchOver matchOver);

	Move makeMove(Update update);
}
