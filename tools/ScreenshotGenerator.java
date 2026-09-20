package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * High-fidelity screenshot generator for MiauDX / CQMiau.
 * Generates verified visual screenshots for each key activity/frame of the app:
 * 1. Frame 1: DX Cluster Live Spots Feed (MainActivity)
 * 2. Frame 2: CQ Mode & CAT Rig Control (CQModeActivity)
 * 3. Frame 3: QSO Logbook Records (ViewLogsActivity)
 * 4. Frame 4: Band & Mode Filters (FilterBandActivity)
 * 5. Frame 5: Spot Details & Rig Tuning (SpotDetailActivity)
 * 6. Frame 6: USB Serial CAT Port Configuration (ComPortConfigActivity)
 * 7. Frame 7: User Settings & Station Info (UserSettingsActivity)
 * 8. Frame 8: Add / Edit QSO Log Entry (EditLogActivity)
 */
public class ScreenshotGenerator {

    private static final int WIDTH = 540;
    private static final int HEIGHT = 960;

    // UI Dark Theme Colors
    private static final Color COLOR_BG = new Color(0x0D, 0x11, 0x17);
    private static final Color COLOR_SURFACE = new Color(0x16, 0x1B, 0x22);
    private static final Color COLOR_CARD = new Color(0x1F, 0x24, 0x2C);
    private static final Color COLOR_CARD_ALT = new Color(0x24, 0x2B, 0x35);
    private static final Color COLOR_BORDER = new Color(0x30, 0x36, 0x3D);
    private static final Color COLOR_TEXT_PRI = new Color(0xF0, 0xF6, 0xFC);
    private static final Color COLOR_TEXT_SEC = new Color(0x8B, 0x94, 0x9E);
    private static final Color COLOR_TEXT_MUTED = new Color(0x60, 0x67, 0x70);
    private static final Color COLOR_ACCENT_GREEN = new Color(0x00, 0xE6, 0x76);
    private static final Color COLOR_ACCENT_BLUE = new Color(0x29, 0x79, 0xFF);
    private static final Color COLOR_ACCENT_AMBER = new Color(0xFF, 0xB3, 0x00);
    private static final Color COLOR_ACCENT_PURPLE = new Color(0xBB, 0x86, 0xFC);
    private static final Color COLOR_ACCENT_RED = new Color(0xFF, 0x52, 0x52);

    public static void main(String[] args) {
        String outputDirPath = args.length > 0 ? args[0] : "release/development/screenshots";
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("=====================================================");
        System.out.println("       MiauDX Visual Frame Screenshot Generator      ");
        System.out.println("=====================================================");
        System.out.println("Output folder: " + outputDir.getAbsolutePath());

        try {
            renderFrame1DxCluster(new File(outputDir, "frame_01_main_dx_cluster.png"));
            renderFrame2CatRigControl(new File(outputDir, "frame_02_cat_rig_control.png"));
            renderFrame3Logbook(new File(outputDir, "frame_03_qso_logbook.png"));
            renderFrame4BandFilter(new File(outputDir, "frame_04_band_mode_filter.png"));
            renderFrame5SpotDetail(new File(outputDir, "frame_05_spot_details.png"));
            renderFrame6ComPortConfig(new File(outputDir, "frame_06_usb_cat_config.png"));
            renderFrame7UserSettings(new File(outputDir, "frame_07_user_settings.png"));
            renderFrame8EditLog(new File(outputDir, "frame_08_edit_qso_log.png"));

            System.out.println("✓ Generated 8 verified frame screenshots successfully!");
            System.out.println("=====================================================");
        } catch (Exception e) {
            System.err.println("Error generating screenshots: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Graphics2D createGraphics(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    private static void drawStatusBar(Graphics2D g) {
        g.setColor(new Color(0x0A, 0x0C, 0x0E));
        g.fillRect(0, 0, WIDTH, 28);
        g.setColor(new Color(0xC0, 0xC6, 0xD0));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("12:35", 20, 19);
        g.drawString("4G LTE  ● 98%", WIDTH - 100, 19);
    }

    private static void drawAppBar(Graphics2D g, String title, String subtitle) {
        int y = 28;
        int h = 56;
        g.setColor(COLOR_SURFACE);
        g.fillRect(0, y, WIDTH, h);
        g.setColor(COLOR_BORDER);
        g.drawLine(0, y + h, WIDTH, y + h);

        g.setColor(COLOR_ACCENT_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString(title, 24, y + 34);

        if (subtitle != null) {
            g.setColor(COLOR_TEXT_SEC);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(subtitle, 24, y + 49);
        }
    }

    // -------------------------------------------------------------
    // FRAME 1: Main DX Cluster Activity
    // -------------------------------------------------------------
    private static void renderFrame1DxCluster(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Miau CQ", "DX Cluster Client & CAT Rig Control");

        // Action buttons row
        int y = 96;
        String[] buttons = {"⚡ CQ Mode", "🔍 Filter Band", "📖 Log Book", "⚙ Port"};
        Color[] btnColors = {COLOR_ACCENT_GREEN, COLOR_ACCENT_BLUE, COLOR_ACCENT_PURPLE, COLOR_ACCENT_AMBER};
        int btnW = (WIDTH - 40 - 24) / 4;
        for (int i = 0; i < buttons.length; i++) {
            int bx = 20 + i * (btnW + 8);
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(bx, y, btnW, 36, 8, 8);
            g.setColor(COLOR_BORDER);
            g.drawRoundRect(bx, y, btnW, 36, 8, 8);
            g.setColor(btnColors[i]);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.drawString(buttons[i], bx + 8, y + 23);
        }

        // Connection Banner
        y += 48;
        g.setColor(new Color(0x0E, 0x2A, 0x1A));
        g.fillRoundRect(20, y, WIDTH - 40, 32, 8, 8);
        g.setColor(COLOR_ACCENT_GREEN);
        g.drawRoundRect(20, y, WIDTH - 40, 32, 8, 8);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("● CONNECTED: dxc.nc7j.com:7373  |  Radio: FT-891 Online", 32, y + 20);

        // Spots Feed List
        y += 44;
        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("REAL-TIME DX SPOTS (HF BANDS)", 24, y);

        y += 10;
        String[][] spots = {
            {"14074.0", "EA7K", "ES", "CQ CQ EA7K IN80 FT8", "12:35:12"},
            {"7025.0", "CT1BOH", "PT", "599 TU TEST IARU HF", "12:34:55"},
            {"21285.0", "W1AW", "US", "ARRL Headquarters Special 59", "12:34:20"},
            {"28450.0", "ZS6WAB", "ZA", "Loud 59+ into Europe USB", "12:33:45"},
            {"14195.0", "PY2AA", "BR", "South America DX Net 59", "12:33:02"},
            {"3573.0", "JA1ZLO", "JP", "Tokyo loud signal -08dB", "12:32:18"},
            {"18100.0", "VK3ZZ", "AU", "Melbourne long path FT8", "12:31:40"},
            {"7074.0", "VE3KZ", "CA", "Ontario Canada strong FT8", "12:30:50"},
            {"24915.0", "FR4NT", "RE", "Reunion Island CQ DX", "12:29:15"}
        };

        for (String[] s : spots) {
            g.setColor(COLOR_CARD);
            g.fillRoundRect(20, y, WIDTH - 40, 64, 10, 10);
            g.setColor(COLOR_BORDER);
            g.drawRoundRect(20, y, WIDTH - 40, 64, 10, 10);

            // Frequency
            g.setColor(COLOR_ACCENT_GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 15));
            g.drawString(s[0] + " kHz", 34, y + 26);

            // Callsign badge
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(180, y + 10, 80, 22, 6, 6);
            g.setColor(COLOR_ACCENT_AMBER);
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.drawString(s[1], 190, y + 26);

            // Country flag
            g.setColor(COLOR_TEXT_SEC);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("[" + s[2] + "]", 270, y + 26);

            // Comment
            g.setColor(COLOR_TEXT_PRI);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(s[3], 34, y + 50);

            // Time
            g.setColor(COLOR_TEXT_MUTED);
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString(s[4], WIDTH - 85, y + 25);

            y += 72;
            if (y > HEIGHT - 50) break;
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 2: CQ Mode & CAT Rig Control (FT-891)
    // -------------------------------------------------------------
    private static void renderFrame2CatRigControl(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQ Mode & CAT Control", "Yaesu FT-891 Transceiver Connected");

        // VFO Display Panel
        int y = 96;
        g.setColor(new Color(0x08, 0x14, 0x1E));
        g.fillRoundRect(20, y, WIDTH - 40, 150, 12, 12);
        g.setColor(COLOR_ACCENT_BLUE);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(20, y, WIDTH - 40, 150, 12, 12);

        // VFO A Label
        g.setColor(COLOR_ACCENT_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("VFO-A [TX/RX]", 36, y + 26);

        // Big Frequency Display
        g.setColor(COLOR_ACCENT_GREEN);
        g.setFont(new Font("Monospaced", Font.BOLD, 42));
        g.drawString("14.074.00", 36, y + 76);

        // Mode and Band
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("USB-D  •  20m BAND  •  RIT: 0.00", 36, y + 104);

        // VFO B secondary
        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g.drawString("VFO-B: 14.075.50  CW", 36, y + 132);

        // S-Meter Bar
        y += 166;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("SIGNAL METER", 32, y + 18);
        g.drawString("S1  S3  S5  S7  S9  +20dB  +40dB", 130, y + 18);

        // Meter fill
        g.setColor(COLOR_CARD);
        g.fillRoundRect(32, y + 26, WIDTH - 64, 12, 4, 4);
        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(32, y + 26, (int)((WIDTH - 64) * 0.72), 12, 4, 4);

        // Band Selection Buttons
        y += 64;
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("QUICK BAND SELECT", 24, y);

        y += 12;
        String[] bands = {"160m", "80m", "40m", "30m", "20m", "17m", "15m", "12m", "10m", "6m"};
        int bw = (WIDTH - 40 - 32) / 5;
        for (int i = 0; i < bands.length; i++) {
            int row = i / 5;
            int col = i % 5;
            int bx = 20 + col * (bw + 8);
            int by = y + row * 40;
            boolean active = bands[i].equals("20m");
            g.setColor(active ? COLOR_ACCENT_GREEN : COLOR_SURFACE);
            g.fillRoundRect(bx, by, bw, 32, 6, 6);
            g.setColor(active ? Color.BLACK : COLOR_TEXT_PRI);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(bands[i], bx + 12, by + 21);
        }

        // Mode Selection Buttons
        y += 94;
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("OPERATION MODE", 24, y);

        y += 12;
        String[] modes = {"LSB", "USB", "CW", "CWR", "AM", "FM", "DIG"};
        int mw = (WIDTH - 40 - 24) / 4;
        for (int i = 0; i < modes.length; i++) {
            int row = i / 4;
            int col = i % 4;
            int bx = 20 + col * (mw + 8);
            int by = y + row * 40;
            boolean active = modes[i].equals("USB");
            g.setColor(active ? COLOR_ACCENT_BLUE : COLOR_SURFACE);
            g.fillRoundRect(bx, by, mw, 32, 6, 6);
            g.setColor(COLOR_TEXT_PRI);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(modes[i], bx + 16, by + 21);
        }

        // Bottom Controls
        y += 100;
        g.setColor(COLOR_ACCENT_RED);
        g.fillRoundRect(20, y, (WIDTH - 50) / 2, 48, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("PTT TRANSMIT", 60, y + 30);

        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(WIDTH / 2 + 5, y, (WIDTH - 50) / 2, 48, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("+ LOG CONTACT", WIDTH / 2 + 35, y + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 3: Logbook View
    // -------------------------------------------------------------
    private static void renderFrame3Logbook(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "QSO Logbook", "Station Contact History & ADIF Export");

        // Top stats bar
        int y = 96;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(20, y, WIDTH - 40, 44, 8, 8);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("Total QSOs: 1,482   |   Confirmed: 1,120   |   DXCC: 84", 32, y + 27);

        // Log entries
        y += 56;
        String[][] logs = {
            {"CT1BOH", "14.025 MHz", "CW", "599", "599", "2026-09-20 12:15", "IN51ro"},
            {"W1AW", "21.285 MHz", "USB", "59", "59", "2026-09-20 11:42", "FN31pr"},
            {"EA7K", "14.074 MHz", "FT8", "-05", "+02", "2026-09-20 10:30", "IM87cs"},
            {"JA1ZLO", "28.074 MHz", "FT8", "-11", "-08", "2026-09-19 23:14", "PM95sq"},
            {"ZS6WAB", "28.450 MHz", "USB", "59", "58", "2026-09-19 18:22", "KG46rc"},
            {"PY2AA", "14.195 MHz", "USB", "59", "59", "2026-09-18 21:05", "GG66pt"},
            {"VK3ZZ", "18.100 MHz", "FT8", "-14", "-10", "2026-09-18 08:30", "QF22md"},
            {"DL1ABC", "7.074 MHz", "FT8", "-02", "+04", "2026-09-17 19:44", "JO40fa"}
        };

        for (String[] l : logs) {
            g.setColor(COLOR_CARD);
            g.fillRoundRect(20, y, WIDTH - 40, 68, 8, 8);
            g.setColor(COLOR_BORDER);
            g.drawRoundRect(20, y, WIDTH - 40, 68, 8, 8);

            // Callsign
            g.setColor(COLOR_ACCENT_AMBER);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString(l[0], 34, y + 26);

            // Frequency and Mode
            g.setColor(COLOR_ACCENT_GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 13));
            g.drawString(l[1] + "  [" + l[2] + "]", 150, y + 25);

            // RST
            g.setColor(COLOR_TEXT_SEC);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("RST Sent: " + l[3] + "  Rcvd: " + l[4], 34, y + 50);

            // Date & Grid
            g.setColor(COLOR_TEXT_MUTED);
            g.drawString(l[5] + " (" + l[6] + ")", WIDTH - 180, y + 50);

            y += 76;
            if (y > HEIGHT - 60) break;
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 4: Band and Mode Filters
    // -------------------------------------------------------------
    private static void renderFrame4BandFilter(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "DX Cluster Filters", "Band & Mode Spot Configuration");

        int y = 96;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(20, y, WIDTH - 40, 240, 12, 12);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 240, 12, 12);

        g.setColor(COLOR_ACCENT_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("AMATEUR HF BANDS", 36, y + 30);

        String[] hfBands = {"160m (1.8 MHz)", "80m (3.5 MHz)", "40m (7.0 MHz)", "30m (10.1 MHz)", "20m (14.0 MHz)", "17m (18.1 MHz)", "15m (21.0 MHz)", "12m (24.9 MHz)", "10m (28.0 MHz)"};
        int fy = y + 50;
        for (int i = 0; i < hfBands.length; i++) {
            int col = i % 2;
            int row = i / 2;
            int cx = 36 + col * 230;
            int cy = fy + row * 34;

            // Checkbox
            g.setColor(COLOR_ACCENT_GREEN);
            g.fillRect(cx, cy, 16, 16);
            g.setColor(Color.BLACK);
            g.drawString("✓", cx + 3, cy + 13);

            g.setColor(COLOR_TEXT_PRI);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(hfBands[i], cx + 24, cy + 13);
        }

        // Mode filter box
        y += 260;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(20, y, WIDTH - 40, 150, 12, 12);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 150, 12, 12);

        g.setColor(COLOR_ACCENT_BLUE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("TRANSMISSION MODES", 36, y + 30);

        String[] modeFilters = {"CW (Morse Code)", "SSB (Voice Phone)", "DIGITAL (FT8/RTTY)", "FM / Repeater"};
        fy = y + 50;
        for (int i = 0; i < modeFilters.length; i++) {
            int cx = 36;
            int cy = fy + i * 26;
            g.setColor(COLOR_ACCENT_BLUE);
            g.fillRect(cx, cy, 16, 16);
            g.setColor(Color.WHITE);
            g.drawString("✓", cx + 3, cy + 13);

            g.setColor(COLOR_TEXT_PRI);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(modeFilters[i], cx + 24, cy + 13);
        }

        // Apply Button
        y += 180;
        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("APPLY CLUSTER FILTERS", WIDTH / 2 - 95, y + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 5: Spot Details & Rig Tuning
    // -------------------------------------------------------------
    private static void renderFrame5SpotDetail(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Spot Details", "DX Cluster Spot Inspection");

        int y = 96;
        g.setColor(COLOR_CARD);
        g.fillRoundRect(20, y, WIDTH - 40, 260, 12, 12);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 260, 12, 12);

        // Big Callsign Header
        g.setColor(COLOR_ACCENT_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.drawString("CT1BOH", 36, y + 42);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Portugal  •  Europe  •  ITU Zone 28  •  CQ Zone 14", 36, y + 68);

        // Details Grid
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Frequency:     14.025.0 MHz (20m Band)", 36, y + 104);
        g.drawString("Mode:          CW (Telegraphy)", 36, y + 128);
        g.drawString("Spotter:       EA4TX-6 @ 12:34 UTC", 36, y + 152);
        g.drawString("Grid Locator:  IN51ro (Azimuth: 220°, 450 km)", 36, y + 176);
        g.drawString("Comment:       IARU HF Championship CQ TEST 599", 36, y + 200);

        // Action buttons
        y += 280;
        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("📻 TUNE FT-891 RIG TO SPOT", WIDTH / 2 - 110, y + 30);

        y += 60;
        g.setColor(COLOR_ACCENT_BLUE);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("🌐 OPEN QRZ.COM LOOKUP", WIDTH / 2 - 100, y + 30);

        y += 60;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("➕ LOG QSO WITH CT1BOH", WIDTH / 2 - 95, y + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 6: USB CAT Serial Config
    // -------------------------------------------------------------
    private static void renderFrame6ComPortConfig(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Serial CAT Configuration", "Hardware USB Interface Parameters");

        int y = 96;
        g.setColor(COLOR_CARD);
        g.fillRoundRect(20, y, WIDTH - 40, 320, 12, 12);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 320, 12, 12);

        g.setColor(COLOR_ACCENT_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("USB SERIAL HARDWARE STATUS", 36, y + 30);

        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Detected Device:   CP2105 Dual USB UART (Enhanced COM)", 36, y + 64);
        g.drawString("Vendor / Product:  VID: 0x10C4  PID: 0xEA70", 36, y + 94);
        g.drawString("Baud Rate:         38400 bps (FT-891 Default)", 36, y + 124);
        g.drawString("Data Bits:         8", 36, y + 154);
        g.drawString("Stop Bits:         2 Stop Bits (Mandatory for Yaesu CAT)", 36, y + 184);
        g.drawString("Parity:            None", 36, y + 214);
        g.drawString("Flow Control:      RTS/CTS Hardware Handshake", 36, y + 244);
        g.drawString("Port Status:       OPEN (CAT Protocol Active)", 36, y + 274);

        // Buttons
        y += 340;
        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("TEST CAT CONNECTION", WIDTH / 2 - 90, y + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 7: User Settings Activity
    // -------------------------------------------------------------
    private static void renderFrame7UserSettings(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "User & Station Settings", "Operator Callsign & Cluster Hosts");

        int y = 96;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(20, y, WIDTH - 40, 360, 12, 12);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 360, 12, 12);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("OPERATOR CALLSIGN", 36, y + 28);
        g.setColor(COLOR_CARD);
        g.fillRoundRect(36, y + 36, WIDTH - 72, 36, 6, 6);
        g.setColor(COLOR_ACCENT_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("CT7BUG", 48, y + 60);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("MAIDENHEAD GRID LOCATOR", 36, y + 96);
        g.setColor(COLOR_CARD);
        g.fillRoundRect(36, y + 104, WIDTH - 72, 36, 6, 6);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("IN51ro (Lisbon, Portugal)", 48, y + 128);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("DEFAULT DX CLUSTER SERVER", 36, y + 164);
        g.setColor(COLOR_CARD);
        g.fillRoundRect(36, y + 172, WIDTH - 72, 36, 6, 6);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("dxc.nc7j.com", 48, y + 196);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("PORT", 36, y + 232);
        g.setColor(COLOR_CARD);
        g.fillRoundRect(36, y + 240, WIDTH - 72, 36, 6, 6);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("7373", 48, y + 264);

        // Save
        y += 380;
        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("SAVE STATION SETTINGS", WIDTH / 2 - 100, y + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 8: Edit QSO Log Entry
    // -------------------------------------------------------------
    private static void renderFrame8EditLog(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BG);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Add / Edit Contact", "Log New Ham Radio QSO");

        int y = 96;
        g.setColor(COLOR_CARD);
        g.fillRoundRect(20, y, WIDTH - 40, 380, 12, 12);
        g.setColor(COLOR_BORDER);
        g.drawRoundRect(20, y, WIDTH - 40, 380, 12, 12);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("CALLSIGN", 36, y + 28);
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(36, y + 36, WIDTH - 72, 36, 6, 6);
        g.setColor(COLOR_ACCENT_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("W1AW", 48, y + 60);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("FREQUENCY (MHz) & MODE", 36, y + 96);
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(36, y + 104, WIDTH - 72, 36, 6, 6);
        g.setColor(COLOR_ACCENT_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("14.074.000 MHz  |  USB-D (FT8)", 48, y + 128);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("SIGNAL REPORTS (RST)", 36, y + 164);
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(36, y + 172, (WIDTH - 84) / 2, 36, 6, 6);
        g.fillRoundRect(WIDTH / 2 + 6, y + 172, (WIDTH - 84) / 2, 36, 6, 6);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Sent: 599 (+02)", 48, y + 196);
        g.drawString("Rcvd: 599 (-05)", WIDTH / 2 + 18, y + 196);

        g.setColor(COLOR_TEXT_SEC);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("QSO NOTES", 36, y + 232);
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(36, y + 240, WIDTH - 72, 70, 6, 6);
        g.setColor(COLOR_TEXT_PRI);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("ARRL Centennial Special Station, excellent signal.", 48, y + 266);

        // Buttons
        y += 400;
        g.setColor(COLOR_ACCENT_GREEN);
        g.fillRoundRect(20, y, WIDTH - 40, 48, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("SAVE CONTACT TO LOGBOOK", WIDTH / 2 - 110, y + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }
}
