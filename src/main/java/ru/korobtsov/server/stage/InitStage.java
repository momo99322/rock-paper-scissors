package ru.korobtsov.server.stage;

public class InitStage implements Stage {
    @Override
    public Stage prev() {
        return new InitStage();
    }

    @Override
    public Stage nextSuccessfully() {
        return new AuthStage();
    }

    @Override
    public StageType type() {
        return StageType.INIT;
    }

    @Override
    public String onSetupMessage() {
        return null;
    }
}
