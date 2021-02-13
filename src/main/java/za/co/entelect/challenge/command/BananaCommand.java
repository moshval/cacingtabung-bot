package za.co.entelect.challenge.command;

import za.co.entelect.challenge.enums.Direction;

public class BananaCommand implements Command {

    private Direction direction;

    public BananaCommand(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String render() {
        return String.format("banana %s", direction.name());
    }
}