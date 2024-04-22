package ru.korobtsov.server.stage;

public interface Stage {

    Stage prev();

    Stage nextSuccessfully();

    StageType type();

    String onSetupMessage();
}
