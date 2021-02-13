package za.co.entelect.challenge.command;

import za.co.entelect.challenge.enums.Direction;

public class SnowballCommand implements Command {

    private Direction direction;

    public SnowballCommand(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String render() {
        return String.format("snowball %s", direction.name());
    }
}