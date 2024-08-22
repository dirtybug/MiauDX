package ft891;

import com.fazecast.jSerialComm.SerialPort;

import repeater.DCS;
import repeater.CTCSS;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FT891CommManager extends Thread {

	private SerialPort serialPort;
	private OutputStream outputStream;
	private InputStream inputStream;
	private BlockingQueue<String> commandQueue;
	private LogCallback logCallback;
	private String currentFrequency;

	public interface LogCallback {
		void onLog(String message);
	}

	public FT891CommManager(LogCallback logCallback) {
		this.logCallback = logCallback;
		this.commandQueue = new LinkedBlockingQueue<>();
	}

	public List<String> getAvailablePorts() {
		List<String> portList = new ArrayList<>();
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort port : ports) {
			portList.add(port.getSystemPortName());
		}
		return portList;
	}

	public void connect(String portName, int baudRate, int dataBits, int stopBits, String parity) {
		try {
			serialPort = SerialPort.getCommPort(portName);
			serialPort.setComPortParameters(baudRate, dataBits, stopBits, getParity(parity));
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);

			if (serialPort.openPort()) {
				outputStream = serialPort.getOutputStream();
				inputStream = serialPort.getInputStream();
				logCallback.onLog("Connected to " + portName);

				// Send the "ID;" command and verify the response
				sendAndReceiveCommand("ID;");

				// Send the "FA;" command after successful ID verification
				sendAndReceiveCommand("FA;");

				// Start the thread
				this.start();
			} else {
				logCallback.onLog("Failed to open port " + portName);
			}
		} catch (Exception e) {
			logCallback.onLog("Error connecting to port: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				String command = commandQueue.take();
				sendCommand(command);

				// Wait for a response after sending the command
				String cmdResponse = readResponse();
				if (cmdResponse != null) {
					logCallback.onLog(cmdResponse); // Send the response back to the UI or main app
				}
			}
		} catch (InterruptedException e) {
			logCallback.onLog("Command sender thread interrupted.");
		}
	}

	public void disconnect() {
		this.interrupt();

		if (serialPort != null && serialPort.isOpen()) {
			serialPort.closePort();
			logCallback.onLog("Disconnected.");
		}
	}

	public void queueCommand(String command) {
		if (!command.endsWith(";")) {
			command += ";";
		}
		try {
			commandQueue.put(command);

		} catch (InterruptedException e) {
			logCallback.onLog("Failed to queue command: " + e.getMessage());
		}
	}

	private void sendCommand(String command) {
		if (serialPort == null || !serialPort.isOpen()) {
			logCallback.onLog("Not connected to any port.");
			return;
		}

		try {
			outputStream.write(command.getBytes());
			logCallback.onLog("Command sent: " + command);
		} catch (Exception e) {
			logCallback.onLog("Failed to send command: " + e.getMessage());
		}
	}

	private String readResponse() {
		try {
			StringBuilder response = new StringBuilder();
			int data;
			long startTime = System.currentTimeMillis();

			while (System.currentTimeMillis() - startTime < 100) { // Wait up to 2 seconds for a response
				if ((data = inputStream.read()) != -1) {
					char c = (char) data;
					response.append(c);
					if (c == ';') { // Assuming the response ends with a semicolon
						break;
					}
				}
			}

			if (response.length() > 0) {
				// Log the raw response
				logCallback.onLog("Raw response received: " + response);

				// Remove the first two characters and the semicolon
				String processedResponse = response.toString();
				if (processedResponse.length() > 2 && processedResponse.endsWith(";")) {
					processedResponse = processedResponse.substring(2, processedResponse.length() - 1);
				}

				// Log the processed response
				logCallback.onLog("Processed response: " + processedResponse);

				return processedResponse;
			}

			return null;
		} catch (Exception e) {
			logCallback.onLog("Failed to read response: " + e.getMessage());
			return null;
		}
	}

	private boolean verifyID(String response) {
		if (response != null && response.length() > 2) {
			return "0650".equals(response);
		}
		return false;
	}

	private int getParity(String parity) {
		switch (parity.toUpperCase()) {
		case "EVEN":
			return SerialPort.EVEN_PARITY;
		case "ODD":
			return SerialPort.ODD_PARITY;
		case "MARK":
			return SerialPort.MARK_PARITY;
		case "SPACE":
			return SerialPort.SPACE_PARITY;
		default:
			return SerialPort.NO_PARITY;
		}
	}

	public void setFrequency(String frequency) {
		sendAndReceiveCommand("FA" + frequency + ";");
	}

	private void setMode(String mode) {
		String command = "";

		switch (mode.toUpperCase()) {
		case "FM ANALOG":
			command = "MD04;"; // Example command for FM mode
			break;
		case "SSB":
			command = "MD01;"; // Example command for AM mode
			break;
		case "DMR":
			command = "MD05;"; // Example command for DMR mode (assuming it exists)
			break;
		// Add other modes as needed
		default:
			logCallback.onLog("Unsupported mode: " + mode);
			return;
		}

		sendAndReceiveCommand(command);
		logCallback.onLog("Mode set to: " + mode);
	}

	private void setDCS(String dcsValue) {
		try {
			int dcsTone = (int) Math.round(Double.parseDouble(dcsValue)); // Round the double to the nearest integer
			String code = DCS.INSTANCE.getCodeFromTone(dcsTone);

			if (code.equals("Unknown")) {
				logCallback.onLog("Invalid DCS value: " + dcsValue);
				return;
			}

			// Ensure the code is three digits, padded with zeros
			String formattedCode = String.format("%03d", Integer.parseInt(code));
			String command = "CN01" + formattedCode + ";"; // Command for setting DCS tone
			sendAndReceiveCommand(command);
			logCallback.onLog("DCS set to: " + dcsValue + " (Code: " + formattedCode + ")");
		} catch (NumberFormatException e) {
			logCallback.onLog("Failed to parse DCS value: " + dcsValue);
		}
	}

	private void setCTCSSTone(String toneValue) {
		try {
			double ctcssTone = Double.parseDouble(toneValue);
			String code = CTCSS.INSTANCE.getCodeFromFrequency(ctcssTone);

			if (code.equals("Unknown")) {
				logCallback.onLog("Invalid CTCSS value: " + toneValue);
				return;
			}

			// Ensure the code is three digits, padded with zeros
			String formattedCode = String.format("%03d", Integer.parseInt(code));
			String command = "CN00" + formattedCode + ";"; // Command for setting CTCSS tone
			sendAndReceiveCommand(command);
			logCallback.onLog("CTCSS tone set to: " + toneValue + " Hz (Code: " + formattedCode + ")");
		} catch (NumberFormatException e) {
			logCallback.onLog("Failed to parse CTCSS value: " + toneValue);
		}
	}

	public void setRepeater(String frequency, String mode, String dcs, String tone) {
		setFrequency(frequency);
		setMode(mode);
		setDCS(dcs);
		setCTCSSTone(tone);
	}

	public void setDx(String frequency, String mode) {
		setFrequency(frequency);
		setMode(mode);

	}

	private void sendAndReceiveCommand(String command) {
		queueCommand(command);

	}

}
