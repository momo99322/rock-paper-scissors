package ru.korobtsov.server.stage;

public class CloseStage implements Stage {

    @Override
    public Stage prev() {
        return new CloseStage();
    }

    @Override
    public Stage nextSuccessfully() {
        return new CloseStage();
    }

    @Override
    public StageType type() {
        return StageType.CLOSED;
    }

    @Override
    public String onSetupMessage() {
        return "It was good to see you, bye!";
    }
}
