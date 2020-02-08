package com.gee12.htmlwysiwygeditor;

import android.text.TextUtils;

import androidx.annotation.IdRes;

import com.lumyjuwon.richwysiwygeditor.R;

public enum ActionType {
    NONE,
    UNDO,
    REDO,
    TEXT_SIZE(R.id.button_text_size),
    FONT_FAMILY,
    HEADERING,
//    HEADERING(R.id.button_text_headering),
    BOLD(R.id.button_text_bold),
    ITALIC(R.id.button_text_italic),
//    SUBSCRIPT((R.id.button_text_subscript),
//    SUPERSCRIPT(R.id.button_text_superscript),
    UNDERLINE(R.id.button_text_underLine),
    STRIKETHROUGH(R.id.button_text_strike),
    TEXT_COLOR(R.id.button_text_color),
    BACKGROUND_COLOR(R.id.button_background_color),
//    ORDERED_LIST(R.id.button_number_list),
//    UNORDERED_LIST(R.id.button_bullet_list),
    CODE,
    QUOTE,

    TEXT_ALIGN(R.id.button_text_align),
//    JUSTIFY_CENTER(R.id.button_text_align),
//    JUSTIFY_FULL(R.id.button_text_align),
//    JUSTUFY_LEFT(R.id.button_text_align),
//    JUSTIFY_RIGHT(R.id.button_text_align),
    UNORDERED_LIST,
    ORDERED_LIST,
    INDENT,
    OUTDENT,

    INSERT_LINE(R.id.button_insert_line),
    INSERT_LINK(R.id.button_insert_link),
//    REMOVE_LINK(R.id.button_remove_link),
    INSERT_IMAGE(false),
    INSERT_VIDEO(false),
    INSERT_TABLE(false),
    INSERT_FORMULA(false),

    REMOVE_FORMAT(R.id.button_insert_video);

    int mButtonId;
    boolean mIsFree = true;

    ActionType() { }

    ActionType(boolean isFree) {
        this.mIsFree = isFree;
    }

    ActionType(@IdRes int buttonId) {
        this.mButtonId = buttonId;
    }

    public int getmButtonId() {
        return this.mButtonId;
    }

    public boolean isFree() {
        return mIsFree;
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
            if (value.equals(type.name()))
                return type;
        }
        return ActionType.NONE;
    }
}
