package commands;


public class Command {
    private String command;
    private String name;
    private java.util.List<CommandParameter> parameters;

    public Command(String command, String name) {
        this.command = command;
        this.name = name;
        this.parameters = new java.util.ArrayList<>();
    }

    public void addParameter(CommandParameter param) {
        parameters.add(param);
    }

    public String getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public java.util.List<CommandParameter> getParameters() {
        return parameters;
    }
}