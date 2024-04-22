package ru.korobtsov.server.command;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.korobtsov.server.context.GameContext;

@Getter
@Accessors(fluent = true)
public class AuthCommand implements Command {
    private final String nickname;

    public AuthCommand(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public CommandType commandType() {
        return CommandType.AUTH;
    }

    @Override
    public String onExecute(GameContext gameContext) {
        return "Authentication...";
    }
}
