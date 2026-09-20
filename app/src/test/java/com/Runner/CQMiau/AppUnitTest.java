package com.Runner.CQMiau;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.Runner.CQMiau.radios.RadioType;
import com.Runner.CQMiau.spot.Spot;

import org.junit.Test;

public class AppUnitTest {

    @Test
    public void testRadioTypeEnum() {
        assertNotNull(RadioType.FT891);
        assertNotNull(RadioType.NORADIO);
        assertTrue(RadioType.values().length > 10);
        assertEquals(RadioType.FT891, RadioType.valueOf("FT891"));
    }

    @Test
    public void testSpotCreationAndGetters() {
        long now = 1700000000000L;
        Spot spot = new Spot("14074.0", "US", "W1AW", "CT", now, "FT8 strong signal");
        assertEquals("14074.0", spot.getFrequency());
        assertEquals("US", spot.getFlag());
        assertEquals("W1AW", spot.getCallSign());
        assertEquals("CT", spot.getLocation());
        assertEquals(now, spot.getTimestamp());
        assertEquals("FT8 strong signal", spot.getComment());
    }
}
