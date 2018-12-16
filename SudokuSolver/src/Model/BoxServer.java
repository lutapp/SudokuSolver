package Model;

import java.io.IOException;
import java.net.ServerSocket;

public class BoxServer extends Thread{
	
	private ServerSocket ss;
	private Box box;
	
	public BoxServer(Box box) throws IOException{
		this.ss = new ServerSocket(0);
		this.box = box;
	}
	
	public int getPort() {
		return this.ss.getLocalPort();
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				BoxNeighbourSocket s = new BoxNeighbourSocket(this.ss.accept(), this.box);
				this.box.addNeighbour(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
