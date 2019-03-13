package SudokuChaos42;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.main.Main;
import org.json.JSONObject;

public class ProcessorToFtp implements Processor {
	
	private String boxname = null;
	private String boxnameRaw = null;
	private String mqttIP = null;
	private int mqttPort = 0;
	private String mqttTopic = null;
	
	
	public ProcessorToFtp(String boxname, String boxnameRaw, String mqttIP, int mqttPort, String mqttTopic) {
		this.boxname = boxname;
		this.boxnameRaw = boxnameRaw;
		this.mqttIP = mqttIP;
		this.mqttPort = mqttPort;
		this.mqttTopic = mqttTopic;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		this.evaluateExchangeType(exchange);

		if (exchange.getOut().getHeader("type", String.class).equals("knowledge")) {
			this.parseKnowledgeJSON(exchange);
			return;
		}
		
		if (exchange.getOut().getHeader("type", String.class).equals("start")) {
			Main main = new Main();
			main.addRouteBuilder(new FtpToMqtt(this.boxname, this.boxnameRaw, this.mqttIP, this.mqttPort, this.mqttTopic));
			main.start();
			return;
		}
	}

	// Prüfe, welches Topic die eingehende Message hat
	private void evaluateExchangeType(Exchange exchange) {
		if (exchange.getIn().getHeader("CamelMQTTSubscribeTopic", String.class).matches("^sudoku/box_[adg][147]$")) {
			exchange.getOut().setHeader("type", "knowledge");
			return;
		}

		if (exchange.getIn().getHeader("CamelMQTTSubscribeTopic", String.class).matches("^sudoku/start$")) {
			exchange.getOut().setHeader("type", "start");
			return;
		}

		exchange.getOut().setHeader("type", "unknown");
		return;
	}

	private void parseKnowledgeJSON(Exchange exchange) {
		String subscribedBox = (String) exchange.getIn().getHeader("CamelMQTTSubscribeTopic");

		System.out.println("Subscribed Box: " + subscribedBox);
		String json = exchange.getIn().getBody(String.class);

		System.out.println("Message from subscribed Box: " + json);

		StringBuilder b = new StringBuilder();
		JSONObject obj = new JSONObject(json);
		String boxnameRaw = obj.getString("box").trim();
		String boxname = "BOX_"
				+ boxnameRaw.substring(boxnameRaw.length() - 2, boxnameRaw.length()).toUpperCase();

		b.append(boxname);
		b.append(",");
		b.append(obj.getInt("r_column"));
		b.append(",");
		b.append(obj.getInt("r_row"));
		b.append(":");
		b.append(obj.getInt("value"));

		String newKnowledge = b.toString();

		System.out.println(newKnowledge);

		exchange.getOut().setHeader(Exchange.FILE_NAME, boxname + "_" + System.currentTimeMillis());
		exchange.getOut().setBody(newKnowledge.toString(), String.class);
	}
}
