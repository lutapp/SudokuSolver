package Model;

import java.util.ArrayList;

public class Cell {
    
    private int value = 0;
    private int column;
    private int row;
    private Box parent;     // Reference to the "parent" box
    private ArrayList<Integer> possibleValues = new ArrayList<Integer>();
    
    // On initialization, all values are possible
    public Cell(int column, int row, Box parent) {
        this.column = column;
        this.row = row;
        this.parent = parent;
        for(int i = 1; i <= 9; i++) {
            this.possibleValues.add(i);
        }
    }
    
    // Reinitializes the possibleValues list if a value gets set, so the list is empty
    public Cell setValue(int value) {
        this.value = value;
        this.possibleValues.clear();
        return this;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public ArrayList<Integer> getPossibleValues() {
        return this.possibleValues;
    }
    
    // Removes one value from the list, and calls the setCellValue function from the parent
    // if only one value is left
    public Cell removeFromPossibilities(int value) {
        int index = this.possibleValues.lastIndexOf(value);
        if (index != -1) {
            this.possibleValues.remove(index);
        }
        if (this.possibleValues.size() == 1) {
            this.parent.setCellValue(this.column, this.row, this.possibleValues.get(0));
        }
        return this;
    }
    
    // Returns the absolute coordinate in the sudoku field, dependent on the parent box name
    public String getAbsoluteCoordinate() {
        String boxname = this.parent.getName();
        char absColumn = boxname.charAt(4);
        absColumn += this.column;
        int absRow = Integer.parseInt(String.valueOf(boxname.charAt(5))) + this.row;
        return "" + absColumn + absRow;
    }
}
