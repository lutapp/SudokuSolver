package Controller;

import Exception.ProcessArgumentException;
import Model.Box;
import Model.PendingMessageHandler;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;


public class Main {
    
    static Box box;
    

    public static void main(String[] args) {
    	try {
    		box = new Box("127.0.0.1", "m3ntozz911", "testpw", "/knowledge/");
    		parseArgs(args);
    		box.initializePollThread();
			
    	} catch (ProcessArgumentException e) {
			System.out.println("Usage: Controller.Main <box name> <initial cell values>");
			e.printStackTrace();
			System.exit(0);
    	} catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void parseArgs(String[] args) throws ProcessArgumentException {
        boolean positionIsKnown = false;
        
        // Check if arguments have the specified patterns
        for(String arg : args) {
            // Box
            if (arg.matches("^BOX_[ADG][147]$")) {
                Main.box.setName(arg);
                positionIsKnown = true;
                continue;
            }
            
            // Cells
            if (arg.matches("^([0-2][0-2]:[1-9],\\s*){0,8}([0-2][0-2]:[1-9])$")) {
                String[] singleCells = arg.split(",");
                for (String cell: singleCells) {
                    cell = cell.trim();
                    int column = Integer.parseInt(String.valueOf(cell.charAt(0)));
                    int row = Integer.parseInt(String.valueOf(cell.charAt(1)));
                    int value = Integer.parseInt(String.valueOf(cell.charAt(3)));
                    Main.box.setCellValue(column, row, value);
                }
                continue;
            }
            
            throw new ProcessArgumentException("One or more process arguments do not match the required pattern");
        }
        
        // At least box name and manager address have to be known; box is allowed to have no cells set
        if (!positionIsKnown) {
            throw new ProcessArgumentException("Box name not given in process arguments");
        }
        return;
    }
    
//    private static boolean resultAlreadySent = false;
//    
//    public static void wrapUpConnections() {
//    	if (!resultAlreadySent) {
//	    	box.printCells();
//	    	StringBuilder result = new StringBuilder();
//	    	result.append("RESULT,");
//	    	result.append(box.getName());
//	    	for(int i = 0; i < 3; i++) {
//	    		for(int j = 0; j < 3; j++) {
//	    			result.append(',');
//	    			result.append(box.getCell(j, i).getValue());
//	    		}
//	    	}
//	    	System.out.println(result.toString());
//	    	try {
//				thread.sendLine(result.toString());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    	resultAlreadySent = true;
//    	}
//    }

}
