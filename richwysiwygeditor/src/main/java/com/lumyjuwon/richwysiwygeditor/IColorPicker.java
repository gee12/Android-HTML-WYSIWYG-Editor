package com.lumyjuwon.richwysiwygeditor;

public interface IColorPicker {

    /**
     * Запуск диалога выбора цвета.
     */
    void onPickColor();

    /**
     * Получение массива выбранных ранее цветов.
     * @return
     */
    int[] getSavedColors();
}
