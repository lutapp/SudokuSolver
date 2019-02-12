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
	
	@Override
	public void run() {
//		synchronized(this.box.getNeighbours()) {
//			ArrayList<BoxNeighbourSocket> neighbours = this.box.getNeighbours();
//			synchronized(pendingMessages) {
//				for(String message: pendingMessages) {
//					for(BoxNeighbourSocket neighbour: neighbours) {
//						try {
//							neighbour.sendMessageWithHistoryCheck(message);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		}
	}
	
	
}
