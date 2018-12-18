package Model;

import java.io.IOException;
import java.net.Socket;

public class ManagerConnectionThread extends ConnectionThread {
	
	Box box;
	
	public ManagerConnectionThread(String address, int port, Box box) throws IOException {
		super(address, port);
		this.box = box;
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
						System.out.println("This box name is already assigned. Closing connection and terminating...");
						break;
					}
					if(line.matches("^([12]?[0-9]?[0-9].){3}[12]?[0-9]?[0-9],\\s*[0-9]+$")) {
						String[] parts = line.split(",");
						String address = null;
						int port = -1;
						if (parts.length >= 2) {
							address = parts[0].trim();
							port = Integer.parseInt(parts[1].trim());
						}
						Socket s = new Socket(address, port);
						BoxNeighbourSocket neighbour = new BoxNeighbourSocket(s, this.box);
						this.box.addNeighbour(neighbour);
						System.out.println("Established connection with neighbour " + address + " on port " + port);
					}
					
				} else {
					break;
				}
			}
			this.closeConnection();
			System.out.println("Connection to manager closed!");
		} catch (IOException ex) {
			ex.printStackTrace();
			//System.exit(0);
		}
	}
	
}
