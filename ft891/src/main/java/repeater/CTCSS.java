package repeater;

import java.util.Arrays;
import java.util.List;

public enum CTCSS {
    INSTANCE;

    // List of CTCSS tones
    private final List<Double> tones = Arrays.asList(
        67.0, 69.3, 71.9, 74.4, 77.0, 79.7, 82.5, 85.4, 88.5, 91.5, 94.8, 97.4, 100.0, 103.5, 107.2, 110.9,
        114.8, 118.8, 123.0, 127.3, 131.8, 136.5, 141.3, 146.2, 151.4, 156.7, 162.2, 167.9, 173.8, 179.9,
        186.2, 192.8, 203.5, 206.5, 210.7, 218.1, 225.7, 229.1, 233.6, 241.8, 250.3, 254.1
    );

    // Method to get the code from the tone
    public String getCodeFromFrequency(double frequency) {
        int index = tones.indexOf(frequency);
        if (index == -1) {
            return "Unknown"; // Return "Unknown" if the tone is not found
        }
        return String.format("%03d", index); // Format the index as a 3-digit, zero-padded string
    }

    public List<Double> getTones() {
        return tones;
    }
}
