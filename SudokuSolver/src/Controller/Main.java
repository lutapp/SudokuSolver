package Controller;

import Exception.ProcessArgumentException;
import Model.Box;
import Model.ManagerConnectionThread;
import Model.PendingMessageHandler;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;

public class Main {
    
    static Box box;
    static String managerAddress;
    static int managerPort;
    static ManagerConnectionThread thread = null;
    

    public static void main(String[] args) {
    	try {
    		box = new Box();
            parseArgs(args);
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
			System.out.println("I am " + box.getName() + "; Connecting to manager");
			thread = new ManagerConnectionThread(managerAddress, managerPort, box);
			thread.start();
			String messageToManager = box.getName() + ", " + Inet4Address.getLocalHost().getHostAddress() + ", " + box.getServerPort();
			thread.sendLine(messageToManager);
			ArrayList<String> neighbours = box.getNeighbourNames();
			for (String neighbour: neighbours) {
				System.out.println(neighbour);
				thread.sendLine(neighbour);
			}
			
			// TODO: Implement sockets and proper concurrency measures to avoid locking
			// Connect to Manager and receive neighbour addresses;
			// Assign neighbour connections to box;
			// Propagate initial state to neighbours;
			// Receive and parse messages from neighbours;
			// Apply gained knowledge to possibleValues list in Cell class;
			// Propagate new cell value to neighbours if only one possibility remains
			// Repeat until finished
			// Be able to recognize finished state
			// Send end result to manager
			// Terminate all connections
    	} catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void parseArgs(String[] args) throws ProcessArgumentException {
        boolean positionIsKnown = false;
        boolean managerAddressIsKnown = false;
        
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
            
            // URI of manager
            if (arg.matches("^(tcp://)([a-zA-Z0-9.:-]+):([0-9]+)$")) {
                Main.managerAddress = arg.substring(6, arg.lastIndexOf(":"));
                Main.managerPort = Integer.parseInt(arg.substring(arg.lastIndexOf(":") + 1));
                managerAddressIsKnown = true;
                continue;
            }
            
            throw new ProcessArgumentException("One or more process arguments do not match the required pattern");
        }
        
        // At least box name and manager address have to be known; box is allowed to have no cells set
        if (!positionIsKnown) {
            throw new ProcessArgumentException("Box name not given in process arguments");
        }
        if (!managerAddressIsKnown) {
            throw new ProcessArgumentException("Manager address not given in process arguments");
        }
        return;
    }
    
    private static boolean resultAlreadySent = false;
    
    public static void wrapUpConnections() {
    	if (!resultAlreadySent) {
	    	box.printCells();
	    	StringBuilder result = new StringBuilder();
	    	result.append("RESULT,");
	    	result.append(box.getName());
	    	for(int i = 0; i < 3; i++) {
	    		for(int j = 0; j < 3; j++) {
	    			result.append(',');
	    			result.append(box.getCell(j, i).getValue());
	    		}
	    	}
	    	System.out.println(result.toString());
	    	try {
				thread.sendLine(result.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	resultAlreadySent = true;
	    	try {
				Thread.sleep(20000);
				System.exit(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}
