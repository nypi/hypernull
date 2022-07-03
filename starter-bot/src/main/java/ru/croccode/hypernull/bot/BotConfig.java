package ru.croccode.hypernull.bot;

import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.util.Check;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class BotConfig {

    private final String serverHost;

    private final int serverPort;

    private final String botName;

    private final String botSecret;

    private final MatchMode mode;

    public BotConfig(Properties properties) {
        Check.notNull(properties);

        serverHost = properties.getProperty(
                "server.host", "localhost");
        serverPort = Integer.parseInt(properties.getProperty(
                "server.port", "2021"));
        botName = properties.getProperty(
                "bot.name", "starter-bot");
        botSecret = properties.getProperty(
                "bot.secret");
        mode = MatchMode.valueOf(properties.getProperty(
                "mode", "FRIENDLY"));
    }

    public static BotConfig load(Path path) throws IOException {
        Properties properties = new Properties();
        if (path != null && Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                properties.load(in);
            }
        }
        return new BotConfig(properties);
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getBotName() {
        return botName;
    }

    public String getBotSecret() {
        return botSecret;
    }

    public MatchMode getMode() {
        return mode;
    }
}
