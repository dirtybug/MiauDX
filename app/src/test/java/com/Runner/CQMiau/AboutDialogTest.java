package com.Runner.CQMiau;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AboutDialogTest {

    @Test
    public void testRadioEscolaConstants() {
        assertEquals("https://radioescola.pt", AboutDialogFragment.RADIO_ESCOLA_URL);
        assertEquals("https://www.paypal.me/radioescola", AboutDialogFragment.RADIO_ESCOLA_PAYPAL_URL);
        assertEquals("https://radioescola.pt/donativos", AboutDialogFragment.RADIO_ESCOLA_DONATIVOS_URL);
        assertEquals("https://github.com/dirtybug/MiauDX/", AboutDialogFragment.REPO_URL);
    }

    @Test
    public void testRadioEscolaUrlsAreHttps() {
        assertTrue(AboutDialogFragment.RADIO_ESCOLA_URL.startsWith("https://"));
        assertTrue(AboutDialogFragment.RADIO_ESCOLA_PAYPAL_URL.startsWith("https://"));
        assertTrue(AboutDialogFragment.RADIO_ESCOLA_DONATIVOS_URL.startsWith("https://"));
    }

    @Test
    public void testAboutDialogFragmentInstantiation() {
        AboutDialogFragment fragment = new AboutDialogFragment();
        assertNotNull(fragment);
    }
}
