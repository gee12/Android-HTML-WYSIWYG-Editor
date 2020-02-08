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

    protected LayoutInflater layoutInflater;
    protected EditableWebView webView;
    protected PopupWindow popupWindow;
    protected HorizontalScrollView toolBarPanel;
    protected LinearLayout layoutButtons;
    protected ProgressBar progressBar;
    protected Map<ActionType, ActionButton> actionButtons;
    protected EditableWebView.IPageLoadListener mPageLoadListener;
    protected boolean isActivateAllButtons = true;
    private int curTextSize;

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

        this.progressBar = findViewById(R.id.progress_bar);

        // webView
        webView = findViewById(R.id.web_view);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // sdk 19 ChromeWebView ?
        webView.setOnStateChangeListener((text, types) ->  webView.post(new Runnable() {
            @Override
            public void run() {
                updateButtonsState(types);
            }
        }));

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

        initToolbar();
    }

    protected void initToolbar() {
        this.actionButtons = new HashMap<>();
        for (int i = 0; i < layoutButtons.getChildCount(); i++) {
            View view = layoutButtons.getChildAt(i);
            if (view instanceof ActionButton) {
                initActionButton((ActionButton) view);
            }
        }
    }

    protected void initActionButton(ActionButton button) {
        int id = button.getId();
        if (id == R.id.button_undo)
            initActionButton(button, ActionType.UNDO, false, false);
        else if (id == R.id.button_redo)
            initActionButton(button, ActionType.REDO, false, false);
        else if (id == R.id.button_text_size)
            initActionButton(button, ActionType.TEXT_SIZE, false, true);
        else if (id == R.id.button_text_bold)
            initActionButton(button, ActionType.BOLD, true, false);
        else if (id == R.id.button_text_italic)
            initActionButton(button, ActionType.ITALIC, true, false);
        else if (id == R.id.button_text_underLine)
            initActionButton(button, ActionType.UNDERLINE, true, false);
        else if (id == R.id.button_text_strike)
            initActionButton(button, ActionType.STRIKETHROUGH, true, false);
        else if (id == R.id.button_text_color)
            initActionButton(button, ActionType.TEXT_COLOR, true, true);
        else if (id == R.id.button_background_color)
            initActionButton(button, ActionType.BACKGROUND_COLOR, true, true);
        else if (id == R.id.button_code)
            initActionButton(button, ActionType.CODE, true, false);
        else if (id == R.id.button_quote)
            initActionButton(button, ActionType.QUOTE, true, false);
        else if (id == R.id.button_text_align)
            initActionButton(button, ActionType.TEXT_ALIGN, true, true);
        else if (id == R.id.button_unordered_list)
            initActionButton(button, ActionType.UNORDERED_LIST, true, false);
        else if (id == R.id.button_ordered_list)
            initActionButton(button, ActionType.ORDERED_LIST, true, false);
        else if (id == R.id.button_indent_inc)
            initActionButton(button, ActionType.INDENT, false, false);
        else if (id == R.id.button_indent_dec)
            initActionButton(button, ActionType.OUTDENT, false, false);

        else if (id == R.id.button_insert_line)
            initActionButton(button, ActionType.INSERT_LINE, false, false);
        else if (id == R.id.button_insert_link)
            initActionButton(button, ActionType.INSERT_LINK, true, true);
        else if (id == R.id.button_insert_image)
            initActionButton(button, ActionType.INSERT_IMAGE, true, true);
        else if (id == R.id.button_insert_video)
            initActionButton(button, ActionType.INSERT_VIDEO, true, true);
        else if (id == R.id.button_insert_table)
            initActionButton(button, ActionType.INSERT_TABLE, false, true);
        else if (id == R.id.button_insert_formula)
            initActionButton(button, ActionType.INSERT_FORMULA, false, true);

        else if (id == R.id.button_remove_format)
            initActionButton(button, ActionType.REMOVE_FORMAT, false, false);
    }

    protected void initActionButton(ActionButton button, ActionType type, boolean isCheckable, boolean isPopup) {
        button.init(type, isCheckable, isPopup, true);
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
     * @param types
     */
    private void updateButtonsState(Map<ActionType, String> types) {
//        this.buttons = new ArrayList<>(Arrays.asList(bFgColor, bBgColor, bTextBold, bTextItalic, bTextUnderline, bTextStrike));
        ArrayList<ActionButton> buttons = new ArrayList<>(actionButtons.values());

        for (Map.Entry<ActionType,String> type : types.entrySet()){
            ActionButton button = actionButtons.get(type.getKey());
            if (button == null) continue;
            String value = type.getValue();

            switch (type.getKey()) {
                case TEXT_SIZE:
                    if (!TextUtils.isEmpty(value)) {
                        try {
                            this.curTextSize = Integer.parseInt(value);
                        } catch (Exception ignored) {}
                    }
                    break;
                case TEXT_COLOR:
                case BACKGROUND_COLOR:
                    // TODO: проверить получение ЛЮБОГО цвета

                    if (!TextUtils.isEmpty(value)) {
//                        int color = TextColor.getColor(getContext(), value);
                        int color = ColorUtils.rgbStringToColor(value);
                        button.setCheckedState(true, color);
                    } else {
                        button.setCheckedState(true);
                    }
                    break;

                case TEXT_ALIGN:
                    // TODO: реализовать смену "основной" иконки в зависимости от установленного отступа

                    button.setCheckedState(true);
                    break;

                default:
                    button.setCheckedState(true);
            }
            buttons.remove(button);
        }

        for (ActionButton button : buttons) {
            button.setCheckedState(false);
        }
    }

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
            case UNDO: webView.undo(); break;
            case REDO: webView.redo(); break;
            case TEXT_SIZE: showTextSizePopupWindow(button); break;
            case BOLD: webView.setBold(); break;
            case ITALIC: webView.setItalic(); break;
            case UNDERLINE: webView.setUnderline(); break;
            case STRIKETHROUGH: webView.setStrikeThrough(); break;
            case TEXT_COLOR: showTextColorPopupWindow(button); break;
            case BACKGROUND_COLOR: showBackgroundColorPopupWindow(button); break;
            case CODE: webView.setCode(); break;
            case QUOTE: webView.setBlockquote(); break;
            case TEXT_ALIGN: showTextAlignPopupWindow(button); break;
            case UNORDERED_LIST: webView.setBullets(); break;
            case ORDERED_LIST: webView.setNumbers(); break;
            case INDENT: webView.setIndent(); break;
            case OUTDENT: webView.setOutdent(); break;

            case INSERT_LINE: webView.insertLine(); break;
            case INSERT_LINK: showLinkPopupWindow(button); break;
            case INSERT_IMAGE: showImagePopupWindow(button); break;
            case INSERT_VIDEO: showVideoPopupWindow(button); break;
            case INSERT_TABLE: break;
            case INSERT_FORMULA: break;

            case REMOVE_FORMAT: webView.removeFormat(); break;
        }
        if (button.isCheckable() && !button.isPopup()) {
            button.switchCheckedState();
        }
    }

    /**
     * Обработчик изменения размера шрифта.
     * @param button
     */
    private void showTextSizePopupWindow(ActionButton button) {
        if (button == null) return;

        // TODO: проверить
        Dialogs.createTextSizeDialog(getContext(), curTextSize, (size) -> {
            webView.setFontSize(size);
//            button.setCheckedState(true);
//                webView.focusEditor();
        });

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
    private void showBackgroundColorPopupWindow(ActionButton button) {
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
    public void showLinkPopupWindow(ActionButton button) {
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
    public void showImagePopupWindow(ActionButton button) {
    }

    /**
     * Обработчики вставки видео из Youtube.
     * @param button
     */
    public void showVideoPopupWindow(ActionButton button) {
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
        for(ActionButton button : actionButtons.values()){
            if (button.isPopup()) {
                button.setCheckedState(false);
            }
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