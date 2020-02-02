package com.lumyjuwon.richwysiwygeditor;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

import com.gee12.htmlwysiwygeditor.ActionButton;
import com.gee12.htmlwysiwygeditor.ActionType;
import com.gee12.htmlwysiwygeditor.ColorUtils;
import com.gee12.htmlwysiwygeditor.Dialogs;
import com.lumyjuwon.richwysiwygeditor.RichEditor.EditableWebView;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.TextColor;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.Youtube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    protected LayoutInflater layoutInflater;
    protected EditableWebView webView;
    protected PopupWindow popupWindow;
    protected HorizontalScrollView toolBarPanel;
    protected LinearLayout layoutButtons;
    protected ProgressBar progressBar;
    private ActionButton bFgColor;
    private ActionButton bBgColor;
//    private ActionButton bTextBold;
    private ActionButton bTextItalic;
    private ActionButton bTextUnderline;
    private ActionButton bTextStrike;
    private ActionButton bTextAlign;
//    private ActionButton bInsertLine;
    private ImageButton bInsertImage;
    protected ArrayList<ActionButton> popupButtons;
//    private ArrayList<ActionButton> buttons;
    protected Map<ActionType, ActionButton> actionButtons;
//    private int buttonBaseColor;

    protected EditableWebView.IPageLoadListener mPageLoadListener;

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

//        this.buttonBaseColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.black);
//        int highlightColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.sky_blue);

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
        this.layoutButtons = findViewById(R.id.layout_toolbar_buttons);

        // decoration buttons
//        OnClickListener decorationButtonListener = v -> WysiwygEditor.this.onClickDecorationButton((ActionButton) v);

        // bold
//        bTextBold = findViewById(R.id.button_textBold);
//        bTextBold.setStateColors(buttonBaseColor, highlightColor);
//        bTextBold.setOnClickListener(decorationButtonListener);

        // italic
//        bTextItalic = findViewById(R.id.button_textItalic);
//        bTextItalic.setStateColors(buttonBaseColor, highlightColor);
//        bTextItalic.setOnClickListener(decorationButtonListener);

        // underline
//        bTextUnderline = findViewById(R.id.button_textUnderLine);
//        bTextUnderline.setStateColors(buttonBaseColor, highlightColor);
//        bTextUnderline.setOnClickListener(decorationButtonListener);

        // strike through
//        bTextStrike = findViewById(R.id.button_textStrike);
//        bTextStrike.setStateColors(buttonBaseColor, highlightColor);
//        bTextStrike.setOnClickListener(decorationButtonListener);

        // popup buttons
//        OnClickListener popupButtonListener = v -> WysiwygEditor.this.onClickPopupButton((ActionButton) v);

        // text size
//        ImageButton textSizeButton = findViewById(R.id.button_textSize);
//        textSizeButton.setOnClickListener(popupButtonListener);
//        textSizeButton.setOnClickListener(view -> {
//            closePopupWindow();
//            showTextSizePopupWindow(view);
//        });

        // text color
//        bFgColor = findViewById(R.id.button_fgColor);
//        bFgColor.setStateColors(buttonBaseColor, highlightColor);
//        bFgColor.setOnClickListener(popupButtonListener);

        // background color
//        bBgColor = findViewById(R.id.button_bgColor);
//        bBgColor.setStateColors(buttonBaseColor, highlightColor);
//        bBgColor.setOnClickListener(popupButtonListener);

        // align
//        bTextAlign = findViewById(R.id.button_textAlign);
//        bTextAlign.setOnClickListener(popupButtonListener);

        // image insert
//        bInsertImage = findViewById(R.id.button_imageInsert);
//        bInsertImage.setOnClickListener(ImgPicker::start);

        // launch YouTube app when clicked on YouTube embed link
//        webView.setYoutubeLoadLinkListener(new EditableWebView.IYoutubeLinkLoadListener() {
//            @Override
//            public void onYoutubeLinkLoad(String videoId) {
//                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
//                WysiwygEditor.this.getContext().startActivity(webIntent);
//            }
//        });

        // video insert
//        ImageButton videoInsertButton = findViewById(R.id.button_videoInsert);
//        videoInsertButton.setOnClickListener(v -> {
//            closePopupWindow();
//            clearPopupButton();
//            Youtube.showYoutubeDialog(layoutInflater, webView, v);
//        });

        initToolbar();

        this.popupButtons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextAlign));

    }

    protected void initToolbar() {
        this.actionButtons = new HashMap<>();
        for (int i = 0; i < layoutButtons.getChildCount(); i++) {
            ActionButton button = (ActionButton) layoutButtons.getChildAt(i);
            initActionButton(button);
        }
    }

    protected void initActionButton(ActionButton button) {
        int id = button.getId();
        if (id == R.id.button_text_size)
            initActionButton(button, ActionType.TEXT_SIZE, false, true, true);
        else if (id == R.id.button_text_bold)
            initActionButton(button, ActionType.BOLD, true, false, true);
        else if (id == R.id.button_text_italic)
            initActionButton(button, ActionType.ITALIC, true, false, true);
        else if (id == R.id.button_text_underLine)
            initActionButton(button, ActionType.UNDERLINE, true, false, true);
        else if (id == R.id.button_text_strike)
            initActionButton(button, ActionType.STRIKETHROUGH, true, false, true);
        else if (id == R.id.button_text_color)
            initActionButton(button, ActionType.TEXT_COLOR, true, true, true);
        else if (id == R.id.button_background_color)
            initActionButton(button, ActionType.BACKGROUND_COLOR, true, true, true);
        else if (id == R.id.button_text_align)
            initActionButton(button, ActionType.TEXT_ALIGN, false, true, true);

        else if (id == R.id.button_insert_line)
            initActionButton(button, ActionType.INSERT_LINE, false, false, true);
        else if (id == R.id.button_insert_link)
            initActionButton(button, ActionType.INSERT_LINK, true, true, true);
        else if (id == R.id.button_insert_image)
            initActionButton(button, ActionType.INSERT_IMAGE, true, true, false);
        else if (id == R.id.button_insert_video)
            initActionButton(button, ActionType.INSERT_VIDEO, true, true, false);

        else if (id == R.id.button_remove_format)
            initActionButton(button, ActionType.REMOVE_FORMAT, false, false, true);
    }

    protected void initActionButton(ActionButton button, ActionType type, boolean isCheckable,
                                    boolean isPopup, boolean isActive) {
        button.init(type, isCheckable, isPopup, isActive);
        button.setOnClickListener(v -> onClickActionButton((ActionButton) v));
        actionButtons.put(type, button);
    }

//    private ActionButton addActionButton(ActionType type, int imageId, boolean isCheckable, boolean isPopup) {
//        ActionButton button = new ActionButton(getContext(), type, imageId, isCheckable, isPopup);
//        button.setOnClickListener(v -> onClickActionButton((ActionButton) v));
//        layoutButtons.addView(button);
//        actionButtons.put(type, button);
//        return button;
//    }

    /**
     *
     * @param button
     */
    public void onClickActionButton(ActionButton button) {
        if (button == null) return;
        closePopupWindow();
//        clearPopupButton();
//        webView.clearAndFocusEditor();
        switch (button.getType()) {
            case TEXT_SIZE: showTextSizePopupWindow(button); break;
            case BOLD: webView.setBold(); break;
            case ITALIC: webView.setItalic(); break;
            case UNDERLINE: webView.setUnderline(); break;
            case STRIKETHROUGH: webView.setStrikeThrough(); break;
            case TEXT_COLOR: showTextColorPopupWindow(button); break;
            case BACKGROUND_COLOR: showBgColorPopupWindow(button); break;
            case TEXT_ALIGN: showTextAlignPopupWindow(button); break;

            case INSERT_LINE: webView.insertLine(); break;
            case INSERT_LINK: getShowInsertLinkPopupWindow(button); break;
            case INSERT_IMAGE: getShowInsertImagePopupWindow(button); break;
            case INSERT_VIDEO: getShowInsertVideoPopupWindow(button); break;

            case REMOVE_FORMAT: webView.removeFormat(); break;
        }
        if (!button.isPopup()) {
            button.switchCheckedState();
        }
    }

    /**
     *
     * @param types
     */
    private void updateButtonsState(Map<ActionType, String> types) {
//        this.buttons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextBold, bTextItalic, bTextUnderline, bTextStrike));
        ArrayList<ActionButton> buttons = new ArrayList<>(actionButtons.values());

        for (Map.Entry<ActionType,String> type : types.entrySet()){
            ActionButton button = actionButtons.get(type.getKey());
            if (button == null) continue;

            switch (type.getKey()) {
                case TEXT_COLOR:
                case BACKGROUND_COLOR:

                    // TODO: реализовать получение ЛЮБОГО цвета (проверить)

                    String value = type.getValue();
                    if (!TextUtils.isEmpty(value)) {
//                        int color = TextColor.getColor(getContext(), value);
                        int color = ColorUtils.rgbStringToColor(value);
                        button.setCheckedState(true, color);
                    } else {
                        button.setCheckedState(true);
                    }
                    break;

                default:
                    button.setCheckedState(true);
            }
            buttons.remove(button);
//            if (type.name().contains("FONT_COLOR")) {
//                bFgColor.setCheckedState(true, TextColor.getColor(getContext(), type.name()));
//                buttons.remove(bFgColor);
//            } else if (type.name().contains("BACKGROUND_COLOR")) {
//                bBgColor.setCheckedState(true, TextColor.getColor(getContext(), type.name()));
//                buttons.remove(bBgColor);
//            } else {
//                switch(type) {
//                    case BOLD:
//                        bTextBold.setCheckedState(true);
//                        buttons.remove(bTextBold);
//                        break;
//                    case ITALIC:
//                        bTextItalic.setCheckedState(true);
//                        buttons.remove(bTextItalic);
//                        break;
//                    case UNDERLINE:
//                        bTextUnderline.setCheckedState(true);
//                        buttons.remove(bTextUnderline);
//                        break;
//                    case STRIKETHROUGH:
//                        bTextStrike.setCheckedState(true);
//                        buttons.remove(bTextStrike);
//                        break;
//                    default:
//                }
//            }
        }

        for (ActionButton button : buttons) {
            button.setCheckedState(false);
        }
    }

//    /**
//     *
//     * @param button
//     */
//    public void onClickDecorationButton(ActionButton button) {
//        if (button == null) return;
//        closePopupWindow();
////        clearPopupButton();
////        webView.clearAndFocusEditor();
//        /*if (button.getId() == R.id.button_textBold)
//            webView.setBold();
//        else */if (button.getId() == R.id.button_textItalic)
//            webView.setItalic();
//        else if (button.getId() == R.id.button_textUnderLine)
//            webView.setUnderline();
//        else if (button.getId() == R.id.button_textStrike)
//            webView.setStrikeThrough();
//        button.switchCheckedState();
//    }

//    /**
//     *
//     * @param button
//     */
//    public void onClickPopupButton(ActionButton button) {
//        if (button == null) return;
//        closePopupWindow();
////        if (!button.isChecked()) {
////            webView.clearFocusEditor();
//            if (button.getId() == R.id.button_textSize)
//                showTextSizePopupWindow(button);
//            else if (button.getId() == R.id.button_fgColor)
//                showTextColorPopupWindow(button);
//            else if (button.getId() == R.id.button_bgColor)
//                showBgColorPopupWindow(button);
//            else if (button.getId() == R.id.button_textAlign)
//                showTextAlignPopupWindow(button);
////            clearPopupButton();
////        } else {
////
////        }
//        // ?
////        button.switchCheckedState();
//    }

    /**
     * Обработчик изменения размера шрифта.
     * @param button
     */
    private void showTextSizePopupWindow(ActionButton button) {
        if (button == null) return;

        // TODO: проверить
        Dialogs.createTextSizeDialog(getContext(), (size) -> {
            webView.setFontSize(size);
//            button.setCheckedState(true);
                webView.focusEditor();
        });

//        this.popupWindow = createPopupWindow(button, R.layout.popup_text_size);
//        View contentView = popupWindow.getContentView();

//        for (int i = 0; i < ((ViewGroup)contentView).getChildCount(); ++i) {
//            View child = ((ViewGroup)contentView).getChildAt(i);
//            if (child instanceof ImageButton) {
//                child.setOnClickListener(view -> {
//                    closePopupWindow();
//                    try {
//                        int size = Integer.parseInt((String) child.getTag());
//                        webView.setFontSize(size);
//                    } catch (NumberFormatException ex) {
//                    }
//                    button.setCheckedState(true);
////                    webView.focusEditor();
//                });
//            }
//        }
    }

    /**
     * Обработчик изменения цвета текста.
     * @param button
     */
    private void showTextColorPopupWindow(ActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_color);
        View contentView = popupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view -> {
                closePopupWindow();
                webView.setTextColor(ContextCompat.getColor(getContext().getApplicationContext(), value));
                int color = ContextCompat.getColor(context, (value != R.color.white)
                        ? value : ActionButton.RES_COLOR_BASE);
                button.setCheckedState(true, color);
//                webView.focusEditor();
            });
        }
    }

    /**
     * Обработчик изменения цвета фона текста.
     * @param button
     */
    private void showBgColorPopupWindow(ActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_color);
        View contentView = popupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view -> {
                closePopupWindow();
                webView.setTextBackgroundColor(ContextCompat.getColor(context, value));
                int color = ContextCompat.getColor(context, (value != R.color.white)
                        ? value : ActionButton.RES_COLOR_BASE);
                button.setCheckedState(true, color);
//                webView.focusEditor();
            });
        }
    }

    /**
     * Обработчик изменения выравнивания текста.
     * @param button
     */
    private void showTextAlignPopupWindow(ActionButton button) {
        if (button == null) return;
        this.popupWindow = createPopupWindow(button, R.layout.popup_text_align);
        View contentView = popupWindow.getContentView();

        ImageButton bAlignLeft = contentView.findViewById(R.id.text_alignLeft);
        bAlignLeft.setOnClickListener(view -> {
            closePopupWindow();
            webView.setAlignLeft();
//            bTextAlign.switchCheckedState();
            button.setCheckedState(true);
//            Keyboard.showKeyboard(view1);
//            webView.focusEditor();
        });

        ImageButton bAlignCenter = contentView.findViewById(R.id.text_alignCenter);
        bAlignCenter.setOnClickListener(view -> {
            closePopupWindow();
            webView.setAlignCenter();
//            bTextAlign.switchCheckedState();
            button.setCheckedState(true);
//            Keyboard.showKeyboard(view12);
//            webView.focusEditor();
        });

        ImageButton bAlignRight = contentView.findViewById(R.id.text_alignRight);
        bAlignRight.setOnClickListener(view -> {
            closePopupWindow();
            webView.setAlignRight();
//            bTextAlign.switchCheckedState();
            button.setCheckedState(true);
//            Keyboard.showKeyboard(view13);
//            webView.focusEditor();
        });
    }

    /**
     * Обработчики вставки, изменения и удаления ссылок.
     * @param button
     */
    public void getShowInsertLinkPopupWindow(ActionButton button) {
        if (button == null) return;

        // TODO: проверить
        boolean isLinkExist = button.isChecked();

        if (isLinkExist) {
            this.popupWindow = createPopupWindow(button, R.layout.popup_insert_link);
            View contentView = popupWindow.getContentView();

            // кнопка изменения ссылки
            ImageButton bChangeLink = contentView.findViewById(R.id.popup_change_link);
            bChangeLink.setOnClickListener(view -> {
                closePopupWindow();

                // TODO: проверить
                // диалог ввода ссылки (без заголовка)
                Dialogs.createInsertLinkDialog(getContext(), true, (link, title) -> {
                    webView.createLink(link);
                    button.setCheckedState(true);
//                webView.focusEditor();
                });
            });

            // кнопка удаления ссылки
            ImageButton bRemoveLink = contentView.findViewById(R.id.popup_remove_link);
            bRemoveLink.setOnClickListener(view -> {
                closePopupWindow();
                webView.removeLink();
                button.setCheckedState(false);
//                webView.focusEditor();
            });
        } else {
            // TODO: проверить
            // диалог ввода ссылки и заголовка
            Dialogs.createInsertLinkDialog(getContext(), false, (link, title) -> {
                webView.insertLink(link, title);
                button.setCheckedState(true);
//                webView.focusEditor();
            });
        }
    }

    /**
     * Обработчики вставки изображений.
     * @param button
     */
    public void getShowInsertImagePopupWindow(ActionButton button) {
    }

    /**
     * Обработчики вставки видео из Youtube.
     * @param button
     */
    public void getShowInsertVideoPopupWindow(ActionButton button) {
        if (button == null) return;
        closePopupWindow();
        clearPopupButton();
        Youtube.showYoutubeDialog(layoutInflater, webView, button);
    }

    /**
     *
     * @param anchor
     * @param contentViewId
     * @return
     */
    private PopupWindow createPopupWindow(View anchor, int contentViewId) {
        View popupView = layoutInflater.inflate(contentViewId, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(-1); // генерация анимации -1, отключить генерацию анимации 0
        popupWindow.showAsDropDown(anchor, 0, +15);
        return popupWindow;
    }

    /**
     *
     */
    private void closePopupWindow(){
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * Сброс существующей всплывающей кнопки,
     * когда пользователь нажимает куда-то кроме всплывающей кнопки после ее нажатия.
     */
    private void clearPopupButton(){
        for(ActionButton popupbutton : popupButtons){
            popupbutton.setCheckedState(false);
        }
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