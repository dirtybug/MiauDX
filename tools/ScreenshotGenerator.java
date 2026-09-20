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

        // Dedicated folders per radio and general
        File generalDir = new File(outputDir, "general");
        generalDir.mkdirs();

        File yaesuFt891Dir = new File(outputDir, "yaesu_ft891");
        yaesuFt891Dir.mkdirs();

        File ft891AliasDir = new File(outputDir, "FT891");
        ft891AliasDir.mkdirs();

        File radiosFt891Dir = new File(outputDir, "radios/yaesu_ft891");
        radiosFt891Dir.mkdirs();

        File yaesuFt857Dir = new File(outputDir, "yaesu_ft857");
        yaesuFt857Dir.mkdirs();

        File ft857AliasDir = new File(outputDir, "FT857");
        ft857AliasDir.mkdirs();

        File radiosFt857Dir = new File(outputDir, "radios/yaesu_ft857");
        radiosFt857Dir.mkdirs();

        System.out.println("=====================================================");
        System.out.println("       MiauDX Visual Frame Screenshot Generator      ");
        System.out.println("=====================================================");
        System.out.println("Output root folder: " + outputDir.getAbsolutePath());
        System.out.println("General folder:     " + generalDir.getAbsolutePath());
        System.out.println("Yaesu FT-891 folder:" + yaesuFt891Dir.getAbsolutePath());
        System.out.println("Yaesu FT-857 folder:" + yaesuFt857Dir.getAbsolutePath());

        try {
            // -------------------------------------------------------------
            // GENERAL APP SCREENSHOTS (general/ and root)
            // -------------------------------------------------------------
            // Frame 1: Main DX Cluster (activity_main.xml)
            File f1Gen = new File(generalDir, "frame_01_main_dx_cluster.png");
            renderFrame1DxCluster(f1Gen);
            copyFile(f1Gen, new File(outputDir, "frame_01_main_dx_cluster.png"));

            // Frame 2: Menu Lateral / Options Menu (main_menu.xml)
            File f2Menu = new File(generalDir, "frame_02_lateral_menu.png");
            renderFrame2LateralMenu(f2Menu);
            copyFile(f2Menu, new File(generalDir, "frame_02_menu_lateral.png"));
            copyFile(f2Menu, new File(outputDir, "frame_02_lateral_menu.png"));
            copyFile(f2Menu, new File(outputDir, "frame_02_menu_lateral.png"));

            // Frame 3: Band Filter (activity_filter.xml)
            File f3Filter = new File(generalDir, "frame_03_filter_band.png");
            renderFrame3BandFilter(f3Filter);
            copyFile(f3Filter, new File(generalDir, "frame_04_band_mode_filter.png"));
            copyFile(f3Filter, new File(outputDir, "frame_03_filter_band.png"));
            copyFile(f3Filter, new File(outputDir, "frame_04_band_mode_filter.png"));

            // Frame 4: View Logs / QSO Logbook (activity_view_logs.xml + item_log.xml)
            File f4Logs = new File(generalDir, "frame_04_view_logs.png");
            renderFrame4ViewLogs(f4Logs);
            copyFile(f4Logs, new File(generalDir, "frame_03_qso_logbook.png"));
            copyFile(f4Logs, new File(outputDir, "frame_04_view_logs.png"));
            copyFile(f4Logs, new File(outputDir, "frame_03_qso_logbook.png"));

            // Frame 5: COM Port Config / Open Radio (activity_com_port_config.xml)
            File f5Port = new File(generalDir, "frame_05_com_port_config.png");
            renderFrame5ComPortConfig(f5Port);
            copyFile(f5Port, new File(generalDir, "frame_06_usb_cat_config.png"));
            copyFile(f5Port, new File(outputDir, "frame_05_com_port_config.png"));
            copyFile(f5Port, new File(outputDir, "frame_06_usb_cat_config.png"));

            // Frame 6: User Settings (activity_user_settings.xml)
            File f6Settings = new File(generalDir, "frame_06_user_settings.png");
            renderFrame6UserSettings(f6Settings);
            copyFile(f6Settings, new File(generalDir, "frame_07_user_settings.png"));
            copyFile(f6Settings, new File(outputDir, "frame_06_user_settings.png"));
            copyFile(f6Settings, new File(outputDir, "frame_07_user_settings.png"));

            // Frame 7: CQ Mode & Rig Control (activity_cq_mode.xml)
            File f7Cq = new File(generalDir, "frame_07_cq_mode.png");
            renderFrame7CqMode(f7Cq);
            copyFile(f7Cq, new File(outputDir, "frame_07_cq_mode.png"));

            // Frame 8: Spot Details & Tuning (activity_spot.xml & activity_edit_log.xml)
            File f8Spot = new File(generalDir, "frame_08_spot_details.png");
            renderFrame8SpotDetails(f8Spot);
            copyFile(f8Spot, new File(generalDir, "frame_05_spot_details.png"));
            copyFile(f8Spot, new File(generalDir, "frame_08_edit_qso_log.png"));
            copyFile(f8Spot, new File(outputDir, "frame_08_spot_details.png"));
            copyFile(f8Spot, new File(outputDir, "frame_05_spot_details.png"));
            copyFile(f8Spot, new File(outputDir, "frame_08_edit_qso_log.png"));

            // -------------------------------------------------------------
            // DEDICATED YAESU FT-891 SCREENSHOTS (yaesu_ft891/ and FT891/)
            // -------------------------------------------------------------
            // 1. FT-891 CAT Rig Control & LCD VFO Display
            File ft1 = new File(yaesuFt891Dir, "01_yaesu_ft891_cat_rig_control.png");
            renderFrame2YaesuFT891Simulation(ft1);
            copyFile(ft1, new File(yaesuFt891Dir, "frame_01_cat_rig_control.png"));
            copyFile(ft1, new File(outputDir, "frame_02_cat_rig_control.png"));
            copyFile(ft1, new File(outputDir, "frame_02_yaesu_ft891_simulation.png"));

            // 2. FT-891 CAT Protocol Monitor (Serial trace)
            File ft2 = new File(yaesuFt891Dir, "02_yaesu_ft891_cat_protocol_monitor.png");
            renderFrameYaesuFT891CatProtocolMonitor(ft2);
            copyFile(ft2, new File(yaesuFt891Dir, "frame_02_cat_protocol_monitor.png"));

            // 3. FT-891 COM Port Configuration (38400, 8N2)
            File ft3 = new File(yaesuFt891Dir, "03_yaesu_ft891_com_port_config.png");
            renderFrame5ComPortConfig(ft3);
            copyFile(ft3, new File(yaesuFt891Dir, "frame_03_com_port_config.png"));

            // 4. FT-891 DX Cluster Live QSY & VFO Sync
            File ft4 = new File(yaesuFt891Dir, "04_yaesu_ft891_dx_cluster_qsy.png");
            renderFrameYaesuFT891DxClusterQsy(ft4);
            copyFile(ft4, new File(yaesuFt891Dir, "frame_04_dx_cluster_qsy.png"));

            // 5. FT-891 CQ Mode & Auto-Tune
            File ft5 = new File(yaesuFt891Dir, "05_yaesu_ft891_cq_mode.png");
            renderFrameYaesuFT891CqMode(ft5);
            copyFile(ft5, new File(yaesuFt891Dir, "frame_05_cq_mode.png"));

            // Copy all Yaesu FT-891 screenshots to alias directories
            for (File src : yaesuFt891Dir.listFiles()) {
                if (src.isFile()) {
                    copyFile(src, new File(ft891AliasDir, src.getName()));
                    copyFile(src, new File(radiosFt891Dir, src.getName()));
                }
            }

            // -------------------------------------------------------------
            // DEDICATED YAESU FT-857 / FT-857D SCREENSHOTS (yaesu_ft857/ and FT857/)
            // -------------------------------------------------------------
            // 1. FT-857D CAT Rig Control & LCD Display
            File ft857_1 = new File(yaesuFt857Dir, "01_yaesu_ft857_cat_rig_control.png");
            renderFrameYaesuFT857Simulation(ft857_1);
            copyFile(ft857_1, new File(yaesuFt857Dir, "frame_01_cat_rig_control.png"));

            // 2. FT-857D CAT Protocol Monitor (5-byte binary serial trace)
            File ft857_2 = new File(yaesuFt857Dir, "02_yaesu_ft857_cat_protocol_monitor.png");
            renderFrameYaesuFT857CatProtocolMonitor(ft857_2);
            copyFile(ft857_2, new File(yaesuFt857Dir, "frame_02_cat_protocol_monitor.png"));

            // 3. FT-857D COM Port Configuration (9600, 8N2)
            File ft857_3 = new File(yaesuFt857Dir, "03_yaesu_ft857_com_port_config.png");
            renderFrameYaesuFT857ComPortConfig(ft857_3);
            copyFile(ft857_3, new File(yaesuFt857Dir, "frame_03_com_port_config.png"));

            // 4. FT-857D DX Cluster Live QSY & VFO Sync
            File ft857_4 = new File(yaesuFt857Dir, "04_yaesu_ft857_dx_cluster_qsy.png");
            renderFrameYaesuFT857DxClusterQsy(ft857_4);
            copyFile(ft857_4, new File(yaesuFt857Dir, "frame_04_dx_cluster_qsy.png"));

            // 5. FT-857D CQ Mode & Auto-Tune
            File ft857_5 = new File(yaesuFt857Dir, "05_yaesu_ft857_cq_mode.png");
            renderFrameYaesuFT857CqMode(ft857_5);
            copyFile(ft857_5, new File(yaesuFt857Dir, "frame_05_cq_mode.png"));

            // Copy all Yaesu FT-857 screenshots to alias directories
            for (File src : yaesuFt857Dir.listFiles()) {
                if (src.isFile()) {
                    copyFile(src, new File(ft857AliasDir, src.getName()));
                    copyFile(src, new File(radiosFt857Dir, src.getName()));
                }
            }

            System.out.println("✓ Generated all visual screenshots organized by radio folders!");
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
    // FRAME 2B: Yaesu FT-891 CAT Rig Control & Simulation
    // -------------------------------------------------------------
    private static void renderFrame2YaesuFT891Simulation(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Yaesu FT-891 CAT Control", true, true);

        int pad = 16;
        int curY = 94;

        // Card 1: Rig Connection Banner
        int bannerH = 72;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), bannerH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), bannerH, 8, 8);

        // Green dot for CONNECTED status
        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 16, curY + 18, 12, 12);

        // Rig title
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("YAESU FT-891 (HF/50MHz Transceiver)", pad + 36, curY + 28);

        // Rig status subtitle
        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("Status: CONNECTED (Simulated USB-CAT | 38400 baud, 8N2)", pad + 36, curY + 48);

        // Badges: Model & Tested
        int badgeW = 68;
        int badgeH = 20;
        int badgeX = WIDTH - pad - badgeW - 12;
        g.setColor(new Color(0x00, 0xE6, 0x76, 40));
        g.fillRoundRect(badgeX, curY + 14, badgeW, badgeH, 4, 4);
        g.setColor(COLOR_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString("VERIFIED", badgeX + 11, curY + 28);

        curY += bannerH + 14;

        // Card 2: Yaesu FT-891 High-Contrast Amber/Dark LCD VFO Display
        int lcdH = 175;
        g.setColor(new Color(0x10, 0x14, 0x14));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), lcdH, 10, 10);
        g.setColor(new Color(0x02, 0x88, 0x78));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), lcdH, 10, 10);
        g.setStroke(new BasicStroke(1f));

        // LCD Header
        g.setColor(COLOR_TEAL);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("YAESU FT-891 VFO DISPLAY", pad + 16, curY + 22);

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString("FILTER: 3.0kHz | AGC: FAST | IPO: ON | ATT: OFF", pad + 16, curY + 38);

        // VFO-A Frequency (Primary tuned frequency)
        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("VFO-A", pad + 16, curY + 70);

        // Digits
        g.setColor(COLOR_AMBER);
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.drawString("14.074.000", pad + 80, curY + 75);

        g.setColor(COLOR_TEAL);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("MHz", pad + 330, curY + 70);

        // Mode badge (USB-DATA)
        int modeW = 75;
        int modeH = 26;
        g.setColor(new Color(0xFF, 0xB3, 0x00, 50));
        g.fillRoundRect(WIDTH - pad - modeW - 16, curY + 52, modeW, modeH, 4, 4);
        g.setColor(COLOR_AMBER);
        g.drawRoundRect(WIDTH - pad - modeW - 16, curY + 52, modeW, modeH, 4, 4);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("USB-DATA", WIDTH - pad - modeW - 10, curY + 70);

        // VFO-B Frequency (Sub)
        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("VFO-B: 07.074.000 MHz (40m) [LSB]", pad + 16, curY + 104);

        g.setColor(new Color(0x30, 0x40, 0x40));
        g.drawLine(pad + 16, curY + 116, WIDTH - pad - 16, curY + 116);

        // S-Meter Graphics
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("SIG / S-METER", pad + 16, curY + 134);

        // S-Meter Bar Background & Ticks
        int smX = pad + 110;
        int smY = curY + 124;
        int smW = WIDTH - pad * 2 - 130;
        int smH = 14;

        g.setColor(new Color(0x20, 0x25, 0x25));
        g.fillRoundRect(smX, smY, smW, smH, 3, 3);

        // Fill S-Meter segments (up to S9 + 10dB)
        int fillW = (int) (smW * 0.72);
        for (int b = 0; b < fillW; b += 6) {
            if (b < smW * 0.55) {
                g.setColor(COLOR_GREEN); // S1 - S9
            } else {
                g.setColor(new Color(0xFF, 0x52, 0x52)); // +10 to +60dB
            }
            g.fillRect(smX + b, smY + 2, 4, smH - 4);
        }

        // S-meter labels
        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g.drawString("S1   3   5   7   9   +20  +40dB", smX, curY + 152);

        // Power out and SWR indicators
        g.setColor(COLOR_TEAL);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString("PO: 100W   SWR: 1.1", pad + 16, curY + 168);

        curY += lcdH + 14;

        // Card 3: Real-Time CAT Protocol Log / Simulated Port Monitor
        int catH = 195;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), catH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), catH, 8, 8);

        // Title of CAT monitor
        g.setColor(COLOR_TEAL);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("YAESU FT-891 CAT PROTOCOL MONITOR", pad + 14, curY + 22);

        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.drawString("Port: /dev/ttyUSB0 (Simulated)", WIDTH - pad - 190, curY + 22);

        // Log Terminal Box
        int termX = pad + 12;
        int termY = curY + 32;
        int termW = WIDTH - pad * 2 - 24;
        int termH = catH - 44;
        g.setColor(COLOR_BLACK);
        g.fillRoundRect(termX, termY, termW, termH, 6, 6);
        g.setColor(new Color(0x33, 0x33, 0x33));
        g.drawRoundRect(termX, termY, termW, termH, 6, 6);

        // Serial CAT Packets
        String[][] catPackets = {
            {"TX", "FA;", "Poll VFO-A Frequency"},
            {"RX", "FA00014074000;", "VFO-A = 14.074.000 Hz (20m)"},
            {"TX", "MD0;", "Poll Operating Mode"},
            {"RX", "MD01;", "Mode = USB (Upper Sideband)"},
            {"TX", "SM0;", "Poll S-Meter Signal Strength"},
            {"RX", "SM0009;", "S-Meter = S9 (+10dB)"},
            {"TX", "FA00014074000;", "Sync DX Spot CT1BOH to Radio"},
            {"RX", "FA00014074000;", "Command ACK (Frequency Locked)"}
        };

        int logY = termY + 18;
        int rowH = 17;
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        for (String[] pkt : catPackets) {
            boolean isTx = pkt[0].equals("TX");
            g.setColor(isTx ? COLOR_BLUE : COLOR_GREEN);
            g.drawString("[" + pkt[0] + "]", termX + 8, logY);

            g.setColor(COLOR_TEXT_WHITE);
            g.drawString(pkt[1], termX + 44, logY);

            g.setColor(COLOR_TEXT_MUTED);
            g.drawString("// " + pkt[2], termX + 170, logY);

            logY += rowH;
        }

        curY += catH + 14;

        // Card 4: Quick QSY Band & Rig Tuning Controls
        int ctrlH = HEIGHT - curY - 20;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), ctrlH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), ctrlH, 8, 8);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("QUICK RIG CONTROLS (TUNE & SYNC)", pad + 14, curY + 22);

        // Band Buttons Row
        String[] bands = {"160m", "80m", "40m", "20m", "15m", "10m", "6m"};
        int bandPad = pad + 12;
        int bandBtnW = (WIDTH - pad * 2 - 24 - (bands.length - 1) * 6) / bands.length;
        int bandBtnH = 34;
        int bandY = curY + 34;

        for (int i = 0; i < bands.length; i++) {
            int bx = bandPad + i * (bandBtnW + 6);
            boolean is20m = bands[i].equals("20m");
            if (is20m) {
                drawTealButton(g, bx, bandY, bandBtnW, bandBtnH, bands[i], 12);
            } else {
                g.setColor(new Color(0x30, 0x30, 0x30));
                g.fillRoundRect(bx, bandY, bandBtnW, bandBtnH, 6, 6);
                g.setColor(COLOR_CARD_BORDER);
                g.drawRoundRect(bx, bandY, bandBtnW, bandBtnH, 6, 6);
                g.setColor(COLOR_TEXT_WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                int bw = g.getFontMetrics().stringWidth(bands[i]);
                g.drawString(bands[i], bx + (bandBtnW - bw) / 2, bandY + 21);
            }
        }

        // Action Buttons: "Tune to Spot", "Read Status"
        int actY = bandY + bandBtnH + 12;
        int actBtnW = (WIDTH - pad * 2 - 24 - 12) / 2;
        int actBtnH = 42;

        drawTealButton(g, pad + 12, actY, actBtnW, actBtnH, "⚡ Sync Spot to FT-891", 13);

        g.setColor(new Color(0x30, 0x30, 0x30));
        g.fillRoundRect(pad + 12 + actBtnW + 12, actY, actBtnW, actBtnH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 12 + actBtnW + 12, actY, actBtnW, actBtnH, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String readText = "🔄 Read Rig Status";
        int rtw = g.getFontMetrics().stringWidth(readText);
        g.drawString(readText, pad + 12 + actBtnW + 12 + (actBtnW - rtw) / 2, actY + 26);

        // Active Spot Sync footer
        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Active Target: ", pad + 14, actY + actBtnH + 22);
        drawFlag(g, "PT", pad + 95, actY + actBtnH + 10, 18, 12);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("CT1BOH @ 14.074.000 MHz (FT8 EU) -> FT-891 VFO-A Locked", pad + 120, actY + actBtnH + 22);

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

    // -------------------------------------------------------------
    // YAESU FT-891: Dedicated CAT Protocol Serial Monitor
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT891CatProtocolMonitor(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "FT-891 CAT Serial Monitor", true, true);

        int pad = 16;
        int curY = 94;

        // Port Parameters Header Card
        int hdrH = 78;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), hdrH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), hdrH, 8, 8);

        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 16, curY + 16, 10, 10);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("SERIAL CAT: /dev/ttyUSB0 (CP2102 UART)", pad + 34, curY + 26);

        g.setColor(COLOR_TEAL);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("38400 BAUD | 8 DATA BITS | 2 STOP BITS | NO PARITY", pad + 16, curY + 48);

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString("FLOW: RTS/CTS | DTR: ON | RTS: ON | CTS: HIGH", pad + 16, curY + 66);

        curY += hdrH + 12;

        // Serial Terminal Box
        int termH = HEIGHT - curY - 96;
        g.setColor(new Color(0x0C, 0x10, 0x10));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), termH, 8, 8);
        g.setColor(new Color(0x22, 0x33, 0x33));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), termH, 8, 8);

        // Terminal Title Bar
        g.setColor(new Color(0x16, 0x1E, 0x1E));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 28, 8, 8);
        g.fillRect(pad, curY + 16, WIDTH - (pad * 2), 12);
        g.setColor(COLOR_TEAL);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString("CAT PROTOCOL PACKET TRACE (HEX / ASCII)", pad + 12, curY + 18);

        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.drawString("AUTO-SCROLL: ON", WIDTH - pad - 120, curY + 18);

        // Packet Rows
        String[][] packets = {
            {"12:35:00.102", "TX", "FA;", "Poll VFO-A Frequency"},
            {"12:35:00.118", "RX", "FA00014074000;", "VFO-A = 14.074.000 Hz (20m)"},
            {"12:35:00.130", "TX", "FB;", "Poll VFO-B Frequency"},
            {"12:35:00.144", "RX", "FB00007074000;", "VFO-B = 7.074.000 Hz (40m)"},
            {"12:35:00.155", "TX", "MD0;", "Poll Operating Mode"},
            {"12:35:00.168", "RX", "MD01;", "Mode: USB (Upper Sideband)"},
            {"12:35:00.180", "TX", "SM0;", "Poll S-Meter Level"},
            {"12:35:00.196", "RX", "SM0009;", "Signal Strength: S9 (+10dB)"},
            {"12:35:01.210", "TX", "FA00021074000;", "QSY to 15m Band (21.074 MHz)"},
            {"12:35:01.232", "RX", "FA00021074000;", "Ack: Frequency Synthesizer Locked"},
            {"12:35:01.245", "TX", "MD01;", "Set Mode USB"},
            {"12:35:01.260", "RX", "MD01;", "Ack: Operating Mode Confirmed"},
            {"12:35:02.100", "TX", "FA00014074000;", "Sync DX Spot CT1BOH to VFO-A"},
            {"12:35:02.122", "RX", "FA00014074000;", "Ack: VFO-A = 14.074.000 Hz"},
            {"12:35:03.450", "TX", "TX1;", "PTT Active (Key Transmitter)"},
            {"12:35:03.468", "RX", "TX1;", "Ack: Transceiver in TX Mode (100W)"},
            {"12:35:05.800", "TX", "TX0;", "PTT Released (Return to RX)"},
            {"12:35:05.818", "RX", "TX0;", "Ack: Transceiver in RX Mode"},
            {"12:35:06.010", "TX", "SM0;", "Poll S-Meter Signal Strength"},
            {"12:35:06.025", "RX", "SM0008;", "Signal Strength: S8"}
        };

        int lineY = curY + 46;
        int rowStep = 18;
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));

        for (String[] p : packets) {
            if (lineY + rowStep > curY + termH - 8) break;

            // Timestamp
            g.setColor(COLOR_TEXT_MUTED);
            g.drawString(p[0], pad + 10, lineY);

            // Direction badge
            boolean isTx = p[1].equals("TX");
            g.setColor(isTx ? COLOR_BLUE : COLOR_GREEN);
            g.drawString("[" + p[1] + "]", pad + 106, lineY);

            // Data string
            g.setColor(COLOR_TEXT_WHITE);
            g.drawString(p[2], pad + 140, lineY);

            // Comment
            g.setColor(new Color(0x60, 0x80, 0x80));
            g.drawString("// " + p[3], pad + 256, lineY);

            lineY += rowStep;
        }

        // Bottom Controls
        int botY = HEIGHT - 80;
        int btnW = (WIDTH - pad * 2 - 12) / 2;
        drawTealButton(g, pad, botY, btnW, 44, "Send Custom CAT", 13);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + btnW + 12, botY, btnW, 44, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + btnW + 12, botY, btnW, 44, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String exp = "Export Trace (.log)";
        int ew = g.getFontMetrics().stringWidth(exp);
        g.drawString(exp, pad + btnW + 12 + (btnW - ew) / 2, botY + 27);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // YAESU FT-891: DX Cluster with Live Rig Sync & QSY
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT891DxClusterQsy(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQMiau - Yaesu FT-891 Live", true, false);

        int pad = 16;
        int curY = 94;

        // Yaesu FT-891 Active Rig Banner
        int rigH = 50;
        g.setColor(new Color(0x10, 0x22, 0x20));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), rigH, 8, 8);
        g.setColor(new Color(0x00, 0xCC, 0x99));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), rigH, 8, 8);

        // Icon + Rig Status
        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 14, curY + 18, 12, 12);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("RIG: YAESU FT-891", pad + 34, curY + 22);

        g.setColor(COLOR_TEAL);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("14.074.000 MHz (USB-DATA)", pad + 34, curY + 40);

        // Sync pill badge
        int syncW = 90;
        int syncH = 24;
        int syncX = WIDTH - pad - syncW - 12;
        g.setColor(new Color(0x00, 0xE6, 0x76, 40));
        g.fillRoundRect(syncX, curY + 13, syncW, syncH, 12, 12);
        g.setColor(COLOR_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString("VFO-A SYNCED", syncX + 10, curY + 29);

        curY += rigH + 12;

        // Top Buttons (Filter Band & View Log)
        int gap = 12;
        int btnW = (WIDTH - (pad * 2) - gap) / 2;
        int btnH = 42;
        drawTealButton(g, pad, curY, btnW, btnH, "Filter Band", 14);
        drawTealButton(g, pad + btnW + gap, curY, btnW, btnH, "View Log", 14);

        curY += btnH + 14;

        // DX Cluster Spots with First Spot Tuned to FT-891
        String[][] spots = {
            {"14074.0", "PT", "CT1BOH", "IN51re FT8 +03dB Strong EU", "12:35:12", "TUNED"},
            {"21074.0", "US", "W1AW", "FN31pr ARRL HQ Special 599", "12:34:40", "TAP TO QSY"},
            {"28074.0", "JP", "JA1ABC", "PM95 Tokyo FT8 -08dB JA net", "12:33:55", "TAP TO QSY"},
            {"7074.0", "DE", "DL1XYZ", "JO43 Munich FT8 +01dB EU", "12:33:10", "TAP TO QSY"},
            {"14205.0", "GB", "M0XYZ", "IO91 London USB 59 loud", "12:32:28", "TAP TO QSY"},
            {"21285.0", "BR", "PY2AA", "GG66 Sao Paulo South Am 59", "12:31:45", "TAP TO QSY"},
            {"14020.0", "ES", "EA7K", "IM76 Seville CW 599 CQ DX", "12:30:15", "TAP TO QSY"}
        };

        for (int i = 0; i < spots.length; i++) {
            String[] s = spots[i];
            int itemH = 68;
            boolean isTuned = s[5].equals("TUNED");

            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);

            if (isTuned) {
                g.setColor(COLOR_TEAL);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);
                g.setStroke(new BasicStroke(1f));
            } else {
                g.setColor(COLOR_CARD_BORDER);
                g.drawRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);
            }

            // Flag + Callsign
            drawFlag(g, s[1], pad + 12, curY + 12, 26, 17);

            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.drawString(s[2], pad + 46, curY + 26);

            // Frequency
            g.setColor(isTuned ? COLOR_AMBER : COLOR_TEAL);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            FontMetrics fm = g.getFontMetrics();
            int freqW = fm.stringWidth(s[0]);
            g.drawString(s[0], WIDTH - pad - 12 - freqW, curY + 26);

            // Comment & Time
            g.setColor(COLOR_TEXT_GREY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(s[3], pad + 12, curY + 50);

            // Rig sync status pill
            if (isTuned) {
                int pillW = 92;
                int pillH = 18;
                int pillX = WIDTH - pad - 12 - pillW;
                int pillY = curY + 40;
                g.setColor(new Color(0x00, 0xE6, 0x76, 40));
                g.fillRoundRect(pillX, pillY, pillW, pillH, 4, 4);
                g.setColor(COLOR_GREEN);
                g.setFont(new Font("SansSerif", Font.BOLD, 9));
                g.drawString("TUNED ON FT-891", pillX + 6, pillY + 13);
            } else {
                g.setColor(COLOR_TEXT_MUTED);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                int tw = g.getFontMetrics().stringWidth(s[4]);
                g.drawString(s[4], WIDTH - pad - 12 - tw, curY + 52);
            }

            curY += itemH + 8;
            if (curY + itemH > HEIGHT - 30) break;
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // YAESU FT-891: CQ Mode & Auto-Tune
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT891CqMode(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQ Mode - Yaesu FT-891", false, true);

        int pad = 20;
        int curY = 96;

        // Banner: Auto-Tune Active
        int banH = 46;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), banH, 6, 6);
        g.setColor(COLOR_TEAL);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), banH, 6, 6);

        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 14, curY + 17, 12, 12);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("AUTO-TUNE ACTIVE: YAESU FT-891", pad + 36, curY + 24);

        g.setColor(COLOR_TEAL);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString("CAT Control: VFO-A & PTT Automated via USB", pad + 36, curY + 38);

        curY += banH + 16;

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

        // Signal Reports
        int spinW = (WIDTH - (pad * 2) - 16) / 2;
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Receive S Number", pad, curY + 12);
        g.drawString("Send S Number", pad + spinW + 16, curY + 12);

        curY += 18;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("599", pad + 16, curY + 25);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + spinW + 16, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + spinW + 16, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("599", pad + spinW + 32, curY + 25);

        curY += 56;

        // Radio Status Box
        int rigBoxH = 110;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), rigBoxH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), rigBoxH, 8, 8);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("YAESU FT-891 LIVE TELEMETRY", pad + 14, curY + 22);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("VFO-A: 14.074.000 Hz  |  MODE: USB-DATA", pad + 14, curY + 44);
        g.drawString("POWER: 100 Watts      |  SWR:  1.1:1", pad + 14, curY + 64);
        g.drawString("S-METER: S9 (+10dB)   |  CAT:  ACK OK", pad + 14, curY + 84);

        curY += rigBoxH + 18;

        // Action Buttons
        drawTealButton(g, pad, curY, WIDTH - (pad * 2), 48, "⚡ Tune Radio to Spot & Call CQ", 15);
        curY += 58;

        g.setColor(new Color(0x30, 0x30, 0x30));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 48, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), 48, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        String logQso = "📝 Save QSO to Logbook";
        int lqw = g.getFontMetrics().stringWidth(logQso);
        g.drawString(logQso, pad + (WIDTH - (pad * 2) - lqw) / 2, curY + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // =============================================================
    // YAESU FT-857 / FT-857D DEDICATED SCREENSHOT RENDERERS
    // =============================================================

    // -------------------------------------------------------------
    // 1. FT-857D CAT Rig Control & LCD Display
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT857Simulation(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Yaesu FT-857D CAT Control", true, true);

        int pad = 16;
        int curY = 94;

        // Card 1: Rig Connection Banner
        int bannerH = 72;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), bannerH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), bannerH, 8, 8);

        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 16, curY + 18, 12, 12);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("YAESU FT-857D (HF/VHF/UHF Transceiver)", pad + 36, curY + 28);

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("Status: CONNECTED (Simulated USB-CAT | 9600 baud, 8N2)", pad + 36, curY + 48);

        int badgeW = 68;
        int badgeH = 20;
        int badgeX = WIDTH - pad - badgeW - 12;
        g.setColor(new Color(0x00, 0xE6, 0x76, 40));
        g.fillRoundRect(badgeX, curY + 14, badgeW, badgeH, 4, 4);
        g.setColor(COLOR_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString("VERIFIED", badgeX + 11, curY + 28);

        curY += bannerH + 14;

        // Card 2: Yaesu FT-857D Dot-Matrix Amber LCD VFO Display
        int lcdH = 175;
        g.setColor(new Color(0x18, 0x14, 0x0C));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), lcdH, 10, 10);
        g.setColor(new Color(0xAA, 0x70, 0x10));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), lcdH, 10, 10);
        g.setStroke(new BasicStroke(1f));

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("YAESU FT-857D DOT-MATRIX LCD DISPLAY", pad + 16, curY + 22);

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString("DSP: DNR/DNF/DBF | IPO: ON | ATT: OFF | NB: ON", pad + 16, curY + 38);

        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("VFO-A", pad + 16, curY + 70);

        g.setColor(new Color(0xFF, 0xAA, 0x00));
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.drawString("14.074.000", pad + 80, curY + 75);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("MHz", pad + 330, curY + 70);

        int modeW = 75;
        int modeH = 26;
        g.setColor(new Color(0xFF, 0xB3, 0x00, 50));
        g.fillRoundRect(WIDTH - pad - modeW - 16, curY + 52, modeW, modeH, 4, 4);
        g.setColor(COLOR_AMBER);
        g.drawRoundRect(WIDTH - pad - modeW - 16, curY + 52, modeW, modeH, 4, 4);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("USB-DATA", WIDTH - pad - modeW - 10, curY + 70);

        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("VFO-B: 07.074.000 MHz (40m) [LSB] | V/U: 144/430 Ready", pad + 16, curY + 104);

        g.setColor(new Color(0x44, 0x33, 0x18));
        g.drawLine(pad + 16, curY + 116, WIDTH - pad - 16, curY + 116);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString("SIG / S-METER", pad + 16, curY + 134);

        int smX = pad + 110;
        int smY = curY + 124;
        int smW = WIDTH - pad * 2 - 130;
        int smH = 14;

        g.setColor(new Color(0x25, 0x20, 0x15));
        g.fillRoundRect(smX, smY, smW, smH, 3, 3);

        int fillW = (int) (smW * 0.72);
        for (int b = 0; b < fillW; b += 6) {
            if (b < smW * 0.55) {
                g.setColor(COLOR_AMBER);
            } else {
                g.setColor(new Color(0xFF, 0x44, 0x44));
            }
            g.fillRect(smX + b, smY + 2, 4, smH - 4);
        }

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g.drawString("S1   3   5   7   9   +20  +40dB", smX, curY + 152);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString("PO: 100W (HF) / 50W (VHF)   SWR: 1.1", pad + 16, curY + 168);

        curY += lcdH + 14;

        // Card 3: 5-Byte Binary CAT Protocol Monitor
        int catH = 195;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), catH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), catH, 8, 8);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("YAESU FT-857D 5-BYTE BINARY CAT PROTOCOL", pad + 14, curY + 22);

        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.drawString("Port: /dev/ttyUSB0 (9600 8N2)", WIDTH - pad - 190, curY + 22);

        int termX = pad + 12;
        int termY = curY + 32;
        int termW = WIDTH - pad * 2 - 24;
        int termH = catH - 44;
        g.setColor(COLOR_BLACK);
        g.fillRoundRect(termX, termY, termW, termH, 6, 6);
        g.setColor(new Color(0x33, 0x33, 0x33));
        g.drawRoundRect(termX, termY, termW, termH, 6, 6);

        String[][] catPackets = {
            {"TX", "01 40 74 00 01", "Opcode 0x01: Set VFO-A (14.074 MHz)"},
            {"RX", "00",             "Ack: Frequency Synthesizer Locked"},
            {"TX", "01 00 00 00 07", "Opcode 0x07: Set Mode USB (0x01)"},
            {"RX", "00",             "Ack: Operating Mode Confirmed"},
            {"TX", "00 00 00 00 03", "Opcode 0x03: Read Freq & Mode Status"},
            {"RX", "01 40 74 00 01", "Returns 14.074.000 Hz, Mode USB"},
            {"TX", "00 00 00 00 E7", "Opcode 0xE7: Read RX Status / S-Meter"},
            {"RX", "09",             "Signal Level S9 (+10dB)"}
        };

        int logY = termY + 18;
        int rowH = 17;
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        for (String[] pkt : catPackets) {
            boolean isTx = pkt[0].equals("TX");
            g.setColor(isTx ? COLOR_BLUE : COLOR_AMBER);
            g.drawString("[" + pkt[0] + "]", termX + 8, logY);

            g.setColor(COLOR_TEXT_WHITE);
            g.drawString(pkt[1], termX + 44, logY);

            g.setColor(COLOR_TEXT_MUTED);
            g.drawString("// " + pkt[2], termX + 175, logY);

            logY += rowH;
        }

        curY += catH + 14;

        // Card 4: Quick QSY Band & Rig Tuning Controls (including VHF/UHF)
        int ctrlH = HEIGHT - curY - 20;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), ctrlH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), ctrlH, 8, 8);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("QUICK RIG CONTROLS (ALL-BAND HF/VHF/UHF)", pad + 14, curY + 22);

        String[] bands = {"160m", "80m", "40m", "20m", "15m", "10m", "6m", "2m", "70cm"};
        int bandPad = pad + 12;
        int bandBtnW = (WIDTH - pad * 2 - 24 - (bands.length - 1) * 4) / bands.length;
        int bandBtnH = 34;
        int bandY = curY + 34;

        for (int i = 0; i < bands.length; i++) {
            int bx = bandPad + i * (bandBtnW + 4);
            boolean is20m = bands[i].equals("20m");
            if (is20m) {
                drawTealButton(g, bx, bandY, bandBtnW, bandBtnH, bands[i], 11);
            } else {
                g.setColor(new Color(0x30, 0x30, 0x30));
                g.fillRoundRect(bx, bandY, bandBtnW, bandBtnH, 6, 6);
                g.setColor(COLOR_CARD_BORDER);
                g.drawRoundRect(bx, bandY, bandBtnW, bandBtnH, 6, 6);
                g.setColor(COLOR_TEXT_WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                int bw = g.getFontMetrics().stringWidth(bands[i]);
                g.drawString(bands[i], bx + (bandBtnW - bw) / 2, bandY + 21);
            }
        }

        int actY = bandY + bandBtnH + 12;
        int actBtnW = (WIDTH - pad * 2 - 24 - 12) / 2;
        int actBtnH = 42;

        drawTealButton(g, pad + 12, actY, actBtnW, actBtnH, "⚡ Sync Spot to FT-857D", 13);

        g.setColor(new Color(0x30, 0x30, 0x30));
        g.fillRoundRect(pad + 12 + actBtnW + 12, actY, actBtnW, actBtnH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 12 + actBtnW + 12, actY, actBtnW, actBtnH, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String readText = "🔄 Read Rig Status";
        int rtw = g.getFontMetrics().stringWidth(readText);
        g.drawString(readText, pad + 12 + actBtnW + 12 + (actBtnW - rtw) / 2, actY + 26);

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Active Target: ", pad + 14, actY + actBtnH + 22);
        drawFlag(g, "PT", pad + 95, actY + actBtnH + 10, 18, 12);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString("CT1BOH @ 14.074.000 MHz (FT8 EU) -> FT-857D VFO-A Locked", pad + 120, actY + actBtnH + 22);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // 2. FT-857D Dedicated CAT Protocol Serial Monitor
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT857CatProtocolMonitor(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "FT-857D CAT Serial Monitor", true, true);

        int pad = 16;
        int curY = 94;

        int hdrH = 78;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), hdrH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), hdrH, 8, 8);

        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 16, curY + 16, 10, 10);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("SERIAL CAT: /dev/ttyUSB0 (CP2102 / CH340)", pad + 34, curY + 26);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("9600 BAUD | 8 DATA BITS | 2 STOP BITS | NO PARITY", pad + 16, curY + 48);

        g.setColor(COLOR_TEXT_GREY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString("PROTOCOL: 5-BYTE BINARY YAESU | RTS/DTR: HANDSHAKE", pad + 16, curY + 66);

        curY += hdrH + 12;

        int termH = HEIGHT - curY - 96;
        g.setColor(new Color(0x0C, 0x0E, 0x0A));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), termH, 8, 8);
        g.setColor(new Color(0x33, 0x2A, 0x18));
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), termH, 8, 8);

        g.setColor(new Color(0x1E, 0x18, 0x10));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 28, 8, 8);
        g.fillRect(pad, curY + 16, WIDTH - (pad * 2), 12);
        g.setColor(COLOR_AMBER);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString("FT-857 5-BYTE PACKET STREAM (HEX OPCODE)", pad + 12, curY + 18);

        g.setColor(COLOR_TEXT_MUTED);
        g.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g.drawString("LOGGING: ACTIVE", WIDTH - pad - 120, curY + 18);

        String[][] packets = {
            {"12:35:00.100", "TX", "01 40 74 00 01", "Opcode 0x01: Set Freq (14.074.000 Hz)"},
            {"12:35:00.115", "RX", "00",             "Ack: VFO-A Frequency Locked"},
            {"12:35:00.130", "TX", "01 00 00 00 07", "Opcode 0x07: Set Mode USB (0x01)"},
            {"12:35:00.145", "RX", "00",             "Ack: Operating Mode USB Confirmed"},
            {"12:35:00.160", "TX", "00 00 00 00 03", "Opcode 0x03: Read Freq & Mode Status"},
            {"12:35:00.180", "RX", "01 40 74 00 01", "Returns 14.074.000 Hz, Mode USB"},
            {"12:35:00.200", "TX", "00 00 00 00 E7", "Opcode 0xE7: Read RX S-Meter"},
            {"12:35:00.215", "RX", "09",             "Signal Level S9 (+10dB)"},
            {"12:35:01.300", "TX", "02 10 74 00 01", "Opcode 0x01: QSY to 15m (21.074 MHz)"},
            {"12:35:01.320", "RX", "00",             "Ack: 15m Synthesizer Locked"},
            {"12:35:02.000", "TX", "00 00 00 00 08", "Opcode 0x08: PTT ON (Transmitter Keyed)"},
            {"12:35:02.018", "RX", "00",             "Ack: Transceiver in TX Mode (100W)"},
            {"12:35:04.500", "TX", "00 00 00 00 88", "Opcode 0x88: PTT OFF (Return to RX)"},
            {"12:35:04.515", "RX", "00",             "Ack: Transceiver in RX Mode"},
            {"12:35:05.100", "TX", "00 00 00 00 E7", "Opcode 0xE7: Read RX S-Meter"},
            {"12:35:05.115", "RX", "08",             "Signal Level S8"}
        };

        int lineY = curY + 46;
        int rowStep = 18;
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));

        for (String[] p : packets) {
            if (lineY + rowStep > curY + termH - 8) break;

            g.setColor(COLOR_TEXT_MUTED);
            g.drawString(p[0], pad + 10, lineY);

            boolean isTx = p[1].equals("TX");
            g.setColor(isTx ? COLOR_BLUE : COLOR_AMBER);
            g.drawString("[" + p[1] + "]", pad + 106, lineY);

            g.setColor(COLOR_TEXT_WHITE);
            g.drawString(p[2], pad + 140, lineY);

            g.setColor(new Color(0x90, 0x80, 0x60));
            g.drawString("// " + p[3], pad + 270, lineY);

            lineY += rowStep;
        }

        int botY = HEIGHT - 80;
        int btnW = (WIDTH - pad * 2 - 12) / 2;
        drawTealButton(g, pad, botY, btnW, 44, "Send 5-Byte Opcode", 13);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + btnW + 12, botY, btnW, 44, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + btnW + 12, botY, btnW, 44, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String exp = "Export Trace (.log)";
        int ew = g.getFontMetrics().stringWidth(exp);
        g.drawString(exp, pad + btnW + 12 + (btnW - ew) / 2, botY + 27);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // 3. FT-857D COM Port Configuration (9600, 8N2)
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT857ComPortConfig(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "Open Radio - Yaesu FT-857D", false, true);

        int pad = 20;
        int curY = 96;

        String[][] fields = {
            {"Baud Rate", "9600"},
            {"Data Bits", "8"},
            {"Stop Bits", "2"},
            {"Parity", "None"},
            {"Flow Control", "None"},
            {"Select Radio", "Yaesu FT-857D"},
            {"USB Device", "CP2102 USB to UART Bridge (/dev/bus/usb/001/003)"}
        };

        for (String[] f : fields) {
            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(f[0], pad, curY + 16);

            int spinY = curY + 24;
            int spinH = 40;
            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, spinY, WIDTH - (pad * 2), spinH, 6, 6);
            g.setColor(COLOR_CARD_BORDER);
            g.drawRoundRect(pad, spinY, WIDTH - (pad * 2), spinH, 6, 6);

            g.setColor(f[0].equals("Select Radio") ? COLOR_AMBER : COLOR_TEAL);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(f[1], pad + 14, spinY + 25);

            int arrX = WIDTH - pad - 20;
            int arrY = spinY + 16;
            g.setColor(COLOR_TEXT_GREY);
            Polygon p = new Polygon();
            p.addPoint(arrX, arrY);
            p.addPoint(arrX + 10, arrY);
            p.addPoint(arrX + 5, arrY + 8);
            g.fillPolygon(p);

            curY += 72;
        }

        drawTealButton(g, pad, HEIGHT - 130, WIDTH - (pad * 2), 44, "Refresh Device List", 15);

        g.setColor(COLOR_TEAL);
        g.fillRoundRect(pad, HEIGHT - 76, WIDTH - (pad * 2), 48, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        String ap = "Connect FT-857D";
        int apw = g.getFontMetrics().stringWidth(ap);
        g.drawString(ap, pad + (WIDTH - (pad * 2) - apw) / 2, HEIGHT - 76 + 29);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // 4. FT-857D DX Cluster Live QSY & VFO Sync
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT857DxClusterQsy(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQMiau - Yaesu FT-857D Live", true, false);

        int pad = 16;
        int curY = 94;

        int rigH = 50;
        g.setColor(new Color(0x1F, 0x1A, 0x0E));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), rigH, 8, 8);
        g.setColor(COLOR_AMBER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), rigH, 8, 8);

        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 14, curY + 18, 12, 12);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("RIG: YAESU FT-857D", pad + 34, curY + 22);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.drawString("14.074.000 MHz (USB-DATA)", pad + 34, curY + 40);

        int syncW = 90;
        int syncH = 24;
        int syncX = WIDTH - pad - syncW - 12;
        g.setColor(new Color(0x00, 0xE6, 0x76, 40));
        g.fillRoundRect(syncX, curY + 13, syncW, syncH, 12, 12);
        g.setColor(COLOR_GREEN);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString("VFO-A SYNCED", syncX + 10, curY + 29);

        curY += rigH + 12;

        int gap = 12;
        int btnW = (WIDTH - (pad * 2) - gap) / 2;
        int btnH = 42;
        drawTealButton(g, pad, curY, btnW, btnH, "Filter Band", 14);
        drawTealButton(g, pad + btnW + gap, curY, btnW, btnH, "View Log", 14);

        curY += btnH + 14;

        String[][] spots = {
            {"14074.0", "PT", "CT1BOH", "IN51re FT8 +03dB Strong EU", "12:35:12", "TUNED"},
            {"21074.0", "US", "W1AW", "FN31pr ARRL HQ Special 599", "12:34:40", "TAP TO QSY"},
            {"28074.0", "JP", "JA1ABC", "PM95 Tokyo FT8 -08dB JA net", "12:33:55", "TAP TO QSY"},
            {"7074.0", "DE", "DL1XYZ", "JO43 Munich FT8 +01dB EU", "12:33:10", "TAP TO QSY"},
            {"14205.0", "GB", "M0XYZ", "IO91 London USB 59 loud", "12:32:28", "TAP TO QSY"},
            {"21285.0", "BR", "PY2AA", "GG66 Sao Paulo South Am 59", "12:31:45", "TAP TO QSY"},
            {"144200.0", "ES", "EA7K", "IM76 2m USB 599 CQ DX", "12:30:15", "TAP TO QSY"}
        };

        for (int i = 0; i < spots.length; i++) {
            String[] s = spots[i];
            int itemH = 68;
            boolean isTuned = s[5].equals("TUNED");

            g.setColor(COLOR_SURFACE);
            g.fillRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);

            if (isTuned) {
                g.setColor(COLOR_AMBER);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);
                g.setStroke(new BasicStroke(1f));
            } else {
                g.setColor(COLOR_CARD_BORDER);
                g.drawRoundRect(pad, curY, WIDTH - (pad * 2), itemH, 6, 6);
            }

            drawFlag(g, s[1], pad + 12, curY + 12, 26, 17);

            g.setColor(COLOR_TEXT_WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.drawString(s[2], pad + 46, curY + 26);

            g.setColor(isTuned ? COLOR_AMBER : COLOR_TEAL);
            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            FontMetrics fm = g.getFontMetrics();
            int freqW = fm.stringWidth(s[0]);
            g.drawString(s[0], WIDTH - pad - 12 - freqW, curY + 26);

            g.setColor(COLOR_TEXT_GREY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(s[3], pad + 12, curY + 50);

            if (isTuned) {
                int pillW = 92;
                int pillH = 18;
                int pillX = WIDTH - pad - 12 - pillW;
                int pillY = curY + 40;
                g.setColor(new Color(0xFF, 0xB3, 0x00, 40));
                g.fillRoundRect(pillX, pillY, pillW, pillH, 4, 4);
                g.setColor(COLOR_AMBER);
                g.setFont(new Font("SansSerif", Font.BOLD, 9));
                g.drawString("TUNED ON FT-857", pillX + 6, pillY + 13);
            } else {
                g.setColor(COLOR_TEXT_MUTED);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                int tw = g.getFontMetrics().stringWidth(s[4]);
                g.drawString(s[4], WIDTH - pad - 12 - tw, curY + 52);
            }

            curY += itemH + 8;
            if (curY + itemH > HEIGHT - 30) break;
        }

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // -------------------------------------------------------------
    // 5. FT-857D CQ Mode & Auto-Tune
    // -------------------------------------------------------------
    private static void renderFrameYaesuFT857CqMode(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics(img);
        g.setColor(COLOR_BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStatusBar(g);
        drawAppBar(g, "CQ Mode - Yaesu FT-857D", false, true);

        int pad = 20;
        int curY = 96;

        int banH = 46;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), banH, 6, 6);
        g.setColor(COLOR_AMBER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), banH, 6, 6);

        g.setColor(COLOR_GREEN);
        g.fillOval(pad + 14, curY + 17, 12, 12);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("AUTO-TUNE ACTIVE: YAESU FT-857D", pad + 36, curY + 24);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.drawString("CAT Control: 5-Byte Binary VFO & PTT Automated via USB", pad + 36, curY + 38);

        curY += banH + 16;

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

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Freq", pad, curY + 18);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + 80, curY, inputW, 36, 6, 6);
        g.setColor(COLOR_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("14074.0", pad + 94, curY + 24);

        curY += 46;

        int spinW = (WIDTH - (pad * 2) - 16) / 2;
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Receive S Number", pad, curY + 12);
        g.drawString("Send S Number", pad + spinW + 16, curY + 12);

        curY += 18;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("599", pad + 16, curY + 25);

        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad + spinW + 16, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad + spinW + 16, curY, spinW, 38, 6, 6);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("599", pad + spinW + 32, curY + 25);

        curY += 56;

        int rigBoxH = 110;
        g.setColor(COLOR_SURFACE);
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), rigBoxH, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), rigBoxH, 8, 8);

        g.setColor(COLOR_AMBER);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("YAESU FT-857D LIVE TELEMETRY", pad + 14, curY + 22);

        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("VFO-A: 14.074.000 Hz  |  MODE: USB-DATA", pad + 14, curY + 44);
        g.drawString("POWER: 100 Watts (HF) |  SWR:  1.1:1", pad + 14, curY + 64);
        g.drawString("S-METER: S9 (+10dB)   |  CAT:  ACK 0x00 OK", pad + 14, curY + 84);

        curY += rigBoxH + 18;

        drawTealButton(g, pad, curY, WIDTH - (pad * 2), 48, "⚡ Tune FT-857D to Spot & Call CQ", 15);
        curY += 58;

        g.setColor(new Color(0x30, 0x30, 0x30));
        g.fillRoundRect(pad, curY, WIDTH - (pad * 2), 48, 8, 8);
        g.setColor(COLOR_CARD_BORDER);
        g.drawRoundRect(pad, curY, WIDTH - (pad * 2), 48, 8, 8);
        g.setColor(COLOR_TEXT_WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        String logQso = "📝 Save QSO to Logbook";
        int lqw = g.getFontMetrics().stringWidth(logQso);
        g.drawString(logQso, pad + (WIDTH - (pad * 2) - lqw) / 2, curY + 30);

        g.dispose();
        ImageIO.write(img, "png", file);
    }
}
