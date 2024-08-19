package ft891;

import commands.Command;
import commands.CommandParameter;
import commands.JsonCommandLoader;


import javax.swing.*;

import cluster.HamRadioClusterConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class FT891ControlApp extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> commandDropdown;
    private JPanel parameterPanel;
    private JButton sendCommandButton;
    private JTextArea logArea;

    private JComboBox<String> portDropdown;
    private JComboBox<Integer> baudRateDropdown;
    private JButton connectButton;

    private Map<String, Command> commandMap;
    private FT891CommManager commManager;
    private boolean isConnected = false;  // Track connection state
    private ConfigManager configManager;
    private ConnectionLogWindow connectionLogWindow;

    public FT891ControlApp() {
        super("FT-891 Control Application");
        configManager = new ConfigManager();
        setLayout(new BorderLayout());
        logArea = new JTextArea();
        commManager = new FT891CommManager(message -> this.onLog(message));
        connectionLogWindow = new ConnectionLogWindow(message -> commManager.setFrequency(message));
        
        new RepeaterTableWindow();
        

        HamRadioClusterConnection clusterConnection = new HamRadioClusterConnection(
        		configManager.getCluster(),  // Replace with actual cluster host
        		configManager.getClusterPort(),                 // Replace with actual cluster port
        		configManager.getCallsign(),       // Replace with your actual callsign
                message -> connectionLogWindow.logMessage(message), // Replace with your log handler
                (freq,callsign,location) ->connectionLogWindow.addSpot(freq,callsign,location)
        );

        clusterConnection.start();
        commandMap = new HashMap<>();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2));

        // Add COM port selection
        portDropdown = new JComboBox<>();
        List<String> availablePorts = commManager.getAvailablePorts();
        for (String port : availablePorts) {
            portDropdown.addItem(port);
        }
        topPanel.add(new JLabel("Select COM Port:"));
        topPanel.add(portDropdown);

        // Add baud rate selection
        baudRateDropdown = new JComboBox<>(new Integer[]{9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000});
        topPanel.add(new JLabel("Select Baud Rate:"));
        topPanel.add(baudRateDropdown);

        // Add connect button
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
        topPanel.add(connectButton);

        commandDropdown = new JComboBox<>();
        parameterPanel = new JPanel();
        sendCommandButton = new JButton("Send Command");
        
        
        // Load commands using JsonCommandLoader
        try {
            JsonCommandLoader loader = new JsonCommandLoader("commands.json");
            commandMap = loader.loadCommands();
            for (String commandName : commandMap.keySet()) {
                commandDropdown.addItem(commandName);
            }
        } catch (Exception e) {
        	onLog("Error loading commands: " + e.getMessage());
        }

        // Use GridBagLayout for the command panel
        JPanel commandPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add the command dropdown to the first row, spanning two columns
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        commandPanel.add(commandDropdown, gbc);

        // Add the parameter panel below the command dropdown, spanning two columns
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        commandPanel.add(parameterPanel, gbc);

        // Add the send command button, spanning two columns
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        commandPanel.add(sendCommandButton, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(commandPanel, BorderLayout.CENTER);

        loadConfig();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.SOUTH);

        // Register this class as the event handler
        commandDropdown.addActionListener(this);
        sendCommandButton.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    private void updateParameterPanel() {
        parameterPanel.removeAll();
        String selectedCommandName = (String) commandDropdown.getSelectedItem();
        Command command = commandMap.get(selectedCommandName);

        if (command != null && !command.getParameters().isEmpty()) {
            parameterPanel.setLayout(new GridLayout(command.getParameters().size(), 2));

            for (CommandParameter param : command.getParameters()) {
                parameterPanel.add(new JLabel(param.getName() + ":"));
                JTextField paramField = new JTextField();
                paramField.setName(param.getName());
                parameterPanel.add(paramField);
            }
        } else {
            parameterPanel.setLayout(new BorderLayout());
            parameterPanel.add(new JLabel("No parameters required"));
        }

        parameterPanel.revalidate();
        parameterPanel.repaint();
    }

    private void sendCommand() {
        String selectedCommandName = (String) commandDropdown.getSelectedItem();
        Command command = commandMap.get(selectedCommandName);

        if (command != null) {
            StringBuilder commandString = new StringBuilder(command.getCommand());
            for (Component comp : parameterPanel.getComponents()) {
                if (comp instanceof JTextField) {
                    JTextField field = (JTextField) comp;
                    String value = field.getText();
                    commandString.append(value);
                }
            }
            commandString.append(";");
            commManager.queueCommand(commandString.toString());
        }
    }

    private void loadConfig() {
        String savedPort = configManager.getPort();
        int savedBaudRate = configManager.getBaudRate();

        if (savedPort != null && savedBaudRate != -1) {
            portDropdown.setSelectedItem(savedPort);
            baudRateDropdown.setSelectedItem(savedBaudRate);
            onLog("Configuration loaded from " + ConfigManager.CONFIG_FILE);
        } else {
            onLog("No previous configuration found.");
        }
    }

    private void connectToTransceiver() {
        if (!isConnected) {
            String selectedPort = (String) portDropdown.getSelectedItem();
            int selectedBaudRate = (Integer) baudRateDropdown.getSelectedItem();
            commManager.connect(selectedPort, selectedBaudRate, 8, 1, "None");
            isConnected = true;
            connectButton.setText("Disconnect");

            // Save the configuration to a JSON file upon connecting
            configManager.saveConfig(selectedPort, selectedBaudRate);

        } else {
            commManager.disconnect();
            isConnected = false;
            connectButton.setText("Connect");
            if (connectionLogWindow != null) {
                connectionLogWindow.dispose();  // Close the log window on disconnect
            }
        }
    }


    public void onLog(String message) {
        logArea.append(message + "\n");

    }



    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == commandDropdown) {
            updateParameterPanel();
        } else if (source == sendCommandButton) {
            sendCommand();
        } else if (source == connectButton) {
            connectToTransceiver();
        }
    }
}
