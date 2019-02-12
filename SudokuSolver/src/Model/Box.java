package Model;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import Controller.Main;

public class Box {

    private String name;
    private Cell[][] cells = new Cell[3][3];
//    private ArrayList<BoxNeighbourSocket> neighbours = new ArrayList<BoxNeighbourSocket>();

    public Box() throws IOException {
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
    
//    public Box addNeighbour(BoxNeighbourSocket neighbour) {
//    	synchronized (this.neighbours) {
//	        this.neighbours.add(neighbour);
//	        neighbour.start();
//	        return this;
//    	}
//    }
    
//    public ArrayList<BoxNeighbourSocket> getNeighbours() {
//    	return this.neighbours;
//    }
    
    public Cell getCell(int column, int row) {
        return this.cells[column][row];
    }
    
    // Sets a value to a specified cell, and propagates this information to the other cells,
    // so they know this value is no longer possible for them to have
    public Box setCellValue(int column, int row, int value) {
        this.cells[column][row].setValue(value);
//      String relative = this.name + ',' + this.cells[column][row].getColumn() + ',' + this.cells[column][row].getRow() + ':' + this.cells[column][row].getValue();
//    	PendingMessageHandler.addMessageToPending(relative);
//    	PendingMessageHandler pmh = new PendingMessageHandler(this);
//    	pmh.start();
        int emptyCellCounter = 0;
        for (Cell[] cellColumn : this.cells) {
            for (Cell cell : cellColumn) {
                cell.removeFromPossibilities(value);
                if (cell.getValue() == 0) {
                	emptyCellCounter++;
                }
            }
        }
        if (emptyCellCounter == 0) {
//        	Main.wrapUpConnections();
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
    
    public ArrayList<String> getNeighbourNames() {
    	String[][] SudokuStructure = {{"BOX_A1", "BOX_D1", "BOX_G1"}, {"BOX_A4", "BOX_D4", "BOX_G4"}, {"BOX_A7", "BOX_D7", "BOX_G7"}};
    	int row = -1;
    	int col = -1;
    	
    	for(int i = 0; i < SudokuStructure.length; i++) {
    		for(int j = 0; j < SudokuStructure[i].length; j++) {
    			if (SudokuStructure[i][j].equals(this.name)) {
    				row = i;
    				col = j;
    			}
    		}
    	}
    	
    	if (row != -1 && col != -1) {
    		ArrayList<String> output = new ArrayList<String>();
    		try {
    			output.add(SudokuStructure[row-1][col]);
    		} catch (ArrayIndexOutOfBoundsException e) {	
    		}
    		try {
    			output.add(SudokuStructure[row+1][col]);
    		} catch (ArrayIndexOutOfBoundsException e) {	
    		}
    		try {
    			output.add(SudokuStructure[row][col-1]);
    		} catch (ArrayIndexOutOfBoundsException e) {	
    		}
    		try {
    			output.add(SudokuStructure[row][col+1]);
    		} catch (ArrayIndexOutOfBoundsException e) {	
    		}
    		
    		return output;
    	} else {
    		return null;
    	}
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
