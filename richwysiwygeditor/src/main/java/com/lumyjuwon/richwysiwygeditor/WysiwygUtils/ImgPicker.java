package com.lumyjuwon.richwysiwygeditor.WysiwygUtils;

import android.app.Activity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.lumyjuwon.richwysiwygeditor.R;

import java.util.List;

public class ImgPicker {

    public interface IImgPicker {
        void startPickImages(Activity activity, String newImagesFullDir);
        void receiveSelectedImages(List<String> fileNames);
    }

    private static ImagePicker imagePicker;

    private static ImagePicker getImagePicker(Activity activity, String newImagesFullDir) {
        imagePicker = ImagePicker.create(activity);

        return imagePicker.limit(10) // max images can be selected (99 by default)
                .toolbarFolderTitle("Gallery")
                .toolbarDoneButtonText(activity.getString(R.string.confirm_selected_images))
                .showCamera(false) // show camera or not (true by default)
                .folderMode(true)
                .includeVideo(false)
                .imageFullDirectory(newImagesFullDir);
    }

    public static void start(Activity activity, String newImagesFullDir) {
        getImagePicker(activity, newImagesFullDir).start(); // start image picker activity with request code
    }

}
