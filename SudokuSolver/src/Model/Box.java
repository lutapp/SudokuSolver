package Model;

import java.util.ArrayList;

public class Box {

    private String name;
    private Cell[][] cells = new Cell[3][3];
    private ArrayList<String> neighbours = new ArrayList<String>();

    public Box() {
        this.initializeCells();
    }
    
    public Box(String name) {
        this.name = name;
        this.initializeCells();
    }
    
    // Since cells are objects, initialize each of them manually
    private void initializeCells() {
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells[i].length; j++) {
                this.cells[i][j] = new Cell(i, j, this);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public Box setName(String name) {
        this.name = name;
        return this;
    }
    
    public Box addNeighbour(String neighbour) {
        this.neighbours.add(neighbour);
        return this;
    }
    
    public Cell getCell(int column, int row) {
        return this.cells[column][row];
    }
    
    // Sets a value to a specified cell, and propagates this information to the other cells,
    // so they know this value is no longer possible for them to have
    public Box setCellValue(int column, int row, int value) {
        this.cells[column][row].setValue(value);
        for (Cell[] cellColumn : this.cells) {
            for (Cell cell : cellColumn) {
                cell.removeFromPossibilities(value);
            }
        }
        return this;
    }
    
    // Returns all cell objects from the specified column
    public Cell[] getAffectedColumn(int column) {
        Cell[] result = new Cell[3];
        for(int i = 0; i < this.cells.length; i++) {
            result[i] = this.cells[column][i];
        }
        return result;
    }
    
    // Returns all cell objects from the specified row
    public Cell[] getAffectedRow(int row) {
        Cell[] result = new Cell[3];
        for(int i = 0; i < this.cells.length; i++) {
            result[i] = this.cells[i][row];
        }
        return result;
    }
    
    // prints current state of the box
    public void printCells() {
        StringBuilder build = new StringBuilder();
        build.append("Cell Contents:\n");
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells.length; j++) {
                build.append('[');
                if (this.cells[j][i].getValue() == 0) {
                    build.append(' ');
                } else {
                    build.append(this.cells[j][i].getValue());
                }
                build.append(']');
            }
            build.append('\n');
        }
        System.out.println(build.toString());
    }
    
    // prints current absolute cell coordinates, for debugging purposes
    public void printCellAbsoluteCoordinates() {
        StringBuilder build = new StringBuilder();
        build.append("Absolute Coordinates:\n");
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells.length; j++) {
                build.append('[');
                build.append(this.cells[j][i].getAbsoluteCoordinate());
                build.append(']');
            }
            build.append('\n');
        }
        System.out.println(build.toString());
    }
    
    // prints current possible values for all cells, for debugging purposes
    public void printCellPossibleValues() {
        StringBuilder build = new StringBuilder();
        build.append("Possible Values:\n");
        for (int i = 0; i < this.cells.length; i++) {
            for (int j = 0; j < this.cells.length; j++) {
                build.append('[');
                for(int value : this.cells[j][i].getPossibleValues()) {
                    build.append(value);
                    build.append(' ');
                }
                build.append(']');
            }
            build.append('\n');
        }
        System.out.println(build.toString());
    }

}
