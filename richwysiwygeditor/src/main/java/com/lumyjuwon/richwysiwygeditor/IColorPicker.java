package com.lumyjuwon.richwysiwygeditor;

public interface IColorPicker {

    /**
     * Запуск диалога выбора цвета.
     */
    void onPickColor(int curColor);

    /**
     * Получение массива сохраненных цветов.
     * @return
     */
    int[] getSavedColors();

    /**
     * Удаление цвета из массива сохраненных.
     * @param index
     * @param color
     */
    void removeSavedColor(int index, int color);
}
