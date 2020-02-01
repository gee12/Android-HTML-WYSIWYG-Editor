package com.gee12.htmlwysiwygeditor;

public enum ActionType {
    NONE,
    BOLD,
    ITALIC,
    SUBSCRIPT,
    SUPERSCRIPT,
    STRIKETHROUGH,
    UNDERLINE,
    H1,
    H2,
    H3,
    H4,
    H5,
    H6,
    ORDEREDLIST,
    UNORDEREDLIST,
    JUSTIFYCENTER,
    JUSTIFYFULL,
    JUSTUFYLEFT,
    JUSTIFYRIGHT,
    TEXT_COLOR,
    BACKGROUND_COLOR,

    INSERT_LINE,
    INSERT_LINK,
    REMOVE_LINK,
    INSERT_IMAGE,
    INSERT_VIDEO,

    BACKGROUND_COLOR_WHITE(255, 255, 255),
    BACKGROUND_COLOR_BLACK(0, 0, 0),
    BACKGROUND_COLOR_MAROON(128, 0, 0),
    BACKGROUND_COLOR_RED(255, 0, 0),
    BACKGROUND_COLOR_MAGENTA(255, 0, 255),
    BACKGROUND_COLOR_PINK(255, 153, 204),
    BACKGROUND_COLOR_ORANGE(255, 102, 0),
    BACKGROUND_COLOR_YELLOW(255, 255, 0),
    BACKGROUND_COLOR_LIME(0, 255, 0),
    BACKGROUND_COLOR_AQUA(0, 255, 255),
    BACKGROUND_COLOR_BLUE(0, 0, 255),
    BACKGROUND_COLOR_SKY_BLUE(0, 204, 255),
    BACKGROUND_COLOR_PALE_CYAN(204, 255, 255),
    BACKGROUND_COLOR_GREEN(0, 128, 0),
    FONT_COLOR_WHITE(255, 255, 255),
    FONT_COLOR_BLACK(0, 0, 0),
    FONT_COLOR_MAROON(128, 0, 0),
    FONT_COLOR_RED(255, 0, 0),
    FONT_COLOR_MAGENTA(255, 0, 255),
    FONT_COLOR_PINK(255, 153, 204),
    FONT_COLOR_ORANGE(255, 102, 0),
    FONT_COLOR_YELLOW(255, 255, 0),
    FONT_COLOR_LIME(0, 255, 0),
    FONT_COLOR_AQUA(0, 255, 255),
    FONT_COLOR_BLUE(0, 0, 255),
    FONT_COLOR_SKY_BLUE(0, 204, 255),
    FONT_COLOR_PALE_CYAN(204, 255, 255),
    FONT_COLOR_GREEN(0, 128, 0);


    private int r;
    private int g;
    private int b;

    ActionType(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    ActionType() {
        this(-1, -1, -1);
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
}
