package Model;

import java.net.Socket;

public class Box {

    private String name;
    private Cell[] cells;
    private String[] neighbours;
    private Socket socket;

    public Box() {
        this.cells = new Cell[9];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

}
