package ru.croccode.hypernull.server;

import ru.croccode.hypernull.domain.MatchMap;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.match.MatchConfig;
import ru.croccode.hypernull.match.MatchListener;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class MatchFileLogger<K> implements MatchListener<K>, Closeable {

    private final static String LOG_FILE_TEMPLATE = "%s/match_%s.log";

    private final PrintWriter logWriter;

    public MatchFileLogger(String matchId, String logsFolder) {
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
    public void matchStarted(String id, MatchMap map, MatchConfig config, Map<K, String> botNames) {
        write("match");
        write("match_id " + id);
        write("match_time " + System.currentTimeMillis());
        write("num_bots " + botNames.size());
        write("##MatchConfig");
        printAllFields(config);
        write("##MapConfig");
        final Size mapSize = map.getSize();
        write("map_size " + mapSize.width() + " " + mapSize.height());
        write("view_radius " + map.getViewRadius());
        write("mining_radius " + map.getMiningRadius());
        write("attack_radius " + map.getAttackRadius());
        printAllBlocks(map);
        write("##BotsAndCoinsInfo");
        for (Map.Entry<K, String> botEntry : botNames.entrySet()) {
            write("bot_name " + botEntry.getKey() + " " + botEntry.getValue());
        }
    }

    private void printAllBlocks(MatchMap map) {
        for (int row = 0; row < map.getHeight(); row++) {
            for (int column = 0; column < map.getWidth(); column++) {
                Point somePoint = new Point(column, row);
                if (map.isBlocked(somePoint)) {
                    write("block " + somePoint.toLog());
                }
            }
        }
    }

    @Override
    public void matchRound(int round) {
        write("round " + round);
    }

    @Override
    public void coinSpawned(Point position) {
        write("coin " + position.toLog());
    }

    @Override
    public void coinCollected(Point position, K botKey) {
        write("coin_collected " + position.toLog() + " " + botKey);
    }

    @Override
    public void botSpawned(K botKey, Point position) {
        botMoved(botKey, position);
    }

    @Override
    public void botMoved(K botKey, Point position) {
        write("bot " + botKey + " " + position.toLog());
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

    private void printAllFields(Object someObj) {
        final Class<?> objClass = someObj.getClass();
        for (Field declaredField : objClass.getDeclaredFields()) {
            try {
                declaredField.setAccessible(true);
                final String filedName = declaredField.getName()
                    .replaceAll("([A-Z][a-z])", "_$1")
                    .toLowerCase();
                write(filedName + " " + declaredField.get(someObj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                    "Ошибка получения значения поля для класса " + objClass.getSimpleName()
                        + " через рефлексию: " + e.getMessage(),
                    e
                );
            }
        }
    }
}
