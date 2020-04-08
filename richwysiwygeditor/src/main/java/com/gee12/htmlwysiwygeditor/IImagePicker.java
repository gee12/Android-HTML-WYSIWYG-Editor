package com.gee12.htmlwysiwygeditor;

public interface IImagePicker {

    /**
     * Запуск активности для выбора файлов изображений.
     */
    void startPicker();

    /**
     * Захват изображения с камеры.
     */
    void startCamera();
}
