package com.lumyjuwon.richwysiwygeditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
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
import com.gee12.htmlwysiwygeditor.IImagePicker;
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

    protected LayoutInflater mLayoutInflater;
    protected EditableWebView mWebView;
    protected PopupWindow mPopupWindow;
    protected HorizontalScrollView mToolBarPanel;
    protected LinearLayout mLayoutButtons;
    protected ProgressBar mProgressBar;
    protected Map<ActionType, ActionButton> mActionButtons;
    private View mViewScrollBottom;
    private View mViewScrollTop;

    private EditableWebView.IScrollListener mScrollListener;
    protected EditableWebView.IPageLoadListener mPageLoadListener;
    protected IImagePicker mImgPickerListener;

    //    protected boolean isActivateAllButtons = true;
    private int mCurTextSize;
    protected boolean mIsEdited;

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
        this.mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mProgressBar = findViewById(R.id.progress_bar);

        // webView
        mWebView = findViewById(R.id.web_view);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null); // sdk 19 ChromeWebView ?

        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        // set for clarity (default true)
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        // resize contents to fit the screen
//        settings.setUseWideViewPort(true);
//        mWebView.setInitialScale(1);
        // just in case
        settings.setDefaultTextEncodingName("utf-8");

        mWebView.setOnTextChangeListener(text -> setIsEdited(true) );
        mWebView.setOnStateChangeListener((text, types) ->  mWebView.post(() -> updateButtonsState(types)));

        mWebView.setOnPageLoadListener(new EditableWebView.IPageLoadListener() {
            @Override
            public void onPageStartLoading() {
                mProgressBar.setVisibility(View.VISIBLE);
                if (mPageLoadListener != null)
                    mPageLoadListener.onPageStartLoading();
            }

            @Override
            public void onPageLoaded() {
                mProgressBar.setVisibility(View.GONE);
                if (mPageLoadListener != null)
                    mPageLoadListener.onPageLoaded();
            }

            @Override
            public void onEditorJSLoaded() {}
        });

        // FIXME: обработчик не запустится, т.к. переопределяется в активности

//        webView.setOnTouchListener((v, event) -> {
//            closePopupWindow();
//            return false;
//        });

//        addScrollButtons();

        // toolBar
        this.mToolBarPanel = findViewById(R.id.layout_toolbar);
        this.mLayoutButtons = findViewById(R.id.layout_toolbar_buttons);

        initToolbar();
    }

    /**
     * Кнопки для пролистывания WebView до упора вниз/вверх.
     */
    public void initScrollButtons(View scrollDownView, View scrollUpView) {
        if (scrollDownView == null || scrollUpView == null) {
            return;
        }
        this.mViewScrollBottom = scrollDownView;
        this.mViewScrollTop = scrollUpView;
//        mButtonScrollDown.setVisibility(VISIBLE);
        this.mScrollListener = new EditableWebView.IScrollListener() {
            @Override
            public void onScrolledToTop() {
                mViewScrollBottom.setVisibility(VISIBLE);
            }

            @Override
            public void onScrolledToBottom() {
                mViewScrollTop.setVisibility(VISIBLE);
            }

            @Override
            public void onScrolledVertical(int direction) {

                mViewScrollBottom.setVisibility(GONE);
                mViewScrollTop.setVisibility(GONE);

                // TODO: доделать

                /*if (direction > 0) {
                    mButtonScrollDown.setVisibility(VISIBLE);
                    mButtonScrollUp.setVisibility(GONE);
                } else {
                    mButtonScrollUp.setVisibility(VISIBLE);
                    mButtonScrollDown.setVisibility(GONE);
                }*/
            }

            @Override
            public void onScrollEnd() {
//                mViewScrollBottom.setVisibility(GONE);
//                mViewScrollTop.setVisibility(GONE);
            }

        };
//        webView.setScrollListener(mScrollListener);
        final int density = (int) (getResources().getDisplayMetrics().density);
        mViewScrollBottom.setOnClickListener(v -> {
            mWebView.scrollTo(0, mWebView.getContentHeight() * density);
            mViewScrollBottom.setVisibility(GONE);
        });

        mViewScrollTop.setOnClickListener(v -> {
            mWebView.scrollTo(0, 0);
            mViewScrollTop.setVisibility(GONE);
        });
    }

    protected void initToolbar() {
        this.mActionButtons = new HashMap<>();
        for (int i = 0; i < mLayoutButtons.getChildCount(); i++) {
            View view = mLayoutButtons.getChildAt(i);
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
            initActionButton(button, ActionType.INSERT_IMAGE, false, true);
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
        mActionButtons.put(type, button);
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
        ArrayList<ActionButton> buttons = new ArrayList<>(mActionButtons.values());

        for (Map.Entry<ActionType,String> type : types.entrySet()){
            ActionButton button = mActionButtons.get(type.getKey());
            if (button == null) continue;
            String value = type.getValue();

            switch (type.getKey()) {
                case TEXT_SIZE:
                    if (!TextUtils.isEmpty(value)) {
                        try {
                            this.mCurTextSize = Integer.parseInt(value);
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
            case UNDO: mWebView.undo(); break;
            case REDO: mWebView.redo(); break;
            case TEXT_SIZE: showTextSizePopupWindow(button); break;
            case BOLD: mWebView.setBold(); break;
            case ITALIC: mWebView.setItalic(); break;
            case UNDERLINE: mWebView.setUnderline(); break;
            case STRIKETHROUGH: mWebView.setStrikeThrough(); break;
            case TEXT_COLOR: showColorPopupWindow(button); break;
//            case BACKGROUND_COLOR: showBackgroundColorPopupWindow(button); break;
            case BACKGROUND_COLOR: showColorPopupWindow(button); break;
            case CODE: mWebView.setCode(); break;
            case QUOTE: mWebView.setBlockquote(); break;
            case TEXT_ALIGN: showTextAlignPopupWindow(button); break;
            case UNORDERED_LIST: mWebView.setBullets(); break;
            case ORDERED_LIST: mWebView.setNumbers(); break;
            case INDENT: mWebView.setIndent(); break;
            case OUTDENT: mWebView.setOutdent(); break;

            case INSERT_LINE: mWebView.insertLine(); break;
            case INSERT_LINK: showLinkPopupWindow(button); break;
            case INSERT_IMAGE: showImagePopupWindow(button); break;
            case INSERT_VIDEO: showVideoPopupWindow(button); break;
            case INSERT_TABLE: break;
            case INSERT_FORMULA: break;

            case REMOVE_FORMAT: mWebView.removeFormat(); break;
        }
        // теперь вызывается stateChange
//        if (button.isCheckable() && !button.isPopup()) {
//            button.switchCheckedState();
//        }

        if (!button.isPopup()) {
            setIsEdited();
        }
    }

    /**
     * Обработчик изменения размера шрифта.
     * @param button
     */
    private void showTextSizePopupWindow(ActionButton button) {
        if (button == null) return;

        Dialogs.createTextSizeDialog(getContext(), mCurTextSize, (size) -> {
            mWebView.setFontSize(size);
        });

    }

    /**
     * Обработчик изменения цвета текста.
     * @param button
     */
    private void showColorPopupWindow(ActionButton button) {
        if (button == null) return;
        this.mPopupWindow = createPopupWindow(button, R.layout.popup_text_color);
        View contentView = mPopupWindow.getContentView();

        Context context = getContext().getApplicationContext();
        for (Integer key : TextColor.colorMap.keySet()){
            final int value = TextColor.colorMap.get(key);
            Button popupButton = contentView.findViewById(key);
            popupButton.setOnClickListener(view -> {
                closePopupWindow();
                if (button.getId() == R.id.button_text_color) {
                    mWebView.setTextColor(ContextCompat.getColor(context, value));
                } else {
                    mWebView.setTextBackgroundColor(ContextCompat.getColor(context, value));
                }
                setIsEdited();
                // теперь вызывается stateChange
//                int color = ContextCompat.getColor(context, (value != R.color.white)
//                        ? value : ActionButton.RES_COLOR_BASE);
//                button.setCheckedState(true, color);
//                webView.focusEditor();
            });
        }
    }

//    /**
//     * Обработчик изменения цвета фона текста.
//     * @param button
//     */
//    private void showBackgroundColorPopupWindow(ActionButton button) {
//        if (button == null) return;
//        this.popupWindow = createPopupWindow(button, R.layout.popup_text_color);
//        View contentView = popupWindow.getContentView();
//
//        Context context = getContext().getApplicationContext();
//        for (Integer key : TextColor.colorMap.keySet()){
//            final int value = TextColor.colorMap.get(key);
//            Button popupButton = contentView.findViewById(key);
//            popupButton.setOnClickListener(view -> {
//                closePopupWindow();
//                webView.setTextBackgroundColor(ContextCompat.getColor(context, value));
//                setIsEdited();
//                // теперь вызывается stateChange
////                int color = ContextCompat.getColor(context, (value != R.color.white)
////                        ? value : ActionButton.RES_COLOR_BASE);
////                button.setCheckedState(true, color);
////                webView.focusEditor();
//            });
//        }
//    }

    /**
     * Обработчик изменения выравнивания текста.
     * @param button
     */
    private void showTextAlignPopupWindow(ActionButton button) {
        if (button == null) return;
        this.mPopupWindow = createPopupWindow(button, R.layout.popup_text_align);
        View contentView = mPopupWindow.getContentView();

        OnClickListener listener = v -> {
           int id = v.getId();
           closePopupWindow();
           if (id == R.id.text_alignLeft)
               mWebView.setAlignLeft();
           else if (id == R.id.text_alignCenter)
               mWebView.setAlignCenter();
           else
               mWebView.setAlignRight();
           setIsEdited();
        };
        setClickListener(contentView, R.id.text_alignLeft, listener);
        setClickListener(contentView, R.id.text_alignCenter, listener);
        setClickListener(contentView, R.id.text_alignRight, listener);
    }

    /**
     * Обработчики вставки, изменения и удаления ссылок.
     * @param button
     */
    private void showLinkPopupWindow(ActionButton button) {
        if (button == null) return;

        // TODO: проверить
        boolean isLinkExist = button.isChecked();

        if (isLinkExist) {
            this.mPopupWindow = createPopupWindow(button, R.layout.popup_link);
            View contentView = mPopupWindow.getContentView();

            // кнопка изменения ссылки
            ImageButton bChangeLink = contentView.findViewById(R.id.popup_change_link);
            bChangeLink.setOnClickListener(view -> {
                closePopupWindow();

                // TODO: проверить
                // диалог ввода ссылки (без заголовка)
                Dialogs.createInsertLinkDialog(getContext(), true, (link, title) -> {
                    mWebView.createLink(link);
                    setIsEdited();
//                    button.setCheckedState(true);
//                webView.focusEditor();
                });
            });

            // кнопка удаления ссылки
            ImageButton bRemoveLink = contentView.findViewById(R.id.popup_remove_link);
            bRemoveLink.setOnClickListener(view -> {
                closePopupWindow();
                mWebView.removeLink();
                setIsEdited();
//                button.setCheckedState(false);
//                webView.focusEditor();
            });
        } else {
            // TODO: проверить
            // диалог ввода ссылки и заголовка
            Dialogs.createInsertLinkDialog(getContext(), false, (link, title) -> {
                mWebView.insertLink(link, title);
                setIsEdited();
                // теперь вызывается stateChange
//                button.setCheckedState(true);
//                webView.focusEditor();
            });
        }
    }

    /**
     * Обработчики вставки изображений.
     * @param button
     */
    private void showImagePopupWindow(ActionButton button) {
        if (button == null) return;
//        closePopupWindow();
//        clearPopupButton();

        this.mPopupWindow = createPopupWindow(button, R.layout.popup_image);
        View contentView = mPopupWindow.getContentView();

        OnClickListener listener = v -> {
            int id = v.getId();
            closePopupWindow();
            if (id == R.id.popup_insert_image) {
//                ImgPicker.startPicker((Activity)getContext());
                if (mImgPickerListener != null) {
                    mImgPickerListener.startPicker();
                }
            } else if (id == R.id.popup_capture_photo) {
//                ImgPicker.startCamera((Activity)getContext(), mImagesFolder);
                if (mImgPickerListener != null) {
                    mImgPickerListener.startCamera();
                }
            }
//            else if (id == R.id.popup_edit_image)

        };
        setClickListener(contentView, R.id.popup_insert_image, listener);
        setClickListener(contentView, R.id.popup_capture_photo, listener);
        setClickListener(contentView, R.id.popup_edit_image, listener);
    }

    /**
     * Вставка выбранных изображений.
     * @param imagesFileNames
     *//*

    public void onSelectImages(List<String> imagesFileNames) {
        if (imagesFileNames == null)
            return;
        int size = imagesFileNames.size();
        if (size > 0) {
            if (size == 1) {
                // обрабатываем изображение только когда выбран один файл
                showEditImageDialog(imagesFileNames.get(0));
            } else {
                for (String fileName : imagesFileNames) {
                    webView.insertImage(fileName, null);
                }
            }
            setIsEdited();
        }
    }
*/

    /**
     * Обработка изображения перед добавлением.
     * @param imageFullName
     * @param srcWidth
     * @param srcHeight
     */
    protected void showEditImageDialog(String imageFullName, int srcWidth, int srcHeight) {
        Dialogs.createImageDimensDialog(getContext(), srcWidth, srcHeight, (width, height) -> {
            mWebView.insertImage(imageFullName, width, height);
        });
    }

    /**
     * Обработчики вставки видео из Youtube.
     * @param button
     */
    private void showVideoPopupWindow(ActionButton button) {
        if (button == null) return;
        closePopupWindow();
        clearPopupButton();
        Youtube.showYoutubeDialog(mLayoutInflater, mWebView, button);
    }

    /**
     *
     * @param anchorView
     * @param contentViewId
     * @return
     */
    private PopupWindow createPopupWindow(View anchorView, int contentViewId) {
        View popupView = mLayoutInflater.inflate(contentViewId, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false); // если true - не будет кликабельно все вокруг popup
        popupWindow.setOutsideTouchable(true); // true - клик за пределами popup закрывает его
            // (в Android 4.4 не работает)
        popupWindow.setBackgroundDrawable(new ColorDrawable()); // чтобы заработал setOutsideTouchable()
        popupWindow.setAnimationStyle(-1); // -1 - генерация анимации, 0 - отключить анимацию
        int xoff = 0;
        int yoff = +10;
        if (Build.VERSION.SDK_INT < 24) {
            popupWindow.showAsDropDown(anchorView, xoff, yoff);
        } else {
            int[] location = new int[2];
            anchorView.getLocationInWindow(location);
            popupWindow.showAtLocation(((Activity)getContext()).getWindow().getDecorView(),
                    Gravity.NO_GRAVITY, location[0] + xoff, location[1] - anchorView.getMeasuredHeight() - yoff);
        }
        return popupWindow;
    }

    /**
     *
     */
    private void closePopupWindow(){
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    /**
     * Сброс существующей всплывающей кнопки,
     * когда пользователь нажимает куда-то кроме всплывающей кнопки после ее нажатия.
     */
    private void clearPopupButton(){
        for(ActionButton button : mActionButtons.values()){
            if (button.isPopup()) {
                button.setCheckedState(false);
            }
        }
    }

    public void setScrollButtonsVisibility(boolean vis) {
        if (mViewScrollBottom == null || mViewScrollTop == null)
            return;
        boolean canScrollToTop = mWebView.canScrollVertically(-1);
        boolean canScrollToBottom = mWebView.canScrollVertically(1);
        // нужно ли вообще отображать кнопки скроллинга (если контент полностью помещается)
        boolean canScroll = canScrollToTop || canScrollToBottom;

        // TODO: раскомментить для исправления
//        mViewScrollBottom.setVisibility(getVisibility(vis && canScroll && !canScrollToTop));
//        mViewScrollTop.setVisibility(getVisibility(vis && canScroll && !canScrollToBottom));
//        mWebView.setScrollListener((vis) ? mScrollListener : null);
    }

    /**
     * Запуск поиска текста.
     * @param query
     */
    public void searchText(String query) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.findAllAsync(query);
        } else {
            mWebView.findAll(query);
        }
    }

    public void stopSearch() {
        mWebView.clearMatches();
    }

    public void nextMatch() {
        mWebView.findNext(true);
    }

    public void prevMatch() {
        mWebView.findNext(false);
    }

    public static int getVisibility(boolean isVisible) {
        return (isVisible) ? ViewGroup.VISIBLE : ViewGroup.GONE;
    }

    public void setClickListener(View parentView, int viewId, OnClickListener listener) {
        parentView.findViewById(viewId).setOnClickListener(listener);
    }

    public EditableWebView getWebView(){
        return mWebView;
    }

    public void setProgressBarVisibility(boolean vis) {
        mProgressBar.setVisibility(getVisibility(vis));
    }

    public void setToolBarVisibility(boolean vis) {
        mToolBarPanel.setVisibility(getVisibility(vis));
    }

    public void setEditMode(boolean isEditMode) {
        mWebView.setInputEnabled(isEditMode);
    }

    public void setOnPageLoadListener(EditableWebView.IPageLoadListener listener) {
        this.mPageLoadListener = listener;
    }

    public boolean isEdited() {
        return mIsEdited;
    }

    public void setIsEdited() {
        setIsEdited(true);
    }

    public void setIsEdited(boolean isEdited) {
        this.mIsEdited = isEdited;
    }

    public void setImgPickerCallback(IImagePicker callback) {
        this.mImgPickerListener = callback;
    }

}