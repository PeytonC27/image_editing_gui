package com.rammble.viperion.ie;

public enum ImageSaveSettings {
    NORMAL(0),
    BLACK_AND_WHITE(1),
    INVERT_COLORS(2),
    COMPRESS(-1),
    PIXELATE(-2),
    POINTILLISM(-3);

    private int value;

    private ImageSaveSettings(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
