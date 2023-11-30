package com.rammble.viperion.ie;

import java.awt.*;

/**
 * This is a special class that assists the ViperionImageEditor by storing and
 * calculating important data relating to pixels and RGB
 */
public class ColorHelper {

    /**
     * Returns the provided color inputs as an rgb value
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static int rgb(int r, int g, int b) {
        return (new Color(r, g, b)).getRGB();
    }

    /**
     * Calculates the luma value of the passed in color
     *
     * @param color the rgb value of the color
     * @return the luma value
     */
    public static double lumaValue(int color) {
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);

        return (0.2126 * r + 0.7152 * g + 0.0722 * b);
    }

    /**
     * Inverts the pixels of the provided RGB
     * @param rgb the color to invert
     * @return the inverted rgb
     */
    public static int invert(int rgb) {
        int[] c = extractColors(rgb);
        c[0] = 255 - c[0];
        c[1] = 255 - c[1];
        c[2] = 255 - c[2];
        return rgb(c[0], c[1], c[2]);
    }

    /**
     * Converts the rgb to a grey-scaled version based on its luma value
     * @param rgb the color to grey-scale
     * @return the grey-scaled color
     */
    public static int blackAndWhite(int rgb) {
        int luma = (int) lumaValue(rgb);
        return rgb(luma, luma, luma);
    }

    /**
     * Gets the red value of a rgb value
     * @param rgb
     * @return
     */
    public static int getRed(int rgb) {
        return (rgb & 0x00ff0000) >> 16;
    }

    /**
     * Gets the green value of a rgb value
     * @param rgb
     * @return
     */
    public static int getGreen(int rgb) {
        return (rgb & 0x0000ff00) >> 8;
    }

    /**
     * Gets the blue value of a rgb value
     * @param rgb
     * @return
     */
    public static int getBlue(int rgb) {
        return (rgb & 0x000000ff);
    }

    /**
     * Extracts the color components of a given rgb. Index 0 is red, index 1 is green, index 2 is blue
     * @param rgb
     * @return
     */
    private static int[] extractColors(int rgb) {
        int red = (rgb & 0x00ff0000) >> 16;
        int green = (rgb & 0x0000ff00) >> 8;
        int blue = (rgb & 0x000000ff);
        return new int[] {red, green, blue};
    }
}
