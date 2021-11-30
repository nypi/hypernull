package ru.croccode.hypernull.server;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.match.MatchConfig;
import ru.croccode.hypernull.match.MatchListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

public class MatchFileLogger<K> implements MatchListener<K> {

    private final static String LOG_FILE_TEMPLATE = "%s/Match_log_%s.txt";

    private final UUID matchId;
    private final PrintWriter logWriter;

    public MatchFileLogger(String logsFolder) {
        this.matchId = UUID.randomUUID();
        Path path = Paths.get(logsFolder);
        try {
            Files.createDirectories(path);
            final String logFileName = String.format(LOG_FILE_TEMPLATE, logsFolder, matchId);
            this.logWriter = new PrintWriter(logFileName);
        } catch (IOException ex) {
            throw new RuntimeException(
                "Ошибка инициализации логера в файл: " + ex.getMessage(),
                ex
            );
        }
    }

    @Override
    public void matchStarted(MatchMap map, MatchConfig config, Map<K, String> botNames) {
        write("match");
        for (Field declaredField : config.getClass().getDeclaredFields()) {
            try {
                declaredField.setAccessible(true);
                write(declaredField.getName() + " " + declaredField.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                    "Ошибка получения значения поля через рефлексию: " + e.getMessage(),
                    e
                );
            }
        }
        for (Map.Entry<K, String> botEntry : botNames.entrySet()) {
            write("bot_name " + botEntry.getKey() + " " + botEntry.getValue());
        }
    }

    @Override
    public void matchRound(int round) {
        write("round " + round);
    }

    @Override
    public void coinSpawned(Point position) {
        write("coin " + position.toString());
    }

    @Override
    public void coinCollected(Point position, K botKey) {
        write("coin_collected " + position.toString() + " " + botKey);
    }

    @Override
    public void botSpawned(K botKey, Point position) {
        botMoved(botKey, position);
    }

    @Override
    public void botMoved(K botKey, Point position) {
        write("bot " + botKey + " " + position.toString());
    }

    @Override
    public void attack(K attackingKey, K defendingKey) {
        write("attack " + attackingKey + " " + defendingKey);
    }

    @Override
    public void botCoinsChanged(K botKey, int numCoins) {
        write("bot_coins " + botKey + " " + numCoins); //TODO надо ли выводить предыдущее кол-во монет?
    }

    @Override
    public void matchOver(K botKey) {
        write("match_over " + botKey);
    }

    private void write(String msg) {
        logWriter.println(msg);
        logWriter.flush();
    }
    @Override
    public void close() {
        logWriter.close();
    }
}
