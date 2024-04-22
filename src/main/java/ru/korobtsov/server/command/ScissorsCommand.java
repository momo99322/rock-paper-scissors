package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;

@Component
public class ScissorsCommand  extends MoveCommand {

    @Override
    public CommandType commandType() {
        return CommandType.SCISSORS;
    }
}
