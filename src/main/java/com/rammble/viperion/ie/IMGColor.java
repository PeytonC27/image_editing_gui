package com.rammble.viperion.ie;

import java.awt.*;

/**
 * This is a special class that assists the ViperionImageEditor by storing and
 * calculating important data relating to pixels and RGB
 */
public class IMGColor {

    public int red;
    public int green;
    public int blue;

    public IMGColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public IMGColor(int rgb) {
        colorToRGB(rgb);
    }

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
     * Gets the rgb of this IMGColor
     *
     * @return a color as an int
     */
    public int rgb() {
        return (new Color(red, green, blue)).getRGB();
    }

    /**
     * <p>Calculates the luma value of this ViperionColor</p>
     *
     * <a href="https://stackoverflow.com/questions/596216/formula-to-determine-perceived-brightness-of-rgb-color">Luma Value Calculation</a>
     * <a href="https://en.wikipedia.org/wiki/Luma_(video)">Luma Value Definition</a>
     *
     * @return the luma value based on the provided values
     */
    public double lumaValue() {
        return (0.2126 * red + 0.7152 * green + 0.0722 * blue);
    }

    /**
     * Inverts the pixels of this ViperionColor
     */
    public void invert() {
        red = 255 - red;
        green = 255 - green;
        blue = 255 - blue;
    }

    /**
     * Converts the colors to black and white
     */
    public void blackAndWhite() {
        int luma = (int) lumaValue();
        red = luma;
        green = luma;
        blue = luma;
    }

    /**
     * Checks if two ViperionColors are close to one another based on their colors
     *
     * @param other          the other ViperionColor
     * @param closenessCheck the distance between each in terms of color (should
     *                       range from 0 to 255)
     * @return whether the two colors are similar
     */
    public boolean isSimilarTo(IMGColor other, double closenessCheck) {
        // convert them to longs and give them more readable names
        long red1 = red;
        long green1 = green;
        long blue1 = blue;

        long red2 = other.red;
        long green2 = other.green;
        long blue2 = other.blue;

        // average of the reds
        long redMean = (red1 + red2) / 2;

        // difference of all colors
        long red = red1 - red2;
        long green = green1 - green2;
        long blue = blue1 - blue2;

        // theoretical equation (https://www.compuphase.com/cmetric.htm) (low-cost)
        long x = 2 + (redMean / 256);
        long y = 2 + ((255 - redMean) / 256);
        double result = Math.sqrt(x * red * red + 4 * green * green + y * blue * blue);
        return result < closenessCheck;
    }

    /**
     * Extracts the components of an RGB integer and stores their values into its
     * fields
     *
     * @param color the color to convert to rgb
     */
    private void colorToRGB(int color) {
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);

        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
