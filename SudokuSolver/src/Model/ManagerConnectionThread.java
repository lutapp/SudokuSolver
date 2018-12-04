package Model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagerConnectionThread extends Thread {
	
	String address;
	int port;
	Socket managerClient;
	protected BufferedReader in = null;
	protected DataOutputStream out = null;
	boolean connected = false;
	
	public ManagerConnectionThread(String address, int port) throws IOException {
		this.address = address;
		this.port = port;
		managerClient = new Socket();
	}
	
	private boolean setupConnection() throws IOException {
		this.managerClient.connect(new InetSocketAddress(this.address, this.port));
		this.in = new BufferedReader(new InputStreamReader(this.managerClient.getInputStream()));
		this.out = new DataOutputStream(this.managerClient.getOutputStream());
		this.connected = true;
		return true;
	}
	
	protected String readLine() throws IOException {
		String line = null;
		line = this.in.readLine();
		return line;
	}

	public boolean sendLine(String line) throws IOException {
		while (!connected) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
				Logger.getLogger(ManagerConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		this.out.writeBytes(line);
		this.out.writeBytes("\n");
		this.out.flush();
		return true;
	}
	
	@Override
	public void run() {
		try {
			this.setupConnection();
			while(true) {
				System.out.println(this.readLine());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
}
