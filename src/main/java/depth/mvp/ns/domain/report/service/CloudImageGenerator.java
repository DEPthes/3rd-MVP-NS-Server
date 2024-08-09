package depth.mvp.ns.domain.report.service;

import depth.mvp.ns.domain.report.domain.WordCount;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

@Service
public class CloudImageGenerator {
    private static final int REJECT_COUNT = 100;
    private static final int LARGEST_FONT_SIZE = 160;
    private static final int FONT_STEP_SIZE = 5;
    private static final int MINIMUM_FONT_SIZE = 20;
    private static final int MINIMUM_WORD_COUNT = 2;
    public static final String FONT_FAMILY = "Helvetica";
    public static final String[] THEME = ColorCombinations.THEME1;

    private String fontFamily;
    private final int width;
    private final int height;
    private final int padding;
    private BufferedImage bi;
    private ColorCombinations colorTheme;
    private ArrayList<Shape> occupied = new ArrayList<>();

    public CloudImageGenerator() {
        this.width = 800;  // 기본 이미지의 가로 크기
        this.height = 600; // 기본 이미지의 세로 크기
        this.fontFamily = FONT_FAMILY;
        this.padding = 10; // 기본 이미지 내 패딩
    }

    public CloudImageGenerator(int width, int height, int padding) {
        this.width = width;
        this.height = height;
        this.fontFamily = FONT_FAMILY;
        this.padding = padding;
    }

    public BufferedImage generateImage(Iterable<WordCount> words, long seed) {
        Random rand = new Random(seed);
        bi = new BufferedImage(width + 2 * padding, height + 2 * padding, BufferedImage.TYPE_INT_ARGB);
        colorTheme = new ColorCombinations(THEME);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(colorTheme.background());
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.translate(padding, padding);

        int maxFontSize = LARGEST_FONT_SIZE;
        int minFontSize = MINIMUM_FONT_SIZE;

        // Find maximum count to normalize font sizes
        int maxCount = 0;
        for (WordCount wc : words) {
            if (wc.getCount() > maxCount) {
                maxCount = wc.getCount();
            }
        }


        for (WordCount wc : words) {
            int fontSize = minFontSize + (int) ((maxFontSize - minFontSize) * ((double) wc.getCount() / maxCount));
            Font font = new Font(fontFamily, Font.BOLD, fontSize);
            g.setFont(font);
            FontRenderContext frc = g.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, wc.getWord());
            Shape glyph = gv.getOutline();
            Rectangle bounds = glyph.getBounds();


            int maxAttempts = 100;
            boolean fitted = false;
            for (int i = 0; i < maxAttempts; i++) {
                AffineTransform at = new AffineTransform();
                int x = rand.nextInt(Math.max(1, width - bounds.width));
                int y = rand.nextInt(Math.max(1, height - bounds.height)) + bounds.height;
                at.translate(x, y);
                Shape s = at.createTransformedShape(glyph);
                if (!collision(s.getBounds()) && s.getBounds().getMaxX() <= width && s.getBounds().getMaxY() <= height) {
                    fitted = true;
                    glyph = s;
                    break;
                }
            }


            if (fitted) {
                g.setColor(colorTheme.next());
                g.fill(glyph);
                occupied.add(glyph);
            }
        }

        g.dispose();
        return bi;
    }

    private boolean collision(Rectangle area) {
        for (Shape shape : occupied) {
            if (shape.getBounds().intersects(area)) {
                return true;
            }
        }
        return false;
    }

    public void setColorTheme(String[] colorCodes, Color background) {
        colorTheme = new ColorCombinations(colorCodes, background);
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
}