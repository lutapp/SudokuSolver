package SudokuChaos42;

import java.io.IOException;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class MqttToFtp extends RouteBuilder{

	private String boxname = null;
	private String boxnameRaw = null;
	private String mqttIP = null;
    private int mqttPort = 0;
	
	public MqttToFtp(String boxname, String boxnameRaw, String mqttIP, int mqttPort) {
		this.boxname = boxname;
		this.boxnameRaw = boxnameRaw;
		this.mqttIP = mqttIP;
		this.mqttPort = mqttPort;
	}
	
    public void configure() throws IOException {
    	
    	Processor proc = new SudokuChaos42.ProcessorToFtp(this.boxname, this.boxnameRaw, this.mqttIP, this.mqttPort);
        
        from("mqtt:bar?host=tcp://" + this.mqttIP + ":" + this.mqttPort + "&subscribeTopicNames=sudoku/+")
        .process(proc)
        .choice()
        	.when(header("type").isEqualTo("knowledge"))
        		.to("ftp://127.0.0.1:21/knowledge/?username=m3ntozz911&password=testpw&passiveMode=true&idempotent=true");
        
        SudokuMain.sendReadyRequest();
    	
    }
}
