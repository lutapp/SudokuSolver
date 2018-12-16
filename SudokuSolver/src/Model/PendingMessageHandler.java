package Model;

import java.util.ArrayList;

public class PendingMessageHandler extends Thread{
	
	static ArrayList<String> pendingMessages = new ArrayList<String>();
	
	private ArrayList<BoxNeighbourSocket> neighbours = new ArrayList<BoxNeighbourSocket>();
	
	public static void addMessageToPending(String message) {
		synchronized (pendingMessages) {
			pendingMessages.add(message);
			return;
		}
	}
	
	private static String getFirstPendingMessage() {
		synchronized (pendingMessages) {
			if (pendingMessages.isEmpty()) {
				return null;
			} else {
				String output = pendingMessages.get(0);
				pendingMessages.remove(0);
				return output;
			}
		}
	}
	
	public PendingMessageHandler(ArrayList<BoxNeighbourSocket> neighbours) {
		this.neighbours = neighbours;
	}
	
	@Override
	public void run() {
		String firstPending = getFirstPendingMessage();
		while (firstPending != null) {
			System.out.println(firstPending);
			firstPending = getFirstPendingMessage();
		}
	}
	
	
}
