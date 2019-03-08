package SudokuChaos42;

import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessorToMqtt implements Processor{
	
	private String boxname = null;
	private String boxnameRaw = null;
	private static ArrayList<String> individualFileContent = new ArrayList<>();
	
	public ProcessorToMqtt(String boxname, String boxnameRaw) {
		this.boxname = boxname;
		this.boxnameRaw = boxnameRaw;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		String filename = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
		String fileContent = exchange.getIn().getBody(String.class);
		
		if (individualFileContent.contains(fileContent)) {
			exchange.getOut().setBody("duplicate", String.class);
			return;
		} else {
			individualFileContent.add(fileContent);
		}
		
		StringBuilder b = new StringBuilder();
		
		if(filename.matches("^" + this.boxname + "_[0-9]+$")) {
			System.out.println("Filename: " + filename + "\nFile Content vorher:\n" + fileContent + '\n');
			b.append("{\n");
			b.append("    \"box\" : \"");
			b.append(this.boxnameRaw);
			b.append("\",\n");
			b.append("    \"r_column\" : ");
			b.append(fileContent.charAt(7));
			b.append(",\n");
			b.append("    \"r_row\" : ");
			b.append(fileContent.charAt(9));
			b.append(",\n");
			b.append("    \"value\" : ");
			b.append(fileContent.charAt(11));
			b.append("\n}");
			System.out.println("FileContent nachher:\n" + b.toString() + '\n');
			exchange.getOut().setHeader("type", "knowledge");
		}
		if(filename.matches("^" + this.boxname + "_Result_[0-9]+$")) {
			System.out.println("Filename: " + filename + "\nFile Content vorher:\n" + fileContent + '\n');
			b.append("{\n");
			b.append("    \"box\" : \"");
			b.append(this.boxnameRaw);
			b.append("\",\n");
			b.append("    \"result\" : [");
			b.append(fileContent.substring(14, fileContent.length()));
			b.append("]\n}");
			System.out.println("FileContent nachher:\n" + b.toString() + '\n');
			exchange.getOut().setHeader("type", "result");
		}
		exchange.getOut().setBody(b.toString(), String.class);
	
	}

}
