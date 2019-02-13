package Model;

import sun.net.ftp.FtpClient;

public class FTPPollThread extends Thread {
	
	private FtpClient ftp = null;
	
	public FTPPollThread(FtpClient ftp) {
		this.ftp = ftp;
	}
	
	public void run() {
		
	}
	
}
