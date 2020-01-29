package com.lumyjuwon.richwysiwygeditor;

import android.content.Context;
import android.util.AttributeSet;

public class CheckedImageButton extends androidx.appcompat.widget.AppCompatImageButton {

    private boolean isChecked = false;
    private int baseColor;      // цвет отключенного состояния
    private int highlightColor; // цвет включенного состояния

    public CheckedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStateColors(int baseColor, int highlightColor) {
        this.baseColor = baseColor;
        this.highlightColor = highlightColor;
    }

    public void switchCheckedState(){
        setCheckedState(!isChecked);
    }

    public void switchCheckedState(int color){
        setCheckedState(!isChecked, color);
    }

    public void setCheckedState(boolean isChecked){
        this.isChecked = isChecked;
        int color = (isChecked) ? highlightColor : baseColor;
        setColorFilter(color);
    }

    /**
     * Включение/отключение кнопки с указанием цвета состояния.
     * @param isChecked
     * @param color
     */
    public void setCheckedState(boolean isChecked, int color){
        this.isChecked = isChecked;
        // если нужно включить кнопку, но переданный цвет состояния указан как цвет ее отключения,
        // то игнорируем переданный цвет и принудительно устанавливаем цвет включения кнопки
        if (isChecked && color == baseColor && baseColor != highlightColor) {
            color = highlightColor;
        }
        setColorFilter(color);
    }

    public boolean isChecked(){
        return this.isChecked;
    }

}
