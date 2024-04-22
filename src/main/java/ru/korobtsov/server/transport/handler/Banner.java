package ru.korobtsov.server.transport.handler;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Accessors(fluent = true)
public class Banner {

    @Getter
    private static final String banner = readBannerFromFile();

    private static final String fallbackBanner = "ROCK PAPER SCISSORS\n";

    private static String readBannerFromFile() {
        var url = ClassLoader.getSystemResource("banner");
        if (url == null) {
            return fallbackBanner;
        }

        try {
            return Files.readString(Paths.get(url.toURI()));
        } catch (IOException | URISyntaxException exception) {
            log.warn("Can't read banner, will yous fallback={}", fallbackBanner, exception);
            return fallbackBanner;
        }
    }
}
