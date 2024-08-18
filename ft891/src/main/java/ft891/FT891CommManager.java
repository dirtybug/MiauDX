package ft891;

import com.fazecast.jSerialComm.SerialPort;

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

    private String currentFrequency;
	private FT891ControlApp control;

    public FT891CommManager(FT891ControlApp control) {
        this.control = control;
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
                control.onLog("Connected to " + portName);

                // Send the "ID;" command and verify the response
                sendCommand("ID;");
                String response = readResponse();
                if (verifyID(response)) {
                    control.onLog("ID verification successful.");

                    // Send the "FA;" command after successful ID verification
                    sendCommand("FA;");
                    this.currentFrequency = readResponse();
                    control.onLog("FA command sent.");
                } else {
                    control.onLog("ID verification failed. Disconnecting...");
                    disconnect();
                    return;
                }

                // Start the thread
                this.start();
            } else {
                control.onLog("Failed to open port " + portName);
            }
        } catch (Exception e) {
            control.onLog("Error connecting to port: " + e.getMessage());
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
                    control.onLog(cmdResponse); // Send the response back to the UI or main app
                }
            }
        } catch (InterruptedException e) {
            control.onLog("Command sender thread interrupted.");
        }
    }

    public void disconnect() {
        this.interrupt();

        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            control.onLog("Disconnected.");
        }
    }

    public void queueCommand(String command) {
        if (!command.endsWith(";")) {
            command += ";";
        }
        try {
            commandQueue.put(command);
            control.onLog("Command queued: " + command);
        } catch (InterruptedException e) {
            control.onLog("Failed to queue command: " + e.getMessage());
        }
    }

    private void sendCommand(String command) {
        if (serialPort == null || !serialPort.isOpen()) {
            control.onLog("Not connected to any port.");
            return;
        }

        try {
            outputStream.write(command.getBytes());
            control.onLog("Command sent: " + command);
        } catch (Exception e) {
            control.onLog("Failed to send command: " + e.getMessage());
        }
    }

    private String readResponse() {
        try {
            StringBuilder response = new StringBuilder();
            int data;
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 2000) { // Wait up to 2 seconds for a response
                if ((data = inputStream.read()) != -1) {
                    char c = (char) data;
                    response.append(c);
                    if (c == ';') {  // Assuming the response ends with a semicolon
                        break;
                    }
                }
            }

            if (response.length() > 0) {
                // Log the raw response
                control.onLog("Raw response received: " + response);

                // Remove the first two characters and the semicolon
                String processedResponse = response.toString();
                if (processedResponse.length() > 2 && processedResponse.endsWith(";")) {
                    processedResponse = processedResponse.substring(2, processedResponse.length() - 1);
                }

                // Log the processed response
                control.onLog("Processed response: " + processedResponse);

                return processedResponse;
            }

            return null;
        } catch (Exception e) {
            control.onLog("Failed to read response: " + e.getMessage());
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
}
