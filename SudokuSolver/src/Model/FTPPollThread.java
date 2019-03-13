package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;

public class FTPPollThread extends Thread {

	private Box box = null;
	private FtpClient ftp = null;
	private String path = null;
	private String regex = null;
	private boolean isFinished = false;
	private ArrayList<String> knownFiles = new ArrayList<>();

	public FTPPollThread(Box box, FtpClient ftp, String path, ArrayList<String> neighbourNames) {
		this.box = box;
		this.ftp = ftp;
		this.path = path;

		StringBuilder b = new StringBuilder();
		b.append("^(");
		for (int i = 0; i < neighbourNames.size(); i++) {
			b.append('(');
			b.append(neighbourNames.get(i));
			b.append(')');
			if (i != neighbourNames.size() - 1) {
				b.append('|');
			}
		}
		b.append(")_[0-9]+$");
		this.regex = b.toString();
		System.out.println("Starting FTP polling thread...");
	}

	public void run() {
		while (!isFinished) {
			synchronized (this.ftp) {
				try {
					ArrayList<String> newFiles = this.readDirContent();
					for (String file : newFiles) {
						this.readFile(file);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(866); // 866 ist sogar noch cooler
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Liest Dateinamen (ohne Verzeichnisse) ein und gibt sie in einer Liste aus
	private ArrayList<String> readDirContent() throws FtpProtocolException, IOException {
		Iterator<FtpDirEntry> dirlist = this.ftp.listFiles(this.path);
		ArrayList<String> filelist = new ArrayList<>();

		while (dirlist.hasNext()) {
			FtpDirEntry current = dirlist.next();
			if (current.getName().matches("^BOX_[ADG][147]_[0-9]+$") && !this.knownFiles.contains(current.getName())) {
				filelist.add(current.getName());
			}
		}
		this.ftp.completePending();
		return filelist;
	}

	// Liest Dateiinhalte aus
	private void readFile(String name) throws FtpProtocolException, IOException {
		this.ftp.changeDirectory(this.path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(ftp.getFileStream(name)));
		StringBuilder b = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			b.append(line);
			line = reader.readLine();
		}
		reader.close();
		ftp.completePending();

		System.out.println("\nRecognized new relevant file: " + name);
		String content = b.toString().trim();
		System.out.println("\nFile contains: " + content + "\nParsing content...");
		this.processFileContent(content);
		System.out.println("Parsing completed!");

		this.knownFiles.add(name);
		this.ftp.changeDirectory("/");
	}

	private void processFileContent(String line) {
		if (line.charAt(4) == this.box.getName().charAt(4)) {
			String[] split = line.split(",");
			Cell[] affectedCells = this.box.getAffectedColumn(split[1].charAt(0) - '0');
			for (Cell cell : affectedCells) {
				cell.removeFromPossibilities(split[2].charAt(split[2].length() - 1) - '0');
			}
		}
		if (line.charAt(5) == this.box.getName().charAt(5)) {
			String[] split = line.split(",");
			Cell[] affectedCells = this.box.getAffectedRow(split[2].charAt(0) - '0');
			for (Cell cell : affectedCells) {
				cell.removeFromPossibilities(split[2].charAt(split[2].length() - 1) - '0');
			}
		}
	}
	
	public void wrapUpConnections() {
		this.isFinished = true;
	}

}
