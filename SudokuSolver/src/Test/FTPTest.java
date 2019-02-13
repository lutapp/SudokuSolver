package Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpProtocolException;



public class FTPTest {

    public static void main(String[] args) {
    	InputStream in = null;
        BufferedReader reader = null;
        OutputStream out = null;
        DataOutputStream writer = null;
        StringBuilder b = new StringBuilder();
        
        // Das hier ist ein try-with-resources-Statement, d.h. sollte eine Exception auftreten,
        // werden die als Parameter übergebenen Ressourcen (sofern das Interface Closeable im-
        // plementiert wurde) ordnungsgemäß geschlossen; allerdings sind die hier instanziierten
        // Variablen Konstanten, also keine Neuzuweisung
        try (FtpClient ftp = FtpClient.create()) {
            // Fun Fact: den String()-Konstruktor aufzurufen ist sogar schlechter, da
            // der als Parameter übergebene String per pass-by-value an den Konstruktor
            // übergeben wird, also noch eine Kopie erstellt wird
            FTPTest.connect(ftp, "halexander.spdns.de", "m3ntozz911", "S3b4st!anIstT0ll".toCharArray(), b);
            
            // Dateien auslesen
            ArrayList<String> filelist = FTPTest.readDirContent(ftp, "/", b);
            for (String single: filelist) {
                FTPTest.readFile(ftp, single, reader, in, b);
            }
            
            // Datei speichern
            String uploadfolder = "/uploadfolder/test" + System.currentTimeMillis();
            String content = "sdlfjls;adf\n"
                    + "lasjfdlsadf\n"
                    + "ksafjlasdjfls\n"
                    + "skfjdjsflsa\n"
                    + "safdkjlsa;fjsa\n"
                    + "sakfljsl\n"
                    + "sadlfkjsalfjlsdf\n"
                    + "sakfjsldaf\n"
                    + "sakfljsadlf\n"
                    + "sakfljads\n";
            for (int i = 0; i < 20; i++) {
                FTPTest.putFile(ftp, uploadfolder, "Test_" + System.currentTimeMillis()+ ".txt", content, out, writer, b);
            }
            b.append('\n');
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    b.append("Closed InputStream\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    b.append("Closed OutputStream\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                    b.append("Closed BufferedReader\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                    b.append("Closed DataOutputStream\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(b.toString());
        }
    }
    
    static void connect(FtpClient ftp, String url, String login, char[] pass, StringBuilder b) throws FtpProtocolException, IOException {
        ftp.connect(new InetSocketAddress(url, 21))
                .login(login, pass);
        b.append(ftp.getLastResponseString());
        b.append('\n');
    }
    
    // Liest Dateinamen (ohne Verzeichnisse) ein und gibt sie in einer Liste aus
    static ArrayList<String> readDirContent(FtpClient ftp, String path, StringBuilder b) throws FtpProtocolException, IOException {
        Iterator<FtpDirEntry> dirlist = ftp.listFiles(path);
        ArrayList<String> filelist = new ArrayList<>();
        
        while (dirlist.hasNext()) {
            FtpDirEntry current = dirlist.next();
            if (current.getType() == FtpDirEntry.Type.FILE) {
                filelist.add(current.getName());
                b.append(current.getName());
                b.append(" is a file");
                b.append('\n');
            } else {
                b.append(current.getName());
                b.append(" is not a file");
                b.append('\n');
            }
        }
        b.append('\n');
        
        ftp.completePending();
        
        for (String single: filelist) {
            b.append("filelist contains: ");
            b.append(single);
            b.append('\n');
        }
        b.append('\n');
        
        return filelist;
    }
    
    // Liest Dateiinhalte aus
    static void readFile(FtpClient ftp, String name, BufferedReader reader, InputStream in, StringBuilder b) throws FtpProtocolException, IOException {
        reader = new BufferedReader(new InputStreamReader(in = ftp.getFileStream(name)));
        b.append("File ");
        b.append(name);
        b.append(" contains:");
        b.append('\n');
        String line = reader.readLine();
        while (line != null) {
            b.append(line);
            b.append('\n');
            line = reader.readLine();
        }
        b.append('\n');
        
        in.close();
        reader.close();
        ftp.completePending();
    }
    
    static void putFile(FtpClient ftp, String path, String name, String content, OutputStream out, DataOutputStream writer, StringBuilder b) throws FtpProtocolException, IOException {
        try {
            ftp.changeDirectory(path);
        } catch(FtpProtocolException e) {
            ftp.makeDirectory(path);
            ftp.changeDirectory(path);
        }
        out = ftp.putFileStream(name);
        writer = new DataOutputStream(out);
        writer.writeBytes(content);
        writer.flush();
        out.close();
        writer.close();
        ftp.changeDirectory("/");
        
        StringBuilder pathnameFormatted = new StringBuilder();
        pathnameFormatted.append('/');
        for (String sub: path.split("/")) {
            if (sub.equals("")) {
                continue;
            }
            pathnameFormatted.append(sub);
            pathnameFormatted.append('/');
        }
        pathnameFormatted.append(name);
        
        b.append("Created file: ");
        b.append(pathnameFormatted.toString());
        b.append('\n');
        ftp.completePending();
    }
}
