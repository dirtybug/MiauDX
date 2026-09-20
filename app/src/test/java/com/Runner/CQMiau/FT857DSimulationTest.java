package com.Runner.CQMiau;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioFactory;
import com.Runner.CQMiau.radios.RadioMode;
import com.Runner.CQMiau.radios.RadioType;
import com.Runner.CQMiau.radios.OldYeasu;
import com.Runner.CQMiau.radios.drivers.FT857D;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit Test suite simulating Yaesu FT-857 / FT-857D CAT transceiver operations.
 * Tests 5-byte binary CAT commands, frequency tuning across all HF amateur bands,
 * operating mode selections (USB, LSB, CW, AM, FM, DIG), and RadioFactory creation.
 */
public class FT857DSimulationTest {

    /**
     * Simulated test double of FT857D that captures commands sent via sendAndReceiveCommand
     * without requiring a physical USB serial connection or hardware port.
     */
    public static class SimulatedFT857D extends FT857D {
        private String lastSentCommand = null;
        private final List<String> commandHistory = new ArrayList<>();

        @Override
        protected void sendAndReceiveCommand(String command) {
            this.lastSentCommand = command;
            this.commandHistory.add(command);
        }

        public String getLastSentCommand() {
            return lastSentCommand;
        }

        public List<String> getCommandHistory() {
            return commandHistory;
        }

        public Long getMinFrequency() {
            try {
                Field f = RadioBase.class.getDeclaredField("Min");
                f.setAccessible(true);
                return (Long) f.get(this);
            } catch (Exception e) {
                return null;
            }
        }

        public Long getMaxFrequency() {
            try {
                Field f = RadioBase.class.getDeclaredField("Max");
                f.setAccessible(true);
                return (Long) f.get(this);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private SimulatedFT857D radio;

    @Before
    public void setUp() {
        radio = new SimulatedFT857D();
    }

    @Test
    public void testFT857DSpecifications() {
        // FT-857D minimum frequency is 3 kHz (3000 Hz)
        assertEquals(Long.valueOf(3000L), radio.getMinFrequency());
        // FT-857D maximum HF frequency in driver is 54 MHz (54,000,000 Hz)
        assertEquals(Long.valueOf(54000000L), radio.getMaxFrequency());
    }

    @Test
    public void testRadioFactoryCreation() {
        RadioBase factoryRadio = RadioFactory.createRadio(RadioType.FT857D);
        assertNotNull("RadioFactory should instantiate FT857D", factoryRadio);
        assertTrue("Factory instance should be FT857D", factoryRadio instanceof FT857D);
        assertTrue("FT857D should inherit from OldYeasu", factoryRadio instanceof OldYeasu);

        FT857D directRadio = new FT857D();
        assertNotNull("Direct instance should not be null", directRadio);
        assertTrue("Direct instance should be RadioBase", directRadio instanceof RadioBase);
    }

    @Test
    public void testSetFrequency20mFT8() {
        // 14.074 MHz (14074.0 kHz)
        radio.setFrequency("14074.0");

        String cmd = radio.getLastSentCommand();
        assertNotNull("Command should not be null", cmd);
        // Yaesu OldYeasu protocol sends 5 bytes as 10 hexadecimal characters
        assertEquals("Command length should be 10 hex chars (5 bytes)", 10, cmd.length());
        // 5th byte (last 2 hex characters) is CAT_FREQ_SET = 0x01
        assertTrue("Command must end with frequency set opcode 01", cmd.endsWith("01"));
    }

    @Test
    public void testSetFrequencyAcrossAllHFAmateurBands() {
        // Test tuning across all 10 HF bands supported by FT-857D
        String[] testFrequenciesKHz = {
            "1840.0",   // 160m Band FT8
            "3573.0",   // 80m Band FT8
            "7074.0",   // 40m Band FT8
            "10136.0",  // 30m Band FT8
            "14074.0",  // 20m Band FT8
            "18100.0",  // 17m Band FT8
            "21074.0",  // 15m Band FT8
            "24915.0",  // 12m Band FT8
            "28074.0",  // 10m Band FT8
            "50313.0"   // 6m Band FT8
        };

        for (String freq : testFrequenciesKHz) {
            radio.setFrequency(freq);
            String cmd = radio.getLastSentCommand();
            assertNotNull("Command for " + freq + " kHz should not be null", cmd);
            assertEquals("Length must be 10 hex characters for " + freq, 10, cmd.length());
            assertTrue("Command for " + freq + " must end with opcode 01", cmd.endsWith("01"));
        }
    }

    @Test
    public void testSetModes() {
        // Yaesu OldYeasu mode commands: 5 bytes, byte[4] = 0x07 (CAT_MODE_SET)
        // USB = 0x01 -> "0100000007"
        radio.setMode(RadioMode.USB);
        assertEquals("0100000007", radio.getLastSentCommand());

        // LSB = 0x00 -> "0000000007"
        radio.setMode(RadioMode.LSB);
        assertEquals("0000000007", radio.getLastSentCommand());

        // CW = 0x02 -> "0200000007"
        radio.setMode(RadioMode.CW);
        assertEquals("0200000007", radio.getLastSentCommand());

        // AM = 0x04 -> "0400000007"
        radio.setMode(RadioMode.AM);
        assertEquals("0400000007", radio.getLastSentCommand());

        // FM = 0x05 -> "0500000007"
        radio.setMode(RadioMode.FM);
        assertEquals("0500000007", radio.getLastSentCommand());

        // DIG = 0x06 -> "0600000007"
        radio.setMode(RadioMode.DIG);
        assertEquals("0600000007", radio.getLastSentCommand());

        // FMN = 0x08 -> "0800000007"
        radio.setMode(RadioMode.FMN);
        assertEquals("0800000007", radio.getLastSentCommand());
    }

    @Test
    public void testSetDxConvenienceMethod() {
        radio.setDx("14074.0", RadioMode.USB);

        assertEquals("History must contain 2 commands (freq + mode)", 2, radio.getCommandHistory().size());
        // First command is frequency set (ends with 01)
        assertTrue(radio.getCommandHistory().get(0).endsWith("01"));
        // Second command is mode set (0100000007 for USB)
        assertEquals("0100000007", radio.getCommandHistory().get(1));
    }
}
