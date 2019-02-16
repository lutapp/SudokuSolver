package Model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

public class Box {

    private String name;
    private Cell[][] cells = new Cell[3][3];
    private FtpClient ftp = null;
    private FTPSendHandler ftpSendHandler = null;
    private FTPPollThread ftpPollThread = null;
    private String FTPPath = null;
    private boolean isFinished = false;
    

    public Box(String domain, String login, String pass, String path) throws IOException {
        this.initializeCells();
        try {
        	this.FTPPath = path;
			this.ftp = FtpClient.create();
			System.out.println("Connecting to FTP server: " + domain);
			this.ftp.connect(new InetSocketAddress(domain, 21)).login(login, pass.toCharArray());
			System.out.println("Connected to FTP server!");
			this.ftpSendHandler = new FTPSendHandler(this.ftp);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(865); //Fehlercode 865 ist der beste Code!
		}
    }
    
    // call this AFTER setName() only
    public void initializePollThread() {
    	if (this.ftpPollThread != null) {
    		return;
    	}
    	this.ftpPollThread = new FTPPollThread(this, this.ftp, this.FTPPath, this.getNeighbourNames());
    	this.ftpPollThread.start();
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
    
    public Cell getCell(int column, int row) {
        return this.cells[column][row];
    }
    
    // Sets a value to a specified cell, and propagates this information to the other cells,
    // so they know this value is no longer possible for them to have
    public Box setCellValue(int column, int row, int value) {
        this.cells[column][row].setValue(value);
        String relative = this.name + ',' + this.cells[column][row].getColumn() + ',' + this.cells[column][row].getRow() + ':' + this.cells[column][row].getValue();
        try {
			this.ftpSendHandler.putFile(this.FTPPath, this.name, relative);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
        int emptyCellCounter = 0;
        for (Cell[] cellColumn : this.cells) {
            for (Cell cell : cellColumn) {
                cell.removeFromPossibilities(value);
                if (cell.getValue() == 0) {
                	emptyCellCounter++;
                }
            }
        }
        if (emptyCellCounter == 0 && !isFinished) {
        	System.out.println("\nBox completed! Wrapping up...");
        	
        	if (this.ftpPollThread != null) {
        		this.ftpPollThread.wrapUpConnections();
        	} else {
        		this.initializePollThread();
        		this.ftpPollThread.wrapUpConnections();
        	}
        	
        	this.isFinished = true;
        	
        	StringBuilder result = new StringBuilder();
	    	result.append("RESULT,");
	    	result.append(this.getName());
	    	for(int i = 0; i < 3; i++) {
	    		for(int j = 0; j < 3; j++) {
	    			result.append(',');
	    			result.append(this.getCell(j, i).getValue());
	    		}
	    	}
        	try {
				this.ftpSendHandler.putFile(this.FTPPath, this.getName() + "_Result", result.toString());
			} catch (Exception e) {
				e.printStackTrace();
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
