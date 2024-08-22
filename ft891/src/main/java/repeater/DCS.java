package repeater;

public enum DCS {
    INSTANCE;

	public String getCodeFromTone(int tone) {
        int minTone = 23; // Assuming the smallest DCS tone starts at 23
        int step = 2; // If DCS tones increment by 2, like 23, 25, 26, etc.

        int index = (tone - minTone) / step;
        return String.format("%03d", index);
    }
}