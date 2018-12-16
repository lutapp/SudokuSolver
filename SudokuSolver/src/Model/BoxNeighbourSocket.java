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
	
	public void appendToHistory(String message) {
			this.history.add(message);
			return;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				String line = this.readLine();
				if (line != null) {
					line = line.trim();
					System.out.println("Server responded: " + line);
					if(line.equals("Someone else is responsible for this box name")) {
						break;
					}
					if(line.matches("^([12]?[0-9]?[0-9].){3}[12]?[0-9]?[0-9],\\s*[0-9]+$")) {
						
						break;
					}
					
				} else {
					break;
				}
			}
			this.closeConnection();
			System.out.println("Connection to manager closed!");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
