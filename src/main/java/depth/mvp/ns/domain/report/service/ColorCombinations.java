package depth.mvp.ns.domain.report.service;

import java.awt.*;
import java.util.Random;

public class ColorCombinations {
    public static final String[] THEME1 = {
            "#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#33FFF6", "#A133FF"
    };

    private final Color[] colors;
    private final Color background;
    private int index = 0;

    public ColorCombinations(String[] hexColors) {
        this(hexColors, "#FFFFFF");
    }

    public ColorCombinations(String[] hexColors, String backgroundHex) {
        this.colors = new Color[hexColors.length];
        for (int i = 0; i < hexColors.length; i++) {
            this.colors[i] = Color.decode(hexColors[i]);
        }
        this.background = Color.decode(backgroundHex);
    }

    public ColorCombinations(String[] hexColors, Color background) {
        this.colors = new Color[hexColors.length];
        for (int i = 0; i < hexColors.length; i++) {
            this.colors[i] = Color.decode(hexColors[i]);
        }
        this.background = background;
    }

    public Color next() {
        Color color = colors[index];
        index = (index + 1) % colors.length;
        return color;
    }

    public Color background() {
        return background;
    }
}
