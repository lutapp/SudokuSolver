package SudokuChaos42;


import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;


public class FtpToMqtt extends RouteBuilder {

	private String boxname = null;
    private String boxnameRaw = null;
    private String mqttIP = null;
    private int mqttPort = 0;
	
	public FtpToMqtt(String boxname, String boxnameRaw, String mqttIP, int mqttPort) {
		this.boxname = boxname;
		this.boxnameRaw = boxnameRaw;
		this.mqttIP = mqttIP;
		this.mqttPort = mqttPort;
	}
	
    public void configure() {
    	Processor proc = new SudokuChaos42.ProcessorToMqtt(this.boxname, this.boxnameRaw);
    	
        from("ftp://127.0.0.1:21/knowledge/?username=m3ntozz911&password=testpw&passiveMode=true&idempotent=true&delay=5000&include=^" + this.boxname +"_(Result_)?[0-9]*$")
        .log("Test")
        .process(proc)
        	.choice()
        		.when(body().isNotEqualTo("duplicate"))
        			.choice()
        				.when(header("type").isEqualTo("knowledge"))
        					.to("mqtt:bar?host=tcp://" + this.mqttIP + ":" + this.mqttPort+"&publishTopicName=" + this.boxnameRaw)
        				.when(header("type").isEqualTo("result"))
        					.to("mqtt:bar?host=tcp://" + this.mqttIP + ":" + this.mqttPort+"&publishTopicName=" + this.boxnameRaw + "/result");    
    }

}
