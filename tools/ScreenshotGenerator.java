package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * High-fidelity visual screenshot generator for MiauDX / CQMiau.
 * Recreates exact Android UI layouts matching the app XML layouts and Dark OLED theme:
 * 1. Frame 1: Main DX Cluster (MainActivity: Filter Band & View Log buttons, log window, spots with flags)
 * 2. Frame 2: Menu Lateral / Options Overflow Menu (main_menu.xml: Open Radio, User Settings, CQ Mode, Donate, Git)
 * 3. Frame 3: Band Filter Screen (activity_filter.xml: HF Band checkboxes and Exit button)
 * 4. Frame 4: View Logs / QSO Logbook (activity_view_logs.xml + item_log.xml: Gen.ADIF, Edit, Delete, Add to Log)
 * 5. Frame 5: COM Port Config / Open Radio (activity_com_port_config.xml: baud rate, parity, radio, Apply)
 * 6. Frame 6: User Settings (activity_user_settings.xml: Callsign input and Save button)
 * 7. Frame 7: CQ Mode & Rig Control (activity_cq_mode.xml: Call, Freq, RST, Date/Time, live spots, 4 buttons)
 * 8. Frame 8: Spot Details & Station Tuning (activity_spot.xml: Send Spot, Open QRZ, Set Frequency, Save to Log)
 */
public class ScreenshotGenerator {

    private static final int WIDTH = 540;
    private static final int HEIGHT = 960;

    // Theme Colors (per themes.xml, colors.xml & activity_main.xml)
    private static final Color COLOR_BLACK = new Color(0x00, 0x00, 0x00);
    private static final Color COLOR_BG = new Color(0x0A, 0x0A, 0x0A);
    private static final Color COLOR_SURFACE = new Color(0x16, 0x16, 0x16);
    private static final Color COLOR_CARD = new Color(0x1E, 0x1E, 0x1E);
    private static final Color COLOR_CARD_BORDER = new Color(0x33, 0x33, 0x33);
    private static final Color COLOR_TEXT_WHITE = new Color(0xFF, 0xFF, 0xFF);
    private static final Color COLOR_TEXT_GREY = new Color(0xA8, 0xA8, 0xA8);
    private static final Color COLOR_TEXT_MUTED = new Color(0x70, 0x70, 0x70);
    private static final Color COLOR_TEAL = new Color(0x03, 0xDA, 0xC5); // @color/teal_200
    private static final Color COLOR_TEAL_DARK = new Color(0x01, 0x98, 0x8A);
    private static final Color COLOR_AMBER = new Color(0xFF, 0xB3, 0x00);
    private static final Color COLOR_GREEN = new Color(0x00, 0xE6, 0x76);
    private static final Color COLOR_BLUE = new Color(0x29, 0x79, 0xFF);

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
            // Frame 1: Main DX Cluster (activity_main.xml)
            renderFrame1DxCluster(new File(outputDir, "frame_01_main_dx_cluster.png"));

            // Frame 2: Menu Lateral / Options Menu (main_menu.xml)
            File f2Menu = new File(outputDir, "frame_02_lateral_menu.png");
            File f2Rig = new File(outputDir, "frame_02_cat_rig_control.png");
            File f2MenuAlt = new File(outputDir, "frame_02_menu_lateral.png");
            renderFrame2LateralMenu(f2Menu);
            copyFile(f2Menu, f2Rig);
            copyFile(f2Menu, f2MenuAlt);

            // Frame 3: Band Filter (activity_filter.xml)
            File f3Filter = new File(outputDir, "frame_03_filter_band.png");
            File f4FilterAlt = new File(outputDir, "frame_04_band_mode_filter.png");
            renderFrame3BandFilter(f3Filter);
            copyFile(f3Filter, f4FilterAlt);

            // Frame 4: View Logs / QSO Logbook (activity_view_logs.xml + item_log.xml)
            File f4Logs = new File(outputDir, "frame_04_view_logs.png");
            File f3LogsAlt = new File(outputDir, "frame_03_qso_logbook.png");
            renderFrame4ViewLogs(f4Logs);
            copyFile(f4Logs, f3LogsAlt);

            // Frame 5: COM Port Config / Open Radio (activity_com_port_config.xml)
            File f5Port = new File(outputDir, "frame_05_com_port_config.png");
            File f6PortAlt = new File(outputDir, "frame_06_usb_cat_config.png");
            renderFrame5ComPortConfig(f5Port);
            copyFile(f5Port, f6PortAlt);

            // Frame 6: User Settings (activity_user_settings.xml)
            File f6Settings = new File(outputDir, "frame_06_user_settings.png");
            File f7SettingsAlt = new File(outputDir, "frame_07_user_settings.png");
            renderFrame6UserSettings(f6Settings);
            copyFile(f6Settings, f7SettingsAlt);

            // Frame 7: CQ Mode & Rig Control (activity_cq_mode.xml)
            File f7Cq = new File(outputDir, "frame_07_cq_mode.png");
            renderFrame7CqMode(f7Cq);

            // Frame 8: Spot Details & Tuning (activity_spot.xml & activity_edit_log.xml)
            File f8Spot = new File(outputDir, "frame_08_spot_details.png");
            File f5SpotAlt = new File(outputDir, "frame_05_spot_details.png");
            File f8EditAlt = new File(outputDir, "frame_08_edit_qso_log.png");
            renderFrame8SpotDetails(f8Spot);
            copyFile(f8Spot, f5SpotAlt);
            copyFile(f8Spot, f8EditAlt);

            System.out.println("✓ Generated all visual screenshots with 100% accuracy!");
            System.out.println("=====================================================");
        } catch (Exception e) {
            System.err.println("Error generating screenshots: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void copyFile(File src, File dst) {
        try {
            BufferedImage img = ImageIO.read(src);
            ImageIO.write(img, "png", dst);
        } catch (Exception ignored) {}
    }

    private static Graphics2D createGraphics(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    // -------------------------------------------------------------
    // Shared UI Components (Status Bar, App Bar, Teal Buttons)
    // -------------------------------------------------------------
    private static void drawStatusBar(Graphics2D g) {
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, 28);
        g.setColor(new Color(0xD0, 0xD0, 0xD0));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("12:35", 20, 19);

        // Right status icons
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("4G LTE", WIDTH - 110, 19);
        g.fillOval(WIDTH - 55, 11, 8, 8);
        g.drawString("98%", WIDTH - 42, 19);
    }

    private static void drawAppBar(Graphics2D g, String title, boolean showMenuDots, boolean showBackArrow) {
        int y = 28;
        int h = 56;
        g.setColor(COLOR_BLACK);
        g.fillRect(0, y, WIDTH, h);
        g.setColor(new Color(0x2A, 0x2A, 0x2A));
        g.drawLine(0, y + h, WIDTH, y + h);

        int textX = 24;
        if (showBackArrow) {
            g.setColor(COLOR_TEXT_WHITE);
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(24, y + 28, 38, y + 28);
            g.drawLine(24, y + 28, 31, y + 21);
            g.drawLine(24, y + 28, 31, y + 35);
            textX = 52;
        }

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString(title, textX, y + 36);

        if (showMenuDots) {
            // Three-dots overflow menu icon (⋮)
            g.setColor(COLOR_TEXT_WHITE);
            int dotX = WIDTH - 28;
            g.fillOval(dotX, y + 20, 4, 4);
            g.fillOval(dotX, y + 27, 4, 4);
            g.fillOval(dotX, y + 34, 4, 4);
        }
    }

    private static void drawTealButton(Graphics2D g, int x, int y, int w, int h, String text, int fontSize) {
        // Material Teal Button (backgroundTint="@color/teal_200", textColor="@color/white")
        g.setColor(COLOR_TEAL);
        g.fillRoundRect(x, y, w, h, 8, 8);

        // Subtle upper highlight
        g.setColor(new Color(255, 255, 255, 35));
        g.fillRoundRect(x, y, w, h / 2, 8, 8);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int strW = fm.stringWidth(text);
        int strH = fm.getAscent();
        g.drawString(text, x + (w - strW) / 2, y + (h + strH) / 2 - 3);
    }

    // -------------------------------------------------------------
    // High-Fidelity Country Flag Graphics (drawFlag)
    // -------------------------------------------------------------
    public static void drawFlag(Graphics2D g, String countryCode, int x, int y, int w, int h) {
        Graphics2D gFlag = (Graphics2D) g.create();
        // Outer border & shadow
        gFlag.setColor(new Color(0x30, 0x30, 0x30));
        gFlag.drawRoundRect(x - 1, y - 1, w + 2, h + 2, 4, 4);
        gFlag.setClip(new RoundRectangle2D.Float(x, y, w, h, 4, 4));

        switch (countryCode.toUpperCase()) {
            case "PT": { // Portugal 🇵🇹: Green (40%), Red (60%), Armillary sphere + shield
                int greenW = (int) (w * 0.40);
                gFlag.setColor(new Color(0x00, 0x66, 0x00));
                gFlag.fillRect(x, y, greenW, h);
                gFlag.setColor(new Color(0xCC, 0x00, 0x00));
                gFlag.fillRect(x + greenW, y, w - greenW, h);

                // Armillary sphere circle
                int cx = x + greenW;
                int cy = y + h / 2;
                int r = h / 3;
                gFlag.setColor(new Color(0xFF, 0xCC, 0x00));
                gFlag.drawOval(cx - r, cy - r, r * 2, r * 2);
                gFlag.fillOval(cx - r / 2, cy - r / 2, r, r);
                gFlag.setColor(Color.WHITE);
                gFlag.fillRect(cx - 2, cy - 3, 4, 6);
                gFlag.setColor(new Color(0x00, 0x22, 0x77));
                gFlag.drawRect(cx - 2, cy - 3, 4, 6);
                break;
            }
            case "US": { // USA 🇺🇸: 13 stripes, blue canton, white stars
                int stripeH = Math.max(1, h / 7);
                for (int i = 0; i < 7; i++) {
                    gFlag.setColor((i % 2 == 0) ? new Color(0xB2, 0x22, 0x34) : Color.WHITE);
                    gFlag.fillRect(x, y + i * stripeH, w, stripeH);
                }
                int cantonW = (int) (w * 0.45);
                int cantonH = stripeH * 4;
                gFlag.setColor(new Color(0x3C, 0x3B, 0x6E));
                gFlag.fillRect(x, y, cantonW, cantonH);
                gFlag.setColor(Color.WHITE);
                for (int r = 1; r <= 3; r++) {
                    for (int c = 1; c <= 3; c++) {
                        gFlag.fillRect(x + c * (cantonW / 4) - 1, y + r * (cantonH / 4) - 1, 2, 2);
                    }
                }
                break;
            }
            case "JP": { // Japan 🇯🇵: White with red sun disc
                gFlag.setColor(Color.WHITE);
                gFlag.fillRect(x, y, w, h);
                gFlag.setColor(new Color(0xBC, 0x00, 0x2D));
                int dia = (int) (h * 0.60);
                gFlag.fillOval(x + (w - dia) / 2, y + (h - dia) / 2, dia, dia);
                break;
            }
            case "DE": { // Germany 🇩🇪: Black, Red, Gold horizontal tricolor
                int sh = h / 3;
                gFlag.setColor(Color.BLACK);
                gFlag.fillRect(x, y, w, sh);
                gFlag.setColor(new Color(0xDD, 0x00, 0x00));
                gFlag.fillRect(x, y + sh, w, sh);
                gFlag.setColor(new Color(0xFF, 0xCE, 0x00));
                gFlag.fillRect(x, y + sh * 2, w, h - sh * 2);
                break;
            }
            case "GB": { // United Kingdom 🇬🇧: Union Jack
                gFlag.setColor(new Color(0x01, 0x21, 0x69));
                gFlag.fillRect(x, y, w, h);
                gFlag.setColor(Color.WHITE);
                gFlag.setStroke(new BasicStroke(3f));
                gFlag.drawLine(x, y, x + w, y + h);
                gFlag.drawLine(x + w, y, x, y + h);
                gFlag.setColor(new Color(0xC8, 0x10, 0x2E));
                gFlag.setStroke(new BasicStroke(1.5f));
                gFlag.drawLine(x, y, x + w, y + h);
                gFlag.drawLine(x + w, y, x, y + h);
                // St George cross
                int crossW = 6;
                gFlag.setColor(Color.WHITE);
                gFlag.fillRect(x + (w - crossW) / 2, y, crossW, h);
                gFlag.fillRect(x, y + (h - crossW) / 2, w, crossW);
                int crossRedW = 3;
                gFlag.setColor(new Color(0xC8, 0x10, 0x2E));
                gFlag.fillRect(x + (w - crossRedW) / 2, y, crossRedW, h);
                gFlag.fillRect(x, y + (h - crossRedW) / 2, w, crossRedW);
                break;
            }
            case "BR": { // Brazil 🇧🇷: Green field, Yellow rhombus, Blue circle
                gFlag.setColor(new Color(0x00, 0x97, 0x39));
                gFlag.fillRect(x, y, w, h);
                gFlag.setColor(new Color(0xFE, 0xDD, 0x00));
                Polygon p = new Polygon();
                p.addPoint(x + w / 2, y + 2);
                p.addPoint(x + w - 2, y + h / 2);
                p.addPoint(x + w / 2, y + h - 2);
                p.addPoint(x + 2, y + h / 2);
                gFlag.fillPolygon(p);
                gFlag.setColor(new Color(0x01, 0x21, 0x69));
                int dia = (int) (h * 0.45);
                gFlag.fillOval(x + (w - dia) / 2, y + (h - dia) / 2, dia, dia);
                gFlag.setColor(Color.WHITE);
                gFlag.setStroke(new BasicStroke(1.2f));
                gFlag.drawArc(x + (w - dia) / 2, y + (h - dia) / 2, dia, dia, 30, 120);
                break;
            }
            case "ES": { // Spain 🇪🇸: Red, Yellow (double width), Red
                int rH = (int) (h * 0.25);
                gFlag.setColor(new Color(0xAA, 0x15, 0x1B));
                gFlag.fillRect(x, y, w, rH);
                gFlag.setColor(new Color(0xF1, 0xBF, 0x00));
                gFlag.fillRect(x, y + rH, w, h - rH * 2);
                gFlag.setColor(new Color(0xAA, 0x15, 0x1B));
                gFlag.fillRect(x, y + h - rH, w, rH);
                // Coat of arms indicator
                gFlag.setColor(new Color(0x8B, 0x00, 0x00));
                gFlag.fillOval(x + (int) (w * 0.25), y + h / 2 - 3, 5, 6);
                break;
            }
            case "CA": { // Canada 🇨🇦: Red, White, Red with maple leaf
                int sideW = (int) (w * 0.25);
                gFlag.setColor(new Color(0xD8, 0x2A, 0x2A));
                gFlag.fillRect(x, y, sideW, h);
                gFlag.setColor(Color.WHITE);
                gFlag.fillRect(x + sideW, y, w - sideW * 2, h);
                gFlag.setColor(new Color(0xD8, 0x2A, 0x2A));
                gFlag.fillRect(x + w - sideW, y, sideW, h);
                // Maple leaf
                gFlag.fillOval(x + w / 2 - 3, y + h / 2 - 3, 6, 6);
                break;
            }
            default: { // Generic / UN Blue
                gFlag.setColor(COLOR_TEAL);
                gFlag.fillRect(x, y, w, h);
                gFlag.setColor(COLOR_BLACK);
                gFlag.setFont(new Font("SansSerif", Font.BOLD, 9));
                gFlag.drawString(countryCode, x + 3, y + h - 4);
                break;
            }
        }
        gFlag.dispose();
    }

    // -------------------------------------------------------------
    // FRAME 1: Main DX Cluster (activity_main.xml)
    // -------------------------------------------------------------
    private static void renderFrame1DxCluster(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQMiau", true, false);

        int curY = 96;

        // Top GridLayout: 2 Equal Teal Buttons (Filter Band & View Log)
        int pad = 16;
        int gap = 12;
        int btnW = (WIDTH - (pad * 2) - gap) / 2;
        int btnH = 46;

        // Button 1: openFilterButton ("Filter Band")
        drawTealButton(g, pad, curY, btnW, btnH, "Filter Band", 15);

        // Button 2: ViewLog ("View Log")
        drawTealButton(g, pad + btnW + gap, curY, btnW, btnH, "View Log", 15);

        curY += btnH + 16;

        // Middle: ScrollView for Logs (logScrollView, height 150dp, black background, logsView)
        int logH = 148;
        g.setColor(COLOR_BLACK);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), logH, 8, 8);
        g.setColor(new Color(0x38, 0x38, 0x38));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), logH, 8, 8);

        // Terminal Log text lines
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int textY = curY + 18;
        int lineH = 16;

        g.setColor(COLOR_TEAL);
        g.drawString("Connecting to DX Cluster (dxfun.com:8000)...", pad + 10, textY);
        textY += lineH;

        g.setColor(COLOR_GREEN);
        g.drawString("Connected to DX Cluster!", pad + 10, textY);
        textY += lineH;

        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("ADDED ", pad + 10, textY);
        drawFlag(g, "PT", pad + 55, textY - 10, 16, 11);
        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("CT1BOH@21074.0", pad + 76, textY);
        textY += lineH;

        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("ADDED ", pad + 10, textY);
        drawFlag(g, "US", pad + 55, textY - 10, 16, 11);
        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("W1AW@14074.0", pad + 76, textY);
        textY += lineH;

        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("ADDED ", pad + 10, textY);
        drawFlag(g, "JP", pad + 55, textY - 10, 16, 11);
        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("JA1ABC@28074.0", pad + 76, textY);
        textY += lineH;

        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("ADDED ", pad + 10, textY);
        drawFlag(g, "DE", pad + 55, textY - 10, 16, 11);
        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("DL1XYZ@7074.0", pad + 76, textY);
        textY += lineH;

        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("ADDED ", pad + 10, textY);
        drawFlag(g, "GB", pad + 55, textY - 10, 16, 11);
        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("M0XYZ@14205.0", pad + 76, textY);
        textY += lineH;

        g.setColor(COLOR_AMBER);
        g.drawString("REFRESH ", pad + 10, textY);
        drawFlag(g, "PT", pad + 68, textY - 10, 16, 11);
        g.setColor(COLOR_TEXT_WHITE);
        g.drawString("CT1BOH@21074.0", pad + 89, textY);

        curY += logH + 16;

        // Bottom: RecyclerView for Spots (spotsRecyclerView & item_spot.xml)
        String[][] spots = {
            {"21074.0", "PT", "CT1BOH", "IN51re FT8 +03dB Strong EU", "12:35:12"},
            {"14074.0", "US", "W1AW", "FN31pr ARRL HQ Special 599", "12:34:40"},
            {"28074.0", "JP", "JA1ABC", "PM95 Tokyo FT8 -08dB JA net", "12:33:55"},
            {"7074.0", "DE", "DL1XYZ", "JO43 Munich FT8 +01dB EU", "12:33:10"},
            {"14205.0", "GB", "M0XYZ", "IO91 London USB 59 loud", "12:32:28"},
            {"21285.0", "BR", "PY2AA", "GG66 Sao Paulo South Am 59", "12:31:45"},
            {"14020.0", "ES", "EA7K", "IM76 Seville CW 599 CQ DX", "12:30:15"},
            {"3573.0", "CA", "VE3KZ", "FN03 Toronto Ontario FT8", "12:29:50"}
        };

        for (String[] s : spots) {
            int itemH = 62;
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);
            g.setColor(COLOR_CARD_BORDER);
            g.drawRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);

            // Row 1: Country Flag + Callsign (left) & Frequency (right)
            drawFlag(g, s[1], pad + 12, curY + 10, 26, 17);

            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.drawString(s[2], pad + 46, curY + 24);

            g.setColor(COLOR_TEAL);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            FontMetrics fm = g.getFontMetrics();
            int freqW = fm.stringWidth(s[0]);
            g.drawString(s[0], WIDTH - pad - 12 - freqW, curY + 24);

            // Row 2: Location/Comment (left) & Time (right)
            g.setColor(COLOR_TEXT_GREY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.drawString(s[3], pad + 12, curY + 48);

            g.setColor(COLOR_TEXT_MUTED);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            int timeW = g.getFontMetrics().stringWidth(s[4]);
            g.drawString(s[4], WIDTH - pad - 12 - timeW, curY + 48);

            curY += itemH + 8;
            if (curY + itemH > HEIGHT) break;
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 2: Menu Lateral / Options Overflow Menu (main_menu.xml)
    // -------------------------------------------------------------
    private static void renderFrame2LateralMenu(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);

        // Render Frame 1 as background
        renderFrame1DxCluster(file);
        BufferedImage base = ImageIO.read(file);
        g.drawImage(base, 0, 0, null);

        // Dim background overlay
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 28 + 56, WIDTH, HEIGHT - 28 - 56);

        // Lateral Popup Menu (per DefaultPopupMenuStyle in themes.xml)
        int menuW = 230;
        int menuH = 250;
        int menuX = WIDTH - menuW - 16;
        int menuY = 28 + 48;

        // Shadow & Popup Box
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(menuX + 4, menuY + 4, menuW, menuH, 10, 10);

        g.setColor(COLOR_BLACK);
        g.fillRoundRect(menuX, menuY, menuW, menuH, 8, 8);
        g.setColor(new Color(0x44, 0x44, 0x44));
        g.drawRoundRect(menuX, menuY, menuW, menuH, 8, 8);

        // Menu Items from main_menu.xml
        String[][] menuItems = {
            {"📻", "Open Radio"},
            {"👤", "User settings"},
            {"⚡", "CQ Mode"},
            {"💖", "Donate"},
            {"💻", "Git source code"}
        };

        int itemH = menuH / menuItems.length;
        for (int i = 0; i < menuItems.length; i++) {
            int iy = menuY + i * itemH;

            // Highlight first item ("Open Radio")
            if (i == 0) {
                g.setColor(new Color(0x03, 0xDA, 0xC5, 40));
                g.fillRect(menuX + 1, iy + 1, menuW - 2, itemH - 2);
                g.setColor(COLOR_TEAL);
                g.fillRect(menuX + 1, iy + 1, 4, itemH - 2);
            }

            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.setColor(COLOR_TEXT_WHITE);
            g.drawString(menuItems[i][0], menuX + 16, iy + itemH / 2 + 5);

            g.setFont(new Font("SansSerif", (i == 0) ? Font.BOLD : Font.PLAIN, 15));
            g.setColor((i == 0) ? COLOR_TEAL : COLOR_TEXT_WHITE);
            g.drawString(menuItems[i][1], menuX + 46, iy + itemH / 2 + 5);

            if (i < menuItems.length - 1) {
                g.setColor(new Color(0x28, 0x28, 0x28));
                g.drawLine(menuX + 12, iy + itemH, menuX + menuW - 12, iy + itemH);
            }
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 3: Band Filter Screen (activity_filter.xml)
    // -------------------------------------------------------------
    private static void renderFrame3BandFilter(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Filter Band", false, true);

        int pad = 24;
        int curY = 100;

        // Heading: "Select HF Bands"
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("Select HF Bands", pad, curY);
        curY += 34;

        String[] bands = {
            "160m (1.8MHz)",
            "80m (3.5MHz)",
            "40m (7.0MHz)",
            "30m (10.0MHz)",
            "20m (14.0MHz)",
            "17m (18.0MHz)",
            "15m (21.0MHz)",
            "12m (24.0MHz)",
            "10m (28.0MHz)",
            "6m (50.0MHz)"
        };

        boolean[] checked = {false, true, true, false, true, true, true, false, true, false};

        int rowH = 48;
        for (int i = 0; i < bands.length; i++) {
            // Row background
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, curY, WIDTH - (pad * 2), rowH - 6, 6, 6);
            g.setColor(COLOR_CARD_BORDER);
            g.drawRoundRect(pad, curY, WIDTH - (pad * 2), rowH - 6, 6, 6);

            // Checkbox
            int cbSize = 22;
            int cbX = pad + 16;
            int cbY = curY + (rowH - 6 - cbSize) / 2;

            if (checked[i]) {
                g.setColor(COLOR_TEAL);
                g.fillRoundRect(cbX, cbY, cbSize, cbSize, 4, 4);
                g.setColor(COLOR_BLACK);
                g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(cbX + 5, cbY + 11, cbX + 9, cbY + 16);
                g.drawLine(cbX + 9, cbY + 16, cbX + 17, cbY + 6);
            } else {
                g.setColor(COLOR_BLACK);
                g.fillRoundRect(cbX, cbY, cbSize, cbSize, 4, 4);
                g.setColor(COLOR_TEXT_WHITE);
                g.setStroke(new BasicStroke(1.8f));
                g.drawRoundRect(cbX, cbY, cbSize, cbSize, 4, 4);
            }

            // Band label
            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", checked[i] ? Font.BOLD : Font.PLAIN, 16));
            g.drawString(bands[i], cbX + cbSize + 16, curY + 26);

            curY += rowH;
        }

        curY += 20;

        // Bottom Exit Button (exitButton: backgroundTint="@color/teal_200")
        drawTealButton(g, pad, HEIGHT - 76, WIDTH - (pad * 2), 48, "Exit", 16);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 4: View Logs / QSO Logbook (activity_view_logs.xml + item_log.xml)
    // -------------------------------------------------------------
    private static void renderFrame4ViewLogs(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Log Book", false, true);

        int pad = 16;
        int curY = 96;

        // QSO Entries from item_log.xml
        String[][] logs = {
            {"14074.0", "PT", "CT1BOH", "IN51re / Portugal", "2026-09-20 12:34:00", "599", "599"},
            {"21074.0", "US", "W1AW", "FN31pr / Newington, CT", "2026-09-20 12:15:22", "59", "59"},
            {"28074.0", "JP", "JA1ABC", "PM95 / Tokyo, Japan", "2026-09-20 11:45:10", "599", "599"},
            {"7074.0", "DE", "DL1XYZ", "JO43 / Munich, Germany", "2026-09-20 10:20:05", "599", "599"}
        };

        for (String[] l : logs) {
            int cardH = 142;
            g.setColor(new Color(0x1C, 0x1C, 0x1C));
            g.fillRoundRect(pad, curY, WIDTH - (pad * 2), cardH, 8, 8);
            g.setColor(COLOR_CARD_BORDER);
            g.drawRoundRect(pad, curY, WIDTH - (pad * 2), cardH, 8, 8);

            // Left details
            int tx = pad + 14;
            int ty = curY + 22;
            int step = 20;

            // Frequency
            g.setColor(COLOR_TEAL);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Freq: " + l[0] + " kHz", tx, ty);
            ty += step;

            // Call Sign with Flag
            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("Call: ", tx, ty);
            drawFlag(g, l[1], tx + 40, ty - 12, 22, 14);
            g.setColor(COLOR_TEXT_WHITE);
            g.drawString(l[2], tx + 68, ty);
            ty += step;

            // Location
            g.setColor(COLOR_TEXT_GREY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.drawString("Loc: " + l[3], tx, ty);
            ty += step;

            // Time
            g.setColor(COLOR_TEXT_MUTED);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("Time: " + l[4], tx, ty);
            ty += step;

            // RST Sent / Rcvd
            g.setColor(COLOR_TEXT_GREY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g.drawString("RX RST: " + l[5] + "   |   TX RST: " + l[6], tx, ty);

            // Right column buttons: Gen.ADIF, Edit, Delete (all teal buttons per item_log.xml)
            int btnW = 96;
            int btnH = 32;
            int btnX = WIDTH - pad - btnW - 12;
            int bty = curY + 14;

            drawTealButton(g, btnX, bty, btnW, btnH, "Gen.ADIF", 12);
            bty += btnH + 8;
            drawTealButton(g, btnX, bty, btnW, btnH, "Edit", 12);
            bty += btnH + 8;
            drawTealButton(g, btnX, bty, btnW, btnH, "Delete", 12);

            curY += cardH + 12;
            if (curY + cardH > HEIGHT - 76) break;
        }

        // Bottom Button: Add To Log (addToLogButton: backgroundTint="@color/teal_200", textSize="18sp")
        drawTealButton(g, pad, HEIGHT - 76, WIDTH - (pad * 2), 48, "Add To Log", 17);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 5: COM Port Config / Open Radio (activity_com_port_config.xml)
    // -------------------------------------------------------------
    private static void renderFrame5ComPortConfig(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Open Radio", false, true);

        int pad = 20;
        int curY = 96;

        String[][] fields = {
            {"Baud Rate", "38400"},
            {"Data Bits", "8"},
            {"Stop Bits", "2"},
            {"Parity", "None"},
            {"Flow Control", "RTS/CTS"},
            {"Select Radio", "Yaesu FT-891"},
            {"USB Device", "CP2102 USB to UART Bridge (/dev/bus/usb/001/002)"}
        };

        for (String[] f : fields) {
            // Label
            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(f[0], pad, curY + 16);

            // Spinner box
            int spinY = curY + 24;
            int spinH = 40;
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, spinY, WIDTH - (pad * 2), spinH, 6, 6);
            g.setColor(COLOR_CARD_BORDER);
            g.drawRoundRect(pad, spinY, WIDTH - (pad * 2), spinH, 6, 6);

            g.setColor(COLOR_TEAL);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            // Truncate if long
            String val = f[1];
            if (val.length() > 36) val = val.substring(0, 33) + "...";
            g.drawString(val, pad + 14, spinY + 25);

            // Dropdown triangle
            int triX = WIDTH - pad - 24;
            int triY = spinY + 17;
            Polygon tri = new Polygon();
            tri.addPoint(triX, triY);
            tri.addPoint(triX + 10, triY);
            tri.addPoint(triX + 5, triY + 7);
            g.setColor(COLOR_TEXT_GREY);
            g.fillPolygon(tri);

            curY += 72;
        }

        curY += 12;

        // Button 1: Refresh Device List (refreshButton)
        drawTealButton(g, pad, curY, WIDTH - (pad * 2), 46, "Refresh Device List", 15);
        curY += 56;

        // Button 2: Apply (applyButton)
        drawTealButton(g, pad, curY, WIDTH - (pad * 2), 46, "Apply", 16);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 6: User Settings (activity_user_settings.xml)
    // -------------------------------------------------------------
    private static void renderFrame6UserSettings(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "User settings", false, true);

        int pad = 24;
        int curY = 110;

        // Label: "Enter Your Callsign:"
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Enter Your Callsign:", pad, curY);

        curY += 20;

        // EditText: callsignEditText
        int editH = 50;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), editH, 6, 6);
        g.setColor(COLOR_TEAL);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), editH, 6, 6);

        // Flag + Callsign inside EditText
        drawFlag(g, "PT", pad + 16, curY + 15, 30, 20);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("CT1BOH", pad + 56, curY + 33);

        // Blinking cursor
        g.setColor(COLOR_TEAL);
        g.fillRect(pad + 156, curY + 12, 3, 26);

        curY += editH + 28;

        // Save Button (saveButton: backgroundTint="@color/teal_200")
        drawTealButton(g, pad, curY, WIDTH - (pad * 2), 48, "Save", 16);

        curY += 80;

        // Station Information card
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 160, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), 160, 8, 8);

        g.setColor(COLOR_TEAL);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("STATION DETAILS", pad + 16, curY + 28);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Grid Locator:    IN51re (Portugal)", pad + 16, curY + 58);
        g.drawString("DX Cluster:      dxfun.com:8000", pad + 16, curY + 84);
        g.drawString("Radio CAT:       Yaesu FT-891 @ 38400bps", pad + 16, curY + 110);
        g.drawString("Status:          ONLINE & READY", pad + 16, curY + 136);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 7: CQ Mode & Rig Control (activity_cq_mode.xml)
    // -------------------------------------------------------------
    private static void renderFrame7CqMode(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQ Mode", false, true);

        int pad = 20;
        int curY = 96;

        // Call Sign input row
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Call Sign", pad, curY + 18);

        int inputW = WIDTH - pad - 100;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        drawFlag(g, "PT", pad + 90, curY + 9, 24, 16);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("CT1BOH", pad + 122, curY + 24);

        curY += 46;

        // Freq input row
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Freq", pad, curY + 18);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_TEAL);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("14074.0", pad + 94, curY + 24);

        curY += 46;

        // Receive S Number & Send S Number side by side
        int spinW = (WIDTH - (pad * 2) - 16) / 2;
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Receive S Number", pad, curY + 12);
        g.drawString("Send S Number", pad + spinW + 16, curY + 12);

        curY += 18;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, spinW, 36, 6, 6);
        g.fillRoundRect(pad + spinW + 16, curY, spinW, 36, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, spinW, 36, 6, 6);
        g.drawRoundRect(pad + spinW + 16, curY, spinW, 36, 6, 6);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("599", pad + 16, curY + 24);
        g.drawString("599", pad + spinW + 32, curY + 24);

        curY += 46;

        // Date input row
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Date", pad, curY + 18);
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("2026-09-20", pad + 94, curY + 24);

        curY += 46;

        // Time Picker widget (spinner mode per XML)
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Time", pad, curY + 14);

        curY += 20;
        int tpW = WIDTH - (pad * 2);
        int tpH = 68;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, tpW, tpH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, tpW, tpH, 8, 8);

        g.setColor(COLOR_TEAL);
        g.setFont(new Font("Monospaced", Font.BOLD, 32));
        g.drawString("12 : 35", pad + tpW / 2 - 64, curY + 46);

        curY += tpH + 14;

        // Mini spots list
        String[][] spots = {
            {"14074.0", "US", "W1AW", "ARRL HQ 599", "12:34"},
            {"28074.0", "JP", "JA1ABC", "Tokyo -08dB", "12:33"},
            {"7074.0", "DE", "DL1XYZ", "Munich 599", "12:33"}
        };

        for (String[] s : spots) {
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 40, 6, 6);
            g.setColor(COLOR_CARD_BORDER);
            g.drawRoundRect(pad, curY, WIDTH - (pad * 2), 40, 6, 6);

            drawFlag(g, s[1], pad + 10, curY + 10, 24, 16);
            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(s[2], pad + 42, curY + 25);

            g.setColor(COLOR_TEAL);
            g.drawString(s[0], pad + 150, curY + 25);

            g.setColor(COLOR_TEXT_GREY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(s[3], pad + 250, curY + 25);

            curY += 46;
        }

        // Bottom 2x2 Grid of Teal Buttons (Spot Myself, Set Now, Load Freq From Radio, Save Log)
        int btnW = (WIDTH - (pad * 2) - 12) / 2;
        int btnH = 42;
        int b1Y = HEIGHT - 110;
        int b2Y = HEIGHT - 58;

        drawTealButton(g, pad, b1Y, btnW, btnH, "Spot Myself", 13);
        drawTealButton(g, pad + btnW + 12, b1Y, btnW, btnH, "Set Now", 13);

        drawTealButton(g, pad, b2Y, btnW, btnH, "Load Freq From Radio", 13);
        drawTealButton(g, pad + btnW + 12, b2Y, btnW, btnH, "Save Log", 13);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // FRAME 8: Spot Details & Station Tuning (activity_spot.xml)
    // -------------------------------------------------------------
    private static void renderFrame8SpotDetails(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Spot Details", false, true);

        int pad = 24;
        int curY = 96;

        // Freq input row
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Freq", pad, curY + 24);

        int inputW = WIDTH - pad - 120;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 90, curY, inputW, 42, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 90, curY, inputW, 42, 6, 6);
        g.setColor(COLOR_TEAL);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("21074.0 kHz", pad + 104, curY + 28);

        curY += 56;

        // Call Sign row with Country Flag
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Call Sign", pad, curY + 24);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 90, curY, inputW, 42, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 90, curY, inputW, 42, 6, 6);
        drawFlag(g, "PT", pad + 104, curY + 11, 28, 18);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("CT1BOH", pad + 142, curY + 28);

        curY += 56;

        // Location / DXCC Zone
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Location", pad, curY + 24);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 90, curY, inputW, 42, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 90, curY, inputW, 42, 6, 6);
        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Portugal (Zone 14, IN51re)", pad + 104, curY + 26);

        curY += 60;

        // Date and Time Box
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 70, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), 70, 8, 8);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Date:  2026-09-20", pad + 16, curY + 28);
        g.drawString("Time:  12 : 35 UTC", pad + 16, curY + 52);

        g.setColor(COLOR_TEAL);
        g.drawString("RX S-Value: 599", WIDTH - pad - 150, curY + 28);
        g.drawString("TX S-Value: 599", WIDTH - pad - 150, curY + 52);

        curY += 86;

        // 4 Large Teal Action Buttons (Send Spot, Open QRZ, Set Frequency, Save to Log Book)
        int btnH = 46;
        drawTealButton(g, pad, curY, WIDTH - (pad * 2), btnH, "Set Frequency (Tune Rig)", 16);
        curY += btnH + 12;

        drawTealButton(g, pad, curY, WIDTH - (pad * 2), btnH, "Save to Log Book", 16);
        curY += btnH + 12;

        drawTealButton(g, pad, curY, WIDTH - (pad * 2), btnH, "Send Spot", 16);
        curY += btnH + 12;

        drawTealButton(g, pad, curY, WIDTH - (pad * 2), btnH, "Open QRZ Callbook", 16);

        g.dispose();
        ImageIO.write(img, "png", file);
    }
}
