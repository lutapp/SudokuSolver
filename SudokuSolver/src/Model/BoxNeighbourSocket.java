package Model;

import java.io.IOException;
import java.net.Socket;

public class BoxNeighbourSocket extends ConnectionThread{
	
	public BoxNeighbourSocket(Socket s) throws IOException {
		super(s);
	}
	
	@Override
	public void run() {
		
	}
}
