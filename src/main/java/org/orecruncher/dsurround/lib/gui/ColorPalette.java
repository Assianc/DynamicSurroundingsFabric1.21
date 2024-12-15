package org.orecruncher.dsurround.lib.gui;

public final class ColorPalette {
    private ColorPalette() {
    }

    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF0000;
    public static final int GREEN = 0xFF00FF00;
    public static final int BLUE = 0xFF0000FF;
    public static final int MC_YELLOW = 0xFFFFFF00;
    public static final int MC_GREEN = 0xFF00FF00;
    public static final int DARK_VIOLET = 0xFF9400D3;
    public static final int SUN_GLOW = 0xFFFFA500;
    public static final int FRESH_AIR = 0xFF87CEEB;
    public static final int CORN_FLOWER_BLUE = 0xFF6495ED;

    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int getBlue(int color) {
        return color & 0xFF;
    }

    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }

    public static int toRGB(int red, int green, int blue) {
        return 0xFF000000 | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    public static int toRGBA(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
}