package com.lumyjuwon.richwysiwygeditor;

import android.content.Context;
import android.util.AttributeSet;

public class EditorActionButton extends androidx.appcompat.widget.AppCompatImageButton {

    private String action;
    private int imageId;
    private boolean isCheckable = true;
    private boolean isChecked = false;
    private boolean isPopup;

    private int baseColor;      // цвет отключенного состояния
    private int checkedColor;   // цвет включенного состояния
    private int disabledColor;  // цвет неактивного состояния

    public EditorActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStateColors(int baseColor, int highlightColor) {
        this.baseColor = baseColor;
        this.checkedColor = highlightColor;
    }

    public void switchCheckedState(){
        setCheckedState(!isChecked);
    }

    public void switchCheckedState(int color){
        setCheckedState(!isChecked, color);
    }

    public void setCheckedState(boolean isChecked){
        this.isChecked = isChecked;
        int color = (isChecked) ? checkedColor : baseColor;
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
        if (isChecked && color == baseColor && baseColor != checkedColor) {
            color = checkedColor;
        }
        setColorFilter(color);
    }

    public boolean isChecked(){
        return this.isChecked;
    }

}
