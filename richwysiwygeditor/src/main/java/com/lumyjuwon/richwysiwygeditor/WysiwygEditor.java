package com.lumyjuwon.richwysiwygeditor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lumyjuwon.richwysiwygeditor.RichEditor.EditableWebView;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.ImgPicker;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.Keyboard;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.TextColor;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.Youtube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Основная масса этого кода принадлежит lumyjuwon.
 *
 * Copyright 2019 lumyjuwon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class WysiwygEditor extends LinearLayout {

    public interface IEditorListener {
        void onGetHtml(String html);
    }

    private LayoutInflater layoutInflater;
    private EditableWebView webView;
//    private View popupView;
//    private PopupWindow popupWindow;
    private HorizontalScrollView toolBarPanel;
    private ImageButton bInsertImage;
    private CheckedImageButton bFgColor;
    private CheckedImageButton bBgColor;
    private CheckedImageButton bTextBold;
    private CheckedImageButton bTextItalic;
    private CheckedImageButton bTextUnderline;
    private CheckedImageButton bTextStrike;
    private CheckedImageButton bTextAlign;
    private ArrayList<CheckedImageButton> popupButtons;
    private ArrayList<CheckedImageButton> buttons;
    private int buttonBaseColor;
    private boolean isEditMode = true;

    public WysiwygEditor(Context context) {
        super(context);
        init();
    }

    public WysiwygEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WysiwygEditor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.activity_editor, this);

        this.layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.buttonBaseColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.black);
        int highlightColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.sky_blue);

        // webView
        webView = findViewById(R.id.web_view);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // sdk 19 ChromeWebView ?
        webView.setOnDecorationChangeListener((text, types) -> updateToolBarState(types));

        // toolBar
        this.toolBarPanel = findViewById(R.id.layout_toolbar);

        // text Size
        ImageButton textSizeButton = findViewById(R.id.button_textSize);
        textSizeButton.setOnClickListener(view -> {
//            closePopupWindow();
            showSizePopupWindow(view);
        });

        PopupButtonListener popupButtonListener = new PopupButtonListener();

        // text color
        bFgColor = findViewById(R.id.button_fgColor);
        bFgColor.setStateColors(buttonBaseColor, highlightColor);
        bFgColor.setOnClickListener(popupButtonListener);

        // background color
        bBgColor = findViewById(R.id.button_bgColor);
        bBgColor.setStateColors(buttonBaseColor, highlightColor);
        bBgColor.setOnClickListener(popupButtonListener);

        // align
        bTextAlign = findViewById(R.id.button_textAlign);
        bTextAlign.setOnClickListener(popupButtonListener);

        DecorationButtonListener decorationButtonListener = new DecorationButtonListener();

        // bold
        bTextBold = findViewById(R.id.button_textBold);
        bTextBold.setStateColors(buttonBaseColor, highlightColor);
        bTextBold.setOnClickListener(decorationButtonListener);

        // italic
        bTextItalic = findViewById(R.id.button_textItalic);
        bTextItalic.setStateColors(buttonBaseColor, highlightColor);
        bTextItalic.setOnClickListener(decorationButtonListener);

        // underline
        bTextUnderline = findViewById(R.id.button_textUnderLine);
        bTextUnderline.setStateColors(buttonBaseColor, highlightColor);
        bTextUnderline.setOnClickListener(decorationButtonListener);

        // strike through
        bTextStrike = findViewById(R.id.button_textStrike);
        bTextStrike.setStateColors(buttonBaseColor, highlightColor);
        bTextStrike.setOnClickListener(decorationButtonListener);

        // image insert
        bInsertImage = findViewById(R.id.button_imageInsert);
        bInsertImage.setOnClickListener(v -> ImgPicker.start(v));

        // launch YouTube app when clicked on YouTube embed link
        webView.setYoutubeLoadLinkListener(videoId -> {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
            getContext().startActivity(webIntent);
        });

        // video insert
        ImageButton videoInsertButton = findViewById(R.id.button_videoInsert);
        videoInsertButton.setOnClickListener(v -> {
//            closePopupWindow();
            clearPopupButton();
            Youtube.showYoutubeDialog(layoutInflater, webView, v);
        });

        popupButtons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextAlign));

    }

    /**
     *
     * @param types
     */
    private void updateToolBarState(List<EditableWebView.Type> types) {
        buttons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextBold, bTextItalic, bTextUnderline, bTextStrike));

        for (EditableWebView.Type type : types){
            if (type.name().contains("FONT_COLOR")) {
//                setCheckedButton(bFgColor, TextColor.getColor(type.name()));
                bFgColor.setCheckedState(true, TextColor.getColor(type.name()));
                buttons.remove(bFgColor);
            }
            else if (type.name().contains("BACKGROUND_COLOR")) {
//                setCheckedButton(bBgColor, TextColor.getColor(type.name()));
                bBgColor.setCheckedState(true, TextColor.getColor(type.name()));
                buttons.remove(bBgColor);
            }
            else {
                switch(type) {
                    case BOLD:
//                        setCheckedButton(bTextBold, buttonHighlightColor);
                        bTextBold.setCheckedState(true);
                        buttons.remove(bTextBold);
                        break;
                    case ITALIC:
//                        setCheckedButton(bTextItalic, buttonHighlightColor);
                        bTextItalic.setCheckedState(true);
                        buttons.remove(bTextItalic);
                        break;
                    case UNDERLINE:
//                        setCheckedButton(bTextUnderline, buttonHighlightColor);
                        bTextUnderline.setCheckedState(true);
                        buttons.remove(bTextUnderline);
                        break;
                    case STRIKETHROUGH:
//                        setCheckedButton(bTextStrike, buttonHighlightColor);
                        bTextStrike.setCheckedState(true);
                        buttons.remove(bTextStrike);
                        break;
                    default:
                }
            }
        }
        for(CheckedImageButton button : buttons) {
            button.setCheckedState(false);
        }
    }

//    /**
//     *
//     * @param button
//     * @param color
//     */
//    private void setCheckedButton(CheckedImageButton button, int color) {
//        button.setColorFilter(color);
//        if (!button.isChecked())
//            button.switchCheckedState();
//    }


    class PopupButtonListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (view instanceof CheckedImageButton) {
                CheckedImageButton button = (CheckedImageButton) view;

//                closePopupWindow();
                if (!button.isChecked()) {
//                    webView.clearFocusEditor();
                    if (button.getId() == R.id.button_fgColor)
                        showFgColorPopupWindow(view);
                    else if (button.getId() == R.id.button_bgColor)
                        showBgColorPopupWindow(view);
                    else if (button.getId() == R.id.button_textAlign)
                        showAlignPopupWindow(view);
                    clearPopupButton();
                }
                // ?
                button.switchCheckedState();
            }
        }
    }

    class DecorationButtonListener implements OnClickListener{
        @Override
        public void onClick(View view){
            if (view instanceof CheckedImageButton) {
                CheckedImageButton button = (CheckedImageButton) view;

//                closePopupWindow();
                clearPopupButton();
                webView.clearAndFocusEditor();
                if(button.getId() == R.id.button_textBold)
                    webView.setBold();
                else if(button.getId() == R.id.button_textItalic)
                    webView.setItalic();
                else if(button.getId() == R.id.button_textUnderLine)
                    webView.setUnderline();
                else if(button.getId() == R.id.button_textStrike)
                    webView.setStrikeThrough();
                button.switchCheckedState();

            }
        }
    }

    /**
     *
     * @param view
     */
    private void showSizePopupWindow(View view) {
        PopupWindow popupWindow = createPopupWindow(view, R.layout.popup_text_size);
        View contentView = popupWindow.getContentView();

        for (int i = 0; i < ((ViewGroup)contentView).getChildCount(); ++i) {
            View child = ((ViewGroup)contentView).getChildAt(i);
            if (child instanceof ImageButton) {
                child.setOnClickListener(view1 -> {
                    closePopupWindow(popupWindow);
                    try {
                        int size = Integer.parseInt((String) child.getTag());
                        webView.setFontSize(size);
                    } catch (NumberFormatException ex) {
                    }
                });
            }
        }
    }

    /**
     *
     * @param view
     */
    private void showFgColorPopupWindow(View view) {
        PopupWindow popupWindow = createPopupWindow(view, R.layout.popup_text_color);
        View contentView = popupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view1 -> {
                closePopupWindow(popupWindow);
                webView.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), value));
                int color = (value != R.color.white)
                        ? ContextCompat.getColor(context, value) : buttonBaseColor;
                bFgColor.switchCheckedState(color);
//                Keyboard.showKeyboard(view1);
            });
        }
    }

    /**
     *
     * @param view
     */
    private void showBgColorPopupWindow(View view) {
        PopupWindow popupWindow = createPopupWindow(view, R.layout.popup_text_color);
        View contentView = popupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view1 -> {
                closePopupWindow(popupWindow);
                webView.setTextBackgroundColor(ContextCompat.getColor(context, value));
                int color = (value != R.color.white)
                        ? ContextCompat.getColor(context, value) : buttonBaseColor;
                bBgColor.switchCheckedState(color);
//                Keyboard.showKeyboard(view1);
            });
        }
    }

    /**
     *
     * @param view
     */
    private void showAlignPopupWindow(View view) {
        PopupWindow popupWindow = createPopupWindow(view, R.layout.popup_text_align);
        View contentView = popupWindow.getContentView();

        ImageButton textAlignLeftButton = contentView.findViewById(R.id.text_alignLeft);
        textAlignLeftButton.setOnClickListener(view1 -> {
            closePopupWindow(popupWindow);
            webView.setAlignLeft();
            bTextAlign.switchCheckedState();
//            Keyboard.showKeyboard(view1);
            webView.focusEditor();
        });

        ImageButton textAlignCenterButton = contentView.findViewById(R.id.text_alignCenter);
        textAlignCenterButton.setOnClickListener(view12 -> {
            closePopupWindow(popupWindow);
            webView.setAlignCenter();
            bTextAlign.switchCheckedState();
//            Keyboard.showKeyboard(view12);
            webView.focusEditor();
        });

        ImageButton textAlignRightButton = contentView.findViewById(R.id.text_alignRight);
        textAlignRightButton.setOnClickListener(view13 -> {
            closePopupWindow(popupWindow);
            webView.setAlignRight();
            bTextAlign.switchCheckedState();
//            Keyboard.showKeyboard(view13);
            webView.focusEditor();
        });
    }

    /**
     *
     * @param anchor
     * @param contentViewId
     * @return
     */
    private PopupWindow createPopupWindow(View anchor, int contentViewId) {
        View popupView = layoutInflater.inflate(contentViewId, null);
        PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(-1); // 생성 애니메이션 -1, 생성 애니메이션 사용 안 함 0
        popupWindow.showAsDropDown(anchor, 0, +15);
        return popupWindow;
    }

    /**
     *
     * @param popupWindow
     */
    private static void closePopupWindow(PopupWindow popupWindow){
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    // 버튼 클릭 후 popup 버튼이 아닌 것을 클릭했을 때 기존 popup 버튼 false로 초기화
    private void clearPopupButton(){
        for(CheckedImageButton popupbutton : popupButtons){
            popupbutton.setCheckedState(false);
        }
    }

    public ImageButton getbInsertImage() {
        return bInsertImage;
    }

    public EditableWebView getWebView(){
        return webView;
    }

//    public String getHtml(){
//        return webView.getHtml();
//    }

    public void setToolBarVisibility(boolean isVisible) {
        toolBarPanel.setVisibility((isVisible) ? VISIBLE : GONE);
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        webView.setInputEnabled(isEditMode);
    }


    public void loadData(String data) {
        getWebView().loadData(data, "text/html", "UTF-8");
    }

    public void loadDataWithBaseURL(@Nullable String baseUrl, String data) {
        getWebView().loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
    }

}