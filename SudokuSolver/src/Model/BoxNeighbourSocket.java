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
	
	private void parseRelativeMessage(String line) {
		if(line.charAt(4) == box.getName().charAt(4)) {
			String[] split = line.split(",");
			Cell[] affectedCells = this.box.getAffectedColumn(split[1].charAt(0) - '0');
			for (Cell cell: affectedCells) {
				synchronized (cell) {
					cell.removeFromPossibilities(split[2].charAt(split[2].length() - 1) - '0');
				}
			}
		}
		if (line.charAt(5) == box.getName().charAt(5)) {
			String[] split = line.split(",");
			Cell[] affectedCells = this.box.getAffectedRow(split[2].charAt(0) - '0');
			for (Cell cell: affectedCells) {
				synchronized (cell) {
					cell.removeFromPossibilities(split[2].charAt(split[2].length() - 1) - '0');
				}
			}
		}
	}
	
	private String convertAbsoluteToRelativeMessage(String line) {
		char column = line.charAt(0);
		char row = line.charAt(1);
		char value = line.charAt(3);

		int relativeColumn = 0;
		int relativeRow = 0;

		StringBuilder relativeMessage = new StringBuilder();
		relativeMessage.append("BOX_");

		switch (column - 'A') {
			case(0):
			case(1):
			case(2):
				relativeMessage.append('A');
				relativeColumn = column - 'A';
				break;
			case(3):
			case(4):
			case(5):
				relativeMessage.append('D');
				relativeColumn = column - 'D';
				break;
			case(6):
			case(7):
			case(8):
				relativeMessage.append('G');
				relativeColumn = column - 'G';
				break;
		}

		switch (row - '1') {
			case(0):
			case(1):
			case(2):
				relativeMessage.append('1');
				relativeRow = row - '1';
				break;
			case(3):
			case(4):
			case(5):
				relativeMessage.append('4');
				relativeRow = row - '4';
				break;
			case(6):
			case(7):
			case(8):
				relativeMessage.append('7');
				relativeRow = row - '7';
				break;
		}

		relativeMessage.append(',');
		relativeMessage.append(relativeColumn);
		relativeMessage.append(',');
		relativeMessage.append(relativeRow);
		relativeMessage.append(':');
		relativeMessage.append(value);

		return relativeMessage.toString();
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
						this.parseRelativeMessage(line);
						PendingMessageHandler.addMessageToPending(line);
						pmh = new PendingMessageHandler(this.box);
						pmh.start();
						continue;
					}
					if (line.matches("^[A-I][1-9]:[1-9]$")) {
						this.parseRelativeMessage(this.convertAbsoluteToRelativeMessage(line));
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
