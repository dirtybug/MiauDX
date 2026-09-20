package com.Runner.CQMiau;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.Runner.CQMiau.radios.RadioBase;
import com.Runner.CQMiau.radios.RadioMode;

import org.junit.Before;
import org.junit.Test;

public class RadioBaseTest {

    private TestRadio radio;

    // Concrete test implementation of abstract RadioBase
    private static class TestRadio extends RadioBase {
        String lastFreq = null;
        RadioMode lastMode = null;

        @Override
        public void setFrequency(String frequencyMHz) {
            this.lastFreq = frequencyMHz;
        }

        @Override
        public void setMode(RadioMode mode) {
            this.lastMode = mode;
        }

        public byte[] testToBCD(long val) {
            return toBCD(val);
        }

        public byte[] testToBcdBe(String val) {
            return to_bcd_be(val);
        }
    }

    @Before
    public void setUp() {
        radio = new TestRadio();
    }

    @Test
    public void testSetDxDelegation() {
        radio.setDx("14074000", RadioMode.USB);
        assertEquals("14074000", radio.lastFreq);
        assertEquals(RadioMode.USB, radio.lastMode);
    }

    @Test
    public void testToDecimalAndFmDecimal() {
        long original = 14200000L;
        int len = 8;
        // toDecimal returns reversed padded string
        String decimal = radio.toDecimal(original, len);
        assertNotNull(decimal);
        assertEquals(len, decimal.length());

        // fmDecimal parses normal string representation
        long parsed = radio.fmDecimal("14200000", len);
        assertEquals(14200000L, parsed);
    }

    @Test
    public void testBcdConversion() {
        byte[] bcd = radio.testToBCD(12345678L);
        assertNotNull(bcd);
        assertEquals(4, bcd.length);

        byte[] bcdBe = radio.testToBcdBe("12345678");
        assertNotNull(bcdBe);
        assertEquals(4, bcdBe.length);
        assertArrayEquals(bcd, bcdBe);
    }
}
