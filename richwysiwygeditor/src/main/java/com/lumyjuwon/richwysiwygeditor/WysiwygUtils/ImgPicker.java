package com.lumyjuwon.richwysiwygeditor.WysiwygUtils;

import android.app.Activity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.lumyjuwon.richwysiwygeditor.R;

public class ImgPicker {

    public interface IImgPicker {
        void startPicker();
        void startCamera();
    }

    /**
     * StartPicker image picker activity with request code
     * @param activity
     */
    public static void startPicker(Activity activity) {
        ImagePicker.create(activity)
                .limit(10)
                .toolbarFolderTitle(activity.getString(R.string.title_gallery))
                .toolbarDoneButtonText(activity.getString(R.string.confirm_selected_images))
                .showCamera(false)
                .folderMode(false)
                .includeVideo(false)
                .start();
    }

    /**
     * Start capture photo from camera with request code.
     * @param activity
     * @param newImagesFullDir
     */
    public static void startCamera(Activity activity, String newImagesFullDir, String newImagesDir) {
        ImagePicker.cameraOnly()
                .imageDirectory(newImagesDir)
                .imageFullDirectory(newImagesFullDir)
                .start(activity);
    }
}
