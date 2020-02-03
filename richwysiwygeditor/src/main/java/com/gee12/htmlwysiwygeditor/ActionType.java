package com.gee12.htmlwysiwygeditor;

import androidx.annotation.IdRes;

import com.lumyjuwon.richwysiwygeditor.R;

public enum ActionType {
    NONE(0),
    TEXT_SIZE(R.id.button_text_size),
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
    INSERT_IMAGE(R.id.button_insert_image),
    INSERT_VIDEO(R.id.button_insert_video),

    REMOVE_FORMAT(R.id.button_insert_video);

    int mButtonId;

    ActionType() { }

    ActionType(@IdRes int buttonId) {
        this.mButtonId = buttonId;
    }

    public int getmButtonId() {
        return this.mButtonId;
    }

    /**
     *
     * @param typeString
     * @return
     */
    public static ActionType parse(String typeString) {
        for (ActionType type : ActionType.values()) {
            if (typeString.equals(type.name()))
                return type;
        }
        return ActionType.NONE;
    }
}
