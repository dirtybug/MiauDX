package com.Runner.CQMiau;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.Runner.CQMiau.logbook.LogBook;

import org.junit.Test;

public class LogBookTest {

    @Test
    public void testLogBookCreationAndGetters() {
        long timestamp = 1726833600000L; // Fixed epoch time
        LogBook entry = new LogBook(1, "14074000", "CT1BOH", "IN51ro", timestamp, 59, 59);

        assertEquals(1, entry.getId());
        assertEquals("14074000", entry.getFrequency());
        assertEquals("CT1BOH", entry.getCallSign());
        assertEquals("IN51ro", entry.getLocation());
        assertEquals(timestamp, entry.getTime());
        assertEquals(59, entry.getReceiveSValue());
        assertEquals(59, entry.getSendSValue());
    }

    @Test
    public void testLogBookTimeStrFormat() {
        long timestamp = 1726833600000L;
        LogBook entry = new LogBook(42, "7025000", "W1AW", "FN31pr", timestamp, 9, 9);
        String timeStr = entry.getTimeStr();

        assertNotNull(timeStr);
        // Verify format yyyy-MM-dd HH:mm:ss
        assertTrue(timeStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
}
