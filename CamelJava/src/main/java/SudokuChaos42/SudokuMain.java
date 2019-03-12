package SudokuChaos42;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.camel.main.Main;
import org.json.*;

public class SudokuMain {

	private static String managerURL = "";
	private static int managerPort = 0;
	private static String mqttIP = "";
	private static int mqttPort = 0;
	private static String boxname = "";
	private static String boxnameRaw = "";
	private static String initialState = "";
	private static String classpath = "";

	public static void main(String... args) {
		// Hier geht die Action los!
		try {
			parseArgs(args);
			
			// Initialisierung
			System.out.println("Sending initialization request...\n");
			parseRequestJson(initializationRequest());
			System.out.println("Request successful! Received following data:");
			print();

			// Starten des Box-Programms
			startBox();

			// Camel-Routen initialisieren - startet MQTT to FTP-Route, sendet Ready an Manager, startet nach
			// Start-Nachricht die FTP to MQTT-Route
			Main main = new Main();
			main.addRouteBuilder(new MqttToFtp(boxname, boxnameRaw, mqttIP, mqttPort));
			main.run();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(42);
		} catch (JSONException e) {
			e.printStackTrace();
			System.exit(422);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(423);
		}
	}

	private static void parseArgs(String... args) throws Exception {
		if (args.length != 3) {
			throw new Exception("Usage: SudokuChaos42.SudokuMain <box manager hostname> <box manager port> <java classpath ftp-box>");
		}
		managerURL = args[0];
		managerPort = Integer.parseInt(args[1]);
		classpath = args[2];
	}

	// Initiale Anfrage an den Boxmanager
	private static String initializationRequest() throws IOException {
		URL myurl = new URL("http://" + managerURL + ":" + managerPort + "/api/initialize");
		HttpURLConnection con = (HttpURLConnection) myurl.openConnection();

		con.setRequestMethod("GET");

		StringBuilder content;

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String line;
		content = new StringBuilder();

		while ((line = in.readLine()) != null) {
			content.append(line);
			content.append(System.lineSeparator());
		}

		return content.toString();
	}

	// Parsen der Antwort der initialen Anfrage an den Boxmanager
	private static void parseRequestJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		mqttIP = obj.getString("mqtt-ip");
		mqttPort = obj.getInt("mqtt-port");
		boxnameRaw = obj.getString("box").trim();
		boxname = "BOX_" + boxnameRaw.substring(boxnameRaw.length() - 2, boxnameRaw.length()).toUpperCase();
		if (obj.has("init")) {
			JSONArray arr = obj.getJSONArray("init");
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject value = arr.getJSONObject(i);
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 3; k++) {
						if (value.has("" + j + k)) {
							b.append(j);
							b.append(k);
							b.append(':');
							b.append(value.getInt("" + j + k));
							if (i != arr.length() - 1) {
								b.append(',');
							}
						}
					}
				}
			}
			initialState = b.toString();
		}
	}

	// Anfrage, nachdem Box initialisiert ist
	public static void sendReadyRequest() throws IOException {
		URL myurl = new URL("http://" + managerURL + "/api/ready?" + URLEncoder.encode("box=" + boxnameRaw, "UTF-8"));
		HttpURLConnection con = (HttpURLConnection) myurl.openConnection();

		con.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String line;
		StringBuilder content = new StringBuilder();

		while ((line = in.readLine()) != null) {
		}
	}

	// Box-Prozess starten
	private static void startBox() throws IOException {
		ProcessBuilder proc = new ProcessBuilder("cmd", "/c", "java", "-cp", "\"" + classpath + "\"", "Controller.Main", boxname, initialState);
		StringBuilder b = new StringBuilder();
		b.append("Executing command:\n");
		for (String single : proc.command()) {
			b.append(single);
			b.append(' ');
		}
		b.append('\n');
		System.out.println(b.toString());
		//proc.start();
	}

	private static void print() {
		System.out.println("Manager URL: " + managerURL + "\nMQTT-IP: " + mqttIP + "\nMQTT-Port: " + mqttPort
				+ "\nBoxname: " + boxname + "\nInital state: " + initialState + "\nFTP-Box Java Classpath: " + classpath
				+ '\n');
	}

}
