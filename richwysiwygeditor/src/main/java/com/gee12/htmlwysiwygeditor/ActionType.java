package com.gee12.htmlwysiwygeditor;

import android.text.TextUtils;

public enum ActionType {
    NONE,
    UNDO,
    REDO,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    SELECTION_MODE,
    SELECT_WORD,
    SELECT_ALL,
    COPY,
    CUT,
    PASTE,
    PASTE_TEXT,
    FORWARD_DEL,
    TEXT_SIZE("FONT-SIZE"),
    FONT_FAMILY,
    HEADERING,
//    HEADERING,
    BOLD,
    ITALIC,
//    SUBSCRIPT,
//    SUPERSCRIPT,
    UNDERLINE,
    STRIKETHROUGH,
    TEXT_COLOR("COLOR"),
    BACKGROUND_COLOR("BACKGROUND-COLOR"),
//    ORDERED_LIST,
//    UNORDERED_LIST,
    CODE("PRE"),
    QUOTE("BLOCKQUOTE"),

    TEXT_ALIGN("JUSTIFY"),
//    JUSTIFY_CENTER,
//    JUSTIFY_FULL,
//    JUSTUFY_LEFT,
//    JUSTIFY_RIGHT,
    UNORDERED_LIST("INSERTUNORDEREDLIST"),
    ORDERED_LIST("INSERTORDEREDLIST"),
    INDENT,
    OUTDENT,

    INSERT_LINE,
    INSERT_LINK("CREATELINK"),
//    REMOVE_LINK,
    INSERT_IMAGE,
    INSERT_VIDEO(false),
    INSERT_TABLE(false),
    INSERT_FORMULA(false),

    REMOVE_FORMAT;

    boolean mIsFree = true;
    String mJSName = name();

    ActionType() { }

    ActionType(String mJSName) {
        this.mJSName = mJSName;
    }

    ActionType(boolean isFree) {
        this.mIsFree = isFree;
    }

    ActionType(boolean mIsFree, String mJSName) {
        this.mIsFree = mIsFree;
        this.mJSName = mJSName;
    }

    public boolean isFree() {
        return mIsFree;
    }

    public String getJSName() {
        return mJSName;
    }

    /**
     *
     * @param typeString
     * @return
     */
    public static ActionType parse(String typeString) {
        if (TextUtils.isEmpty(typeString))
            return ActionType.NONE;
        String value = typeString.toUpperCase();
        for (ActionType type : ActionType.values()) {
            if (value.equals(type.getJSName())) {
                return type;
            }
        }
        return ActionType.NONE;
    }
}
