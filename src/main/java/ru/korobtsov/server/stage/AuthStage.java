package ru.korobtsov.server.stage;

public class AuthStage implements Stage {

    @Override
    public Stage prev() {
        return new AuthStage();
    }

    @Override
    public Stage nextSuccessfully() {
        return new SearchStage();
    }

    @Override
    public StageType type() {
        return StageType.AUTH;
    }

    @Override
    public String onSetupMessage() {
        return "Enter a nickname";
    }
}
