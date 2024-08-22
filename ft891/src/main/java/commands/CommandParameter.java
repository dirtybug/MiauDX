package commands;

public class CommandParameter {
	private String name;
	private int minValue;
	private int maxValue;
	private int requiredDigits;

	public CommandParameter(String name, int minValue, int maxValue, int requiredDigits) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.requiredDigits = requiredDigits;
	}

	public String getName() {
		return name;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getRequiredDigits() {
		return requiredDigits;
	}
}