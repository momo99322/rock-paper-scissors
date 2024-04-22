package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;

@Component
public class RockCommand extends MoveCommand {

    @Override
    public CommandType commandType() {
        return CommandType.ROCK;
    }
}
