package com.gee12.htmlwysiwygeditor;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.lumyjuwon.richwysiwygeditor.R;

public class ActionButton extends androidx.appcompat.widget.AppCompatImageButton {

    public static final int RES_COLOR_BASE = R.color.black;
    public static final int RES_COLOR_CHECKED = R.color.sky_blue;
    public static final int RES_COLOR_DISABLED = R.color.dark_gray;
    private ActionType type;
    private int imageId;
    private boolean isCheckable;
    private boolean isChecked;
    private boolean isPopup;

    private int mBaseColor;      // цвет отключенного состояния
    private int mCheckedColor;   // цвет включенного состояния
    private int mDisabledColor;  // цвет неактивного состояния

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(ActionType.NONE, 0, false, false);
    }

    public ActionButton(Context context, ActionType type, int imageId, boolean isCheckable, boolean isPopup) {
        super(context);
        init(type, imageId, isCheckable, isPopup);
    }

    public void init(ActionType type, int imageId, boolean isCheckable, boolean isPopup) {
        this.type = type;
        this.imageId = imageId;
        this.isCheckable = isCheckable;
        this.isPopup = isPopup;
        this.mBaseColor = ContextCompat.getColor(getContext().getApplicationContext(), RES_COLOR_BASE);
        this.mCheckedColor = ContextCompat.getColor(getContext().getApplicationContext(), RES_COLOR_CHECKED);
        this.mDisabledColor = ContextCompat.getColor(getContext().getApplicationContext(), RES_COLOR_DISABLED);
    }

    public void switchCheckedState(){
        setCheckedState(!isChecked);
    }

    public void switchCheckedState(int color){
        setCheckedState(!isChecked, color);
    }

    public void setCheckedState(boolean isChecked){
        this.isChecked = isChecked;
//        int color = (isChecked) ? mCheckedColor : mBaseColor;
//        setColorFilter(color);
        updateColor();
    }

    /**
     * Включение/отключение кнопки с указанием цвета состояния.
     * @param isChecked
     * @param checkedColor
     */
    public void setCheckedState(boolean isChecked, int checkedColor){
        this.isChecked = isChecked;
        // если нужно включить кнопку, но переданный цвет состояния указан как цвет ее отключения,
        // то игнорируем переданный цвет и принудительно устанавливаем цвет включения кнопки
        if (isChecked && checkedColor == mBaseColor && mBaseColor != this.mCheckedColor) {
            checkedColor = this.mCheckedColor;
        }
        updateColor(checkedColor);
    }

    private void updateColor() {
        updateColor(-1);
    }

    private void updateColor(int checkedColor) {
        int color = mBaseColor;
        if (isEnabled()) {
            if (isChecked) {
                color = (checkedColor != -1) ? checkedColor : this.mCheckedColor;
            }
        } else {
            color = mDisabledColor;
        }
        setColorFilter(color);
    }

    public boolean isChecked(){
        return this.isChecked;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateColor();
    }

    public ActionType getType() {
        return type;
    }

    public boolean isCheckable() {
        return isCheckable;
    }

    public boolean isPopup() {
        return isPopup;
    }
}
