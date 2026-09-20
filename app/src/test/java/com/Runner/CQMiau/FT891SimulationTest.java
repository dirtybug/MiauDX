package com.Runner.CQMiau;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.Runner.CQMiau.radios.RadioMode;
import com.Runner.CQMiau.radios.drivers.FT891;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test suite simulating the Yaesu FT-891 CAT transceiver.
 * Validates frequency tuning, operating modes, BCD/Decimal formatting,
 * and CAT command generation matching the Yaesu FT-891 specification.
 */
public class FT891SimulationTest {

    private SimulatedFT891 ft891;

    public static class SimulatedFT891 extends FT891 {
        private String lastCommand;
        private final List<String> commandLog = new ArrayList<>();

        @Override
        protected void sendAndReceiveCommand(String command) {
            this.lastCommand = command;
            this.commandLog.add(command);
        }

        public String getLastCommand() {
            return lastCommand;
        }

        public List<String> getCommandLog() {
            return commandLog;
        }

        public long getMinFreq() {
            return Min;
        }

        public long getMaxFreq() {
            return Max;
        }

        public int getVarLen() {
            return Len;
        }
    }

    @Before
    public void setUp() {
        ft891 = new SimulatedFT891();
    }

    @Test
    public void testFT891Specifications() {
        assertEquals("FT-891 minimum frequency must be 3000 Hz (3 kHz)", 3000L, ft891.getMinFreq());
        assertEquals("FT-891 maximum frequency must be 54000000 Hz (54 MHz)", 54000000L, ft891.getMaxFreq());
        assertEquals("FT-891 command length must be 10 digits", 10, ft891.getVarLen());
    }

    @Test
    public void testSetFrequency20mFT8() {
        ft891.setFrequency("14074000");
        assertNotNull(ft891.getLastCommand());
        assertTrue("Command should start with FA", ft891.getLastCommand().startsWith("FA"));
        assertTrue("Command should end with semicolon", ft891.getLastCommand().endsWith(";"));
        assertTrue("Command should contain frequency 14074000", ft891.getLastCommand().contains("14074000"));
    }

    @Test
    public void testSetFrequencyAcrossAllHFAmateurBands() {
        String[] frequencies = {
            "1840000",   // 160m Band
            "3573000",   // 80m Band
            "7074000",   // 40m Band
            "10136000",  // 30m Band
            "14074000",  // 20m Band
            "18100000",  // 17m Band
            "21074000",  // 15m Band
            "24915000",  // 12m Band
            "28074000",  // 10m Band
            "50313000"   // 6m Band
        };

        for (String freq : frequencies) {
            ft891.setFrequency(freq);
            assertNotNull(ft891.getLastCommand());
            assertTrue(ft891.getLastCommand().startsWith("FA"));
            assertTrue(ft891.getLastCommand().endsWith(";"));
        }
        assertEquals(frequencies.length, ft891.getCommandLog().size());
    }

    @Test
    public void testSetModeSSBAndFM() {
        ft891.setMode(RadioMode.SSB);
        assertEquals("MD01;", ft891.getLastCommand());

        ft891.setMode(RadioMode.FMN);
        assertEquals("MD04;", ft891.getLastCommand());
    }

    @Test
    public void testSetDxConvenienceMethod() {
        ft891.setDx("21074000", RadioMode.SSB);
        assertEquals(2, ft891.getCommandLog().size());
        assertTrue(ft891.getCommandLog().get(0).startsWith("FA"));
        assertEquals("MD01;", ft891.getCommandLog().get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFrequencyThrows() {
        ft891.setFrequency("INVALID_FREQ");
    }
}
