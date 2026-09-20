package com.Runner.CQMiau;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BandDetectorTest {

    // Helper logic to convert frequency in kHz to standard ham band designation
    public static String getBandFromKHz(double freqKHz) {
        if (freqKHz >= 1800 && freqKHz <= 2000) return "160m";
        if (freqKHz >= 3500 && freqKHz <= 3800) return "80m";
        if (freqKHz >= 5351.5 && freqKHz <= 5366.5) return "60m";
        if (freqKHz >= 7000 && freqKHz <= 7300) return "40m";
        if (freqKHz >= 10100 && freqKHz <= 10150) return "30m";
        if (freqKHz >= 14000 && freqKHz <= 14350) return "20m";
        if (freqKHz >= 18068 && freqKHz <= 18168) return "17m";
        if (freqKHz >= 21000 && freqKHz <= 21450) return "15m";
        if (freqKHz >= 24890 && freqKHz <= 24990) return "12m";
        if (freqKHz >= 28000 && freqKHz <= 29700) return "10m";
        if (freqKHz >= 50000 && freqKHz <= 54000) return "6m";
        if (freqKHz >= 144000 && freqKHz <= 146000) return "2m";
        if (freqKHz >= 430000 && freqKHz <= 440000) return "70cm";
        return "GEN";
    }

    @Test
    public void testStandardHamBands() {
        assertEquals("160m", getBandFromKHz(1840));
        assertEquals("80m", getBandFromKHz(3573));
        assertEquals("40m", getBandFromKHz(7074));
        assertEquals("20m", getBandFromKHz(14074));
        assertEquals("15m", getBandFromKHz(21074));
        assertEquals("10m", getBandFromKHz(28074));
        assertEquals("6m", getBandFromKHz(50313));
        assertEquals("2m", getBandFromKHz(144300));
        assertEquals("70cm", getBandFromKHz(432200));
        assertEquals("GEN", getBandFromKHz(100000));
    }
}
