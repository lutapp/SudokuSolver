package Model;

import java.io.IOException;

public class ManagerConnectionThread extends ConnectionThread {
	
	public ManagerConnectionThread(String address, int port) throws IOException {
		super(address, port);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				String line = this.readLine();
				if (line != null) {
					System.out.println(line);
				} else {
					break;
				}
			}
			this.closeConnection();
			System.out.println("Connection to manager closed!");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
}
