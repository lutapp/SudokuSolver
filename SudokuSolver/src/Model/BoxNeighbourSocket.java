package Model;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class BoxNeighbourSocket extends ConnectionThread{
	
	private ArrayList<String> history = new ArrayList<String>();
	
	Box box;
	
	public BoxNeighbourSocket(Socket s, Box box) throws IOException {
		super(s);
		this.box = box;
	}
	
	public void sendMessageWithHistoryCheck(String message) throws IOException {
		if (this.history.contains(message)) {
			return;
		} else {
			System.out.println("Sending message: " + message);
			this.sendLine(message);
			this.history.add(message);
		}
	}
	
	@Override
	public void run() {
		PendingMessageHandler pmh = new PendingMessageHandler(this.box);
		pmh.start();
		try {
			while(true) {
				String line = this.readLine();
				if (line != null) {
					line = line.trim();
					System.out.println("Received message: " + line);
					if (line.matches("^BOX_[ADG][147],[0-2],[0-2]:[1-9]$")) {
						if(line.charAt(4) == box.getName().charAt(4)) {
							String[] split = line.split(",");
							//System.out.println("Message: " + line + "; " + split[0] + " is in column! Column: " + split[1].substring(0,1) + " Value: " + split[2].charAt(split[2].length() - 1));
							Cell[] affectedCells = this.box.getAffectedColumn(Integer.parseInt(split[1].substring(0,1)));
							for (Cell cell: affectedCells) {
								synchronized (cell) {
									//TODO: Sch�ner machen
									cell.removeFromPossibilities(Integer.parseInt(("" + split[2].charAt(split[2].length() - 1))));
								}
							}
						}
						if (line.charAt(5) == box.getName().charAt(5)) {
							String[] split = line.split(",");
							//System.out.println("Message: " + line + "; " + split[0] + " is in row! Row: " + split[2].charAt(0) + " Value: " + split[2].charAt(split[2].length() - 1));
							Cell[] affectedCells = this.box.getAffectedRow(Integer.parseInt(("" + split[2].charAt(0))));
							for (Cell cell: affectedCells) {
								synchronized (cell) {
									//TODO: Schoener machen
									cell.removeFromPossibilities(Integer.parseInt(("" + split[2].charAt(split[2].length() - 1))));
								}
							}
						}
						PendingMessageHandler.addMessageToPending(line);
						pmh = new PendingMessageHandler(this.box);
						pmh.start();
						continue;
					}
					if (line.matches("^[A-I][1-9]:[1-9]$")) {
						PendingMessageHandler.addMessageToPending(line);
						pmh = new PendingMessageHandler(this.box);
						pmh.start();
						continue;
					}
				} else {
					break;
				}
			}
			this.closeConnection();
			System.out.println("Connection to neighbour closed!");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}