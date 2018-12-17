package Model;

import java.io.IOException;
import java.util.ArrayList;

public class PendingMessageHandler extends Thread{
	
	static ArrayList<String> pendingMessages = new ArrayList<String>();
	
	private Box box;
	
	public PendingMessageHandler(Box box) {
		this.box = box;
	}
	
	public static void addMessageToPending(String message) {
		synchronized (pendingMessages) {
			pendingMessages.add(message);
			return;
		}
	}
	
	/*private static String getFirstPendingMessage() {
		synchronized (pendingMessages) {
			if (!pendingMessages.isEmpty()) {
				return pendingMessages.get(0);
			} else {
				return null;
			}
		}
	}
	
	private static void removeFirstPendingMessage() {
		synchronized (pendingMessages) {
			if (!pendingMessages.isEmpty()) {
				pendingMessages.remove(0);
			}
		}
	}*/
	
	@Override
	public void run() {
		synchronized(this.box.getNeighbours()) {
			ArrayList<BoxNeighbourSocket> neighbours = this.box.getNeighbours();
			/*if (neighbours.isEmpty()) {
				return;
			} else {
				while (getFirstPendingMessage() != null) {
					for (BoxNeighbourSocket neighbour: neighbours) {
						try {
							neighbour.sendMessageWithHistoryCheck(getFirstPendingMessage());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					removeFirstPendingMessage();
				}*/
			synchronized(pendingMessages) {
				for(String message: pendingMessages) {
					for(BoxNeighbourSocket neighbour: neighbours) {
						try {
							neighbour.sendMessageWithHistoryCheck(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	
}
