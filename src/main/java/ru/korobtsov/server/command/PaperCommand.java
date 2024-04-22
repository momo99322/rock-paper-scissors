package ru.korobtsov.server.command;

import org.springframework.stereotype.Component;

@Component
public class PaperCommand extends MoveCommand {

    @Override
    public CommandType commandType() {
        return CommandType.PAPER;
    }
}
