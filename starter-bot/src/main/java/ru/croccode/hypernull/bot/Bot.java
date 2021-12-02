package ru.croccode.hypernull.bot;

import ru.croccode.hypernull.message.Hello;
import ru.croccode.hypernull.message.MatchOver;
import ru.croccode.hypernull.message.MatchStarted;
import ru.croccode.hypernull.message.Move;
import ru.croccode.hypernull.message.Register;
import ru.croccode.hypernull.message.Update;

public interface Bot {

	Register onHello(Hello hello);

	void onMatchStarted(MatchStarted matchStarted);

	Move onUpdate(Update update);

	void onMatchOver(MatchOver matchOver);
}
