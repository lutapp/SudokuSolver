package Model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionThread extends Thread{
	
	String address;
	int port;
	Socket socket;
	protected BufferedReader in = null;
	protected DataOutputStream out = null;
	
	public ConnectionThread(String address, int port) throws IOException {
		this.address = address;
		this.port = port;
		this.setupConnection();
		this.setupStreams();
	}
	
	public ConnectionThread(Socket s) throws IOException {
		this.socket = s;
		this.setupStreams();
	}
	
	private void setupConnection() throws IOException {
		socket = new Socket();
		this.socket.connect(new InetSocketAddress(this.address, this.port));
	}
	
	private void setupStreams() throws IOException {
		this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new DataOutputStream(this.socket.getOutputStream());
	}
	
	protected String readLine() throws IOException {
		String line = null;
		line = this.in.readLine();
		return line;
	}
	
	public boolean sendLine(String line) throws IOException {
		this.out.writeBytes(line);
		this.out.writeBytes("\n");
		this.out.flush();
		return true;
	}
	
	public void closeConnection() throws IOException {
		this.out.flush();
		this.out.close();
		this.in.close();
		this.socket.close();
	}
	
	@Override
	public void run() {
		
	}
	
}
