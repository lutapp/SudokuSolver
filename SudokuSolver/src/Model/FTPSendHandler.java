package Model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

public class FTPSendHandler {

	private FtpClient ftp = null;
	private OutputStream out = null;
	private DataOutputStream writer = null;

	public FTPSendHandler(FtpClient ftp) {
		this.ftp = ftp;
	}

	public void putFile(String path, String name, String content) throws FtpProtocolException, IOException {
		synchronized (this.ftp) {
			try {
				this.ftp.changeDirectory(path);
			} catch (FtpProtocolException e) {
				this.ftp.makeDirectory(path);
				this.ftp.changeDirectory(path);
			}
			String correctName = name + '_' + System.currentTimeMillis();
			System.out.println("\nUploading new file: " + correctName);
			this.out = this.ftp.putFileStream(correctName + "_temp");			
			this.writer = new DataOutputStream(out);
			this.writer.writeBytes(content);
			this.writer.flush();
			this.out.close();
			this.writer.close();
			this.ftp.completePending();
			this.ftp.rename(correctName + "_temp", correctName);
			this.ftp.changeDirectory("/");
			System.out.println("Upload successful!");
		}
	}

}
