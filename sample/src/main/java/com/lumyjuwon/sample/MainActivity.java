package com.lumyjuwon.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.lumyjuwon.richwysiwygeditor.RichWysiwyg;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RichWysiwyg wysiwyg;
    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wysiwyg = findViewById(R.id.rich);
        wysiwyg.getConfirmButton().setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println(wysiwyg.getContent().getHtml());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            printImages(images);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void printImages(List<Image> images) {
        if (images == null) return;

        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0, l = images.size(); i < l; i++) {
            stringBuffer.append(images.get(i).getPath()).append("\n");
            wysiwyg.getContent().insertImage("file://" + images.get(i).getPath(), "A");
        }
    }
}