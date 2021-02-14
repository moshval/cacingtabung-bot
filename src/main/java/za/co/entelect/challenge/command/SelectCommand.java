package za.co.entelect.challenge.command;
import za.co.entelect.challenge.enums.Direction;

public class SelectCommand implements Command {

    private final int id;
    private final String cmd;
    private Direction direction;
    private final int x;
    private final int y;

    public SelectCommand(int id, String cmd,int x,int y,Direction direction) {
        this.id = id;
        this.cmd = cmd;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public String render() {
        if(cmd=="move") return String.format("select %d;move %d %d",id,x, y);
        else if(cmd=="dig") return String.format("select %d;dig %d %d",id,x, y);
        else if(cmd=="banana") return String.format("select %d;banana %d %d",id,x, y);
        else if(cmd=="snowball") return String.format("select %d;snowball %d %d",id,x, y);
        else if(cmd=="shoot") return String.format("select %d;shoot %s",id,direction);
        else return "nothing";
    }
}