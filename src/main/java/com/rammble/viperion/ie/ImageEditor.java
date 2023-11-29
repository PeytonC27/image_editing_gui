package com.rammble.viperion.ie;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageEditor {

    BufferedImage image;
    String saveLocation;

    int imageWidth;
    int imageHeight;

    public ImageEditor(String imageFilePath, String saveLocation) throws ITAParseException {
        try {
            File imageFile = new File(imageFilePath);
            this.image = ImageIO.read(imageFile);
            this.saveLocation = saveLocation;

            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
        } catch (Exception e) {
            throw new ITAParseException("One of the provided file paths could not be read.");
        }
    }

    /**
     * Creates a new image and saves it at the specified location. If an image
     * already exists at that location, it will be overriden. You can specify some
     * save options as well, such as making the image black and white or inverting
     * the colors
     *
     * @param saveOptions the options for saving the image
     * @param chosenValue the value to be utilized in options requiring specific ratios, sizes, etc
     */
    public void saveNewImage(ImageSaveSettings saveOptions, int chosenValue) {
        if (saveOptions == ImageSaveSettings.NORMAL || saveOptions == ImageSaveSettings.INVERT_COLORS || saveOptions == ImageSaveSettings.BLACK_AND_WHITE)
            applyFilter(saveOptions);
        else if (saveOptions == ImageSaveSettings.COMPRESS)
            compressImage(chosenValue);
        else if (saveOptions == ImageSaveSettings.PIXELATE)
            pixelate(chosenValue);


        System.out.println("Image was saved.");
    }

    private void applyFilter(ImageSaveSettings saveOptions) {
        int width = imageWidth;
        int height = imageHeight;

        // making a new image
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[][] pixels = new int[width][height];

        // setting each pixel in the file
        for (int y = 0; y < pixels[0].length; y++) {
            for (int x = 0; x < pixels.length; x++) {

                IMGColor color = new IMGColor(image.getRGB(x, y));

                // change the colorRGB based on the specified options and get the rgb
                modifyColorFromOption(saveOptions, color);

                newImage.setRGB(x, y, color.rgb());
            }
        }

        saveImage(newImage, saveLocation);
    }

    /**
     * Replaces all instances of the color to replace with the new color
     * <p>
     * WIP
     *
     * @param toReplace
     * @param newColor
     */
    public void replaceColor(Color toReplace, Color newColor) {
        int newWidth = imageWidth;
        int newHeight = imageHeight;

        // making a new image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        int[][] pixels = new int[newWidth][newHeight];

        // setting each pixel in the file
        for (int y = 0; y < pixels[0].length; y++) {
            for (int x = 0; x < pixels.length; x++) {
                IMGColor currentImageColor = new IMGColor(image.getRGB(x, y));
                IMGColor colorComparison = new IMGColor(toReplace.getRGB());

                boolean colorsAreClose = currentImageColor.isSimilarTo(colorComparison, 200);

                if (colorsAreClose) newImage.setRGB(x, y, newColor.getRGB());
                else newImage.setRGB(x, y, currentImageColor.rgb());
            }
        }

        saveImage(newImage, saveLocation);

        System.out.println("Colors were replaced.");
    }

    /**
     * Compresses the image and saves it, the compression level is based on the
     * compressionMultiplier
     *
     * @param compressionMultiplier
     */
    public void compressImage(int compressionMultiplier) {
        if (compressionMultiplier == 0) return;

        // calculate the lost pixels
        int pixelWidthLoss = imageWidth % compressionMultiplier;
        int pixelHeightLoss = imageHeight % compressionMultiplier;

        // calculate the width/height to step through when compresssing
        int tempWidth = imageWidth - pixelWidthLoss;
        int tempHeight = imageHeight - pixelHeightLoss;

        int newWidth = (tempWidth - pixelWidthLoss + compressionMultiplier) / compressionMultiplier;
        int newHeight = (tempHeight - pixelHeightLoss + compressionMultiplier) / compressionMultiplier;
        int newImageX = 0, newImageY = 0;

        // making a new image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // setting each pixel in the file
        for (int y = 0; y < tempHeight; y += compressionMultiplier) {
            for (int x = 0; x < tempWidth; x += compressionMultiplier) {

                int averageColor = averageRGB(x, y, compressionMultiplier);
                newImage.setRGB(newImageX, newImageY, averageColor);
                newImageX++;
            }
            newImageY++;
            newImageX = 0;
        }

        saveImage(newImage, saveLocation);

        System.out.println("Image was compressed.");
        System.out.println("The image lost " + pixelWidthLoss + " pixels on the right, and " + pixelHeightLoss + " pixels on the bottom");
    }

    /**
     * @param pixelSize the length of each new, larger pixel (in pixels)
     */
    public void pixelate(int pixelSize) {
        if (pixelSize == 0) return;

        int pixelWidthLoss = imageWidth % pixelSize;
        int pixelHeightLoss = imageHeight % pixelSize;

        int newWidth = imageWidth - pixelWidthLoss;
        int newHeight = imageHeight - pixelHeightLoss;

        // making a new image
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        // setting each pixel in the file
        for (int y = 0; y < newHeight; y += pixelSize) {
            for (int x = 0; x < newWidth; x += pixelSize) {
                int averageColor = averageRGB(x, y, pixelSize);
                paintSquare(newImage, averageColor, x, y, pixelSize);
            }
        }

        saveImage(newImage, saveLocation);

        System.out.println("Image was pixelated.");
        System.out.println("The image lost " + pixelWidthLoss + " pixels on the right, and " + pixelHeightLoss + " pixels on the bottom");
    }

    /**
     * Calculates the average RGB value of a square, where (x,y) is the top left,
     * and s is the side length
     *
     * @param x
     * @param y
     * @return
     */
    private int averageRGB(int x, int y, int s) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        IMGColor color;
        for (int i = x; i < x + s && i < imageWidth; i++) {
            for (int j = y; j < y + s && j < imageHeight; j++) {
                color = new IMGColor(image.getRGB(i, j));

                redSum += color.red;
                greenSum += color.green;
                blueSum += color.blue;
            }
        }

        int count = s * s;
        return IMGColor.rgb(redSum / count, greenSum / count, blueSum / count);
    }

    /**
     * Paints all pixels in a square the same color
     *
     * @param image      the image to draw on
     * @param rgb        the color to use
     * @param x          the top left horizontal coordinate
     * @param y          the top left vertical coordinate
     * @param sideLength the length of the drawn square
     */
    private void paintSquare(BufferedImage image, int rgb, int x, int y, int sideLength) {
        for (int i = x; i < x + sideLength && i < imageWidth; i++) {
            for (int j = y; j < y + sideLength && j < imageHeight; j++) {
                image.setRGB(i, j, rgb);
            }
        }
    }

    // ==================== IMAGE SAVING METHODS ==================== //

    /**
     * Calcualtes the new color based on the provided color and the color-changing
     * option
     *
     * @param option
     * @param color
     * @return
     */
    private void modifyColorFromOption(ImageSaveSettings option, IMGColor color) {
        switch (option) {
            case BLACK_AND_WHITE:
                color.blackAndWhite();
                break;
            case INVERT_COLORS:
                color.invert();
                break;
            default:
        }
    }

    // ==================== HELPERS ==================== //

    /**
     * Saves a new BufferedImage at the specified save location
     *
     * @param newImage
     * @param saveLocation
     */
    private void saveImage(BufferedImage newImage, String saveLocation) {
        try {
            File f = new File(saveLocation);

            ImageIO.write(newImage, "png", f);
        } catch (Exception e) {
            System.out.println("Could not save file.");
        }
    }

    /**
     * With the option, determine if a numerical parameter is needed in order to go through with the ImageEditor action
     * @param option
     * @return
     */
    public boolean needsNumericalOption(ImageSaveSettings option) {
        return option == ImageSaveSettings.COMPRESS || option == ImageSaveSettings.PIXELATE;
    }

    private static class ITAParseException extends Exception {
        public ITAParseException(String message) {
            super(message);
        }
    }
}
