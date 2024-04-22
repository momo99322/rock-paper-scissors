package ru.korobtsov.server.context;

import org.springframework.stereotype.Component;

@Component
public class GameContextFactory {
    public GameContext create() {
        return new GameContext();
    }
}
