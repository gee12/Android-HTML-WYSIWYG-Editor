package com.lumyjuwon.richwysiwygeditor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lumyjuwon.richwysiwygeditor.RichEditor.EditableWebView;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.ImgPicker;
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

//    public interface IListener {
//        void onGetHtml(String html);
//    }

    private LayoutInflater layoutInflater;
    protected EditableWebView webView;
    private PopupWindow popupWindow;
    private HorizontalScrollView toolBarPanel;
    private ProgressBar progressBar;
    private EditorActionButton bFgColor;
    private EditorActionButton bBgColor;
    private EditorActionButton bTextBold;
    private EditorActionButton bTextItalic;
    private EditorActionButton bTextUnderline;
    private EditorActionButton bTextStrike;
    private EditorActionButton bTextAlign;
    private ImageButton bInsertImage;
    private ArrayList<EditorActionButton> popupButtons;
    private ArrayList<EditorActionButton> buttons;
    private int buttonBaseColor;

    private EditableWebView.IPageLoadListener mPageLoadListener;

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
        inflate(getContext(), R.layout.layout_editor, this);

        this.layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.buttonBaseColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.black);
        int highlightColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.sky_blue);

        this.progressBar = findViewById(R.id.progress_bar);

        // webView
        webView = findViewById(R.id.web_view);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // sdk 19 ChromeWebView ?
        webView.setOnDecorationChangeListener((text, types) -> updateButtonsState(types));

        webView.setOnPageLoadListener(new EditableWebView.IPageLoadListener() {
            @Override
            public void onPageStartLoading() {
                progressBar.setVisibility(View.VISIBLE);
                if (mPageLoadListener != null)
                    mPageLoadListener.onPageStartLoading();
            }

            @Override
            public void onPageLoaded() {
                progressBar.setVisibility(View.GONE);
                if (mPageLoadListener != null)
                    mPageLoadListener.onPageLoaded();
            }
        });

        // toolBar
        this.toolBarPanel = findViewById(R.id.layout_toolbar);

        // decoration buttons
        OnClickListener decorationButtonListener = v -> WysiwygEditor.this.onClickDecorationButton((EditorActionButton) v);

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

        // popup buttons
        OnClickListener popupButtonListener = v -> WysiwygEditor.this.onClickPopupButton((EditorActionButton) v);

        // text size
        ImageButton textSizeButton = findViewById(R.id.button_textSize);
        textSizeButton.setOnClickListener(popupButtonListener);
//        textSizeButton.setOnClickListener(view -> {
//            closePopupWindow();
//            showTextSizePopupWindow(view);
//        });

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

        // image insert
        bInsertImage = findViewById(R.id.button_imageInsert);
        bInsertImage.setOnClickListener(ImgPicker::start);

        // launch YouTube app when clicked on YouTube embed link
        webView.setYoutubeLoadLinkListener(videoId -> {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
            getContext().startActivity(webIntent);
        });

        // video insert
        ImageButton videoInsertButton = findViewById(R.id.button_videoInsert);
        videoInsertButton.setOnClickListener(v -> {
            closePopupWindow();
            clearPopupButton();
            Youtube.showYoutubeDialog(layoutInflater, webView, v);
        });

        this.popupButtons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextAlign));
    }

    /**
     *
     * @param types
     */
    private void updateButtonsState(List<EditableWebView.Type> types) {
        this.buttons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextBold, bTextItalic, bTextUnderline, bTextStrike));

        for (EditableWebView.Type type : types){
            if (type.name().contains("FONT_COLOR")) {
//                setCheckedButton(bFgColor, TextColor.getColor(type.name()));
                bFgColor.setCheckedState(true, TextColor.getColor(getContext(), type.name()));
                buttons.remove(bFgColor);
            } else if (type.name().contains("BACKGROUND_COLOR")) {
//                setCheckedButton(bBgColor, TextColor.getColor(type.name()));
                bBgColor.setCheckedState(true, TextColor.getColor(getContext(), type.name()));
                buttons.remove(bBgColor);
            } else {
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

        for (EditorActionButton button : buttons) {
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

    /**
     *
     * @param button
     */
    public void onClickDecorationButton(EditorActionButton button) {
        if (button == null) return;
        closePopupWindow();
//        clearPopupButton();
//        webView.clearAndFocusEditor();
        if (button.getId() == R.id.button_textBold)
            webView.setBold();
        else if (button.getId() == R.id.button_textItalic)
            webView.setItalic();
        else if (button.getId() == R.id.button_textUnderLine)
            webView.setUnderline();
        else if (button.getId() == R.id.button_textStrike)
            webView.setStrikeThrough();
        button.switchCheckedState();
    }

    /**
     *
     * @param button
     */
    public void onClickPopupButton(EditorActionButton button) {
        if (button == null) return;
        closePopupWindow();
//        if (!button.isChecked()) {
//            webView.clearFocusEditor();
            if (button.getId() == R.id.button_textSize)
                showTextSizePopupWindow(button);
            else if (button.getId() == R.id.button_fgColor)
                showFgColorPopupWindow(button);
            else if (button.getId() == R.id.button_bgColor)
                showBgColorPopupWindow(button);
            else if (button.getId() == R.id.button_textAlign)
                showAlignPopupWindow(button);
//            clearPopupButton();
//        } else {
//
//        }
        // ?
//        button.switchCheckedState();
    }

    /**
     *
     * @param button
     */
    private void showTextSizePopupWindow(EditorActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_size);
        View contentView = popupWindow.getContentView();

        for (int i = 0; i < ((ViewGroup)contentView).getChildCount(); ++i) {
            View child = ((ViewGroup)contentView).getChildAt(i);
            if (child instanceof ImageButton) {
                child.setOnClickListener(view1 -> {
                    closePopupWindow();
                    try {
                        int size = Integer.parseInt((String) child.getTag());
                        webView.setFontSize(size);
                    } catch (NumberFormatException ex) {
                    }
                    button.setCheckedState(true);
//                    webView.focusEditor();
                });
            }
        }
    }

    /**
     *
     * @param button
     */
    private void showFgColorPopupWindow(EditorActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_color);
        View contentView = popupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view1 -> {
                closePopupWindow();
                webView.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), value));
                int color = (value != R.color.white)
                        ? ContextCompat.getColor(context, value) : buttonBaseColor;
//                bFgColor.switchCheckedState(color);
                button.setCheckedState(true, color);
//                Keyboard.showKeyboard(view1);
//                webView.focusEditor();
            });
        }
    }

    /**
     *
     * @param button
     */
    private void showBgColorPopupWindow(EditorActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_color);
        View contentView = popupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view1 -> {
                closePopupWindow();
                webView.setTextBackgroundColor(ContextCompat.getColor(context, value));
                int color = (value != R.color.white)
                        ? ContextCompat.getColor(context, value) : buttonBaseColor;
//                bBgColor.switchCheckedState(color);
                button.setCheckedState(true, color);
//                Keyboard.showKeyboard(view1);
//                webView.focusEditor();
            });
        }
    }

    /**
     *
     * @param button
     */
    private void showAlignPopupWindow(EditorActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_align);
        View contentView = popupWindow.getContentView();

        ImageButton textAlignLeftButton = contentView.findViewById(R.id.text_alignLeft);
        textAlignLeftButton.setOnClickListener(view1 -> {
            closePopupWindow();
            webView.setAlignLeft();
//            bTextAlign.switchCheckedState();
            button.setCheckedState(true);
//            Keyboard.showKeyboard(view1);
            webView.focusEditor();
        });

        ImageButton textAlignCenterButton = contentView.findViewById(R.id.text_alignCenter);
        textAlignCenterButton.setOnClickListener(view12 -> {
            closePopupWindow();
            webView.setAlignCenter();
//            bTextAlign.switchCheckedState();
            button.setCheckedState(true);
//            Keyboard.showKeyboard(view12);
            webView.focusEditor();
        });

        ImageButton textAlignRightButton = contentView.findViewById(R.id.text_alignRight);
        textAlignRightButton.setOnClickListener(view13 -> {
            closePopupWindow();
            webView.setAlignRight();
//            bTextAlign.switchCheckedState();
            button.setCheckedState(true);
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
     */
//    private static void closePopupWindow(PopupWindow popupWindow){
    private void closePopupWindow(){
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    // 버튼 클릭 후 popup 버튼이 아닌 것을 클릭했을 때 기존 popup 버튼 false로 초기화
    private void clearPopupButton(){
        for(EditorActionButton popupbutton : popupButtons){
            popupbutton.setCheckedState(false);
        }
    }

    public ImageButton getbInsertImage() {
        return bInsertImage;
    }

    public EditableWebView getWebView(){
        return webView;
    }

    public void setToolBarVisibility(boolean isVisible) {
        toolBarPanel.setVisibility((isVisible) ? VISIBLE : GONE);
    }

    public void setEditMode(boolean isEditMode) {
        webView.setInputEnabled(isEditMode);
    }

    public void setOnPageLoadListener(EditableWebView.IPageLoadListener listener) {
        this.mPageLoadListener = listener;
    }

}