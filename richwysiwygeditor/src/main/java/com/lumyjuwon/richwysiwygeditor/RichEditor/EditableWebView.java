package com.lumyjuwon.richwysiwygeditor.RichEditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.gee12.htmlwysiwygeditor.ActionType;
import com.gee12.htmlwysiwygeditor.ColorUtils;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.Youtube;

import java.util.HashMap;
import java.util.Map;

/**
 * Основная масса этого кода принадлежит Wasabeef.
 *
 * Copyright (C) 2017 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class EditableWebView extends WebView {

    public static final String JAVASCRIPT = "javascript:";

    public interface ITextChangeListener {
        void onTextChange(String text);
    }

    public interface ITextStateListener {
        void onStateChange(String text, Map<ActionType, String> types);
    }

    public interface IPageLoadListener {
        void onStartPageLoading();
        void onPageLoading(int progress);
        void onPageLoaded();
        void onEditorJSLoaded();
        void onStartEditorJSLoading();
    }

    public interface ILinkLoadListener {
        boolean onLinkLoad(String url);
    }

    public interface IYoutubeLinkLoadListener {
        void onYoutubeLinkLoad(String videoId);
    }

    public interface IHtmlReceiveListener {
        void onReceiveEditableHtml(String htmlText);
    }

    public interface IScrollListener {
        void onScrolledVertical(int direction);
        void onScrolledToTop();
        void onScrolledToBottom();
        void onScrollEnd();
    }

    /**
     * Class with methods to be called from javascript.
     */
    public class JavascriptInterface {

        JavascriptInterface() { }

        @android.webkit.JavascriptInterface
        public void textChange(/*String text, */String html) {
            EditableWebView.this.onTextChanged(/*text, */html);
        }

        @android.webkit.JavascriptInterface
        public void stateChange(String formatsAsQuery/*, String selectedText*/) {
            EditableWebView.this.onStateChanged(formatsAsQuery);
        }

        @android.webkit.JavascriptInterface
        public void receiveHtml(String html) {
            EditableWebView.this.onReceiveEditableHtml(html);
        }
    }

    public static final int EXEC_TRY_DELAY_MSEC = 100;
    public static final String EDITOR_JS_FILE = "editor.js";

    private boolean mIsPageLoaded = false;
    private boolean mIsEditorJSLoaded = false;
    private boolean mIsHtmlRequestMade = false;
    private boolean mIsEditMode = false;
    private String mHtml;
    private String mBaseUrl;
    // listeners
    private ITextChangeListener mTextChangeListener;
    private ITextStateListener mStateListener;
    private IPageLoadListener mPageListener;
    private ILinkLoadListener mUrlLoadListener;
    private IHtmlReceiveListener mReceiveHtmlListener;
    private IYoutubeLinkLoadListener mLoadYoutubeLinkListener;
    private IScrollListener mScrollListener;

    public EditableWebView(Context context) {
        this(context, null);
    }

    public EditableWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public EditableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        setWebChromeClient(new TetroidWebChromeClient());
        setWebViewClient(new EditorWebViewClient());
        applyAttributes(context, attrs);
        addJavascriptInterface(new JavascriptInterface(), "Android");
    }

    /**
     * Page load event handler (caused by WebView).
     */
    public void onPageLoaded() {
/*        // load main javascript code
        loadEditorScript();
        // send first javascript request to receive page html
//        if (!mIsPageLoaded) {
            makeEditableHtmlRequest();
//        }*/
        this.mIsPageLoaded = true;

        if (mPageListener != null) {
            mPageListener.onPageLoaded();
        }
    }

    public void loadEditorJSScript(boolean isMakeHtmlRequest) {
        if (!mIsEditorJSLoaded) {
//            this.mIsJavaScriptLoaded = true;

            byte[] buffer = Utils.readFileFromAssets(getContext(), EDITOR_JS_FILE);
            if (buffer != null) {
                if (mPageListener != null) {
                    mPageListener.onStartEditorJSLoading();
                }
                load(JAVASCRIPT + new String(buffer), value -> {
                    this.mIsEditorJSLoaded = true;
                    if (mPageListener != null) {
                        mPageListener.onEditorJSLoaded();
                    }
                    if (isMakeHtmlRequest) {
                        makeEditableHtmlRequest();
                    }
                });
            }
        } else if (isMakeHtmlRequest) {
            makeEditableHtmlRequest();
        }
    }

    /**
     * Запрос на получение html-кода редактируемого фрагмента страницы.
     */
    public void makeEditableHtmlRequest() {
        load(JAVASCRIPT + "Android.receiveHtml(RE.getHtml());", null);
        this.mIsHtmlRequestMade = true;
    }

    private void onReceiveEditableHtml(String html) {

        // FIXED?
        // Javascript always returned html-text with excess line break ('\n') in the end,
        // remove it
//        if (html.length() >= 1) {
//            html = html.substring(0, html.length()-1);
//        }
        EditableWebView.this.mHtml = html;
        if (mReceiveHtmlListener != null)
            mReceiveHtmlListener.onReceiveEditableHtml(html);
    }

    private void onTextChanged(/*String text, */String html) {
//        this.text = text;
        this.mHtml = html;
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(html);
        }
    }

    /**
     *  1) разделить строку по амперсантам
     *  2) делать цикл по полученным подстрокам
     *  3) разделить подстроку по "="
     *  4) если есть второй элемент - это значение (цвет, например)
     * @param formatsAsQuery
     */
    private void onStateChanged(String formatsAsQuery) {
        String[] typesStrings = formatsAsQuery.split("&");
        Map<ActionType, String> types = new HashMap<>();

        for (String typeString : typesStrings) {
            String[] typeParts = typeString.split("=");
            if (typeParts.length > 1) {
                ActionType type = ActionType.parse(typeParts[0]);
                String value = (typeParts[1] != null) ? typeParts[1].toUpperCase() : "";
                types.put(type, value);
            } else {
                ActionType type = ActionType.parse(typeString);
                types.put(type, "");
            }
        }

        if (mStateListener != null) {
            mStateListener.onStateChange(formatsAsQuery, types);
        }
    }

    public Boolean onUrlLoading(String url) {
        // если не используется режим редактирования, то не обрабатываем события редактирования страницы
        if (!mIsEditMode) {
            if (mUrlLoadListener != null)
                return mUrlLoadListener.onLinkLoad(url);
        }

        // User clicks the link that is youtube then post video id.
        if (!Youtube.getVideoId(url).equals("error")) {
            String videoid = Youtube.getVideoId(url);
            if (!videoid.equals("error")) {
                if (mLoadYoutubeLinkListener != null) {
                    mLoadYoutubeLinkListener.onYoutubeLinkLoad(videoid);
                }
            }
            return true;
        } else if (mUrlLoadListener != null)
            // ?
            return mUrlLoadListener.onLinkLoad(url);
        return null;
    }

    /**
     *
     * @param trigger
     */
    protected void execJavascript(final String trigger) {
        execJavascript(trigger, true);
    }

    /**
     *
     * @param trigger
     */
    protected void execJavascript(final String trigger, boolean callStateChange) {
        exec(JAVASCRIPT + trigger);
        if (callStateChange)
            exec(JAVASCRIPT + "RE.stateChange();");
    }

    /**
     *
     * @param trigger
     */
    protected void exec(final String trigger) {
        if (mIsPageLoaded) {
            load(trigger, null);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, EXEC_TRY_DELAY_MSEC);
        }
    }

    /**
     *
     * @param trigger
     */
    private void load(String trigger, ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, callback);
        } else {
            loadUrl(trigger);
        }
    }

    /**
     *
     * @param context
     * @param attrs
     */
    private void applyAttributes(Context context, AttributeSet attrs) {
        final int[] attrsArray = new int[]{
                android.R.attr.gravity
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);

        int gravity = ta.getInt(0, NO_ID);
        switch (gravity) {
            case Gravity.LEFT:
                execJavascript("RE.setTextAlign(\"left\")");
                break;
            case Gravity.RIGHT:
                execJavascript("RE.setTextAlign(\"right\")");
                break;
            case Gravity.TOP:
                execJavascript("RE.setVerticalAlign(\"top\")");
                break;
            case Gravity.BOTTOM:
                execJavascript("RE.setVerticalAlign(\"bottom\")");
                break;
            case Gravity.CENTER_VERTICAL:
                execJavascript("RE.setVerticalAlign(\"middle\")");
                break;
            case Gravity.CENTER_HORIZONTAL:
                execJavascript("RE.setTextAlign(\"center\")");
                break;
            case Gravity.CENTER:
                execJavascript("RE.setVerticalAlign(\"middle\")", false);
                execJavascript("RE.setTextAlign(\"center\")");
                break;
        }

        ta.recycle();
    }

    public EditableWebView setEditorFontColor(int color) {
        String hex = ColorUtils.colorToHexString(color);
        execJavascript("RE.setBaseTextColor('" + hex + "');");
        return this;
    }

    public EditableWebView setEditorFontSize(int px) {
        execJavascript("RE.setBaseFontSize('" + px + "px');");
        return this;
    }

    public EditableWebView setEditorPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        execJavascript("RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
                + "px');");
        return this;
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // still not support RTL.
        setPadding(start, top, end, bottom);
    }

    public EditableWebView setEditorBackgroundColor(int color) {
        setBackgroundColor(color);
        return this;
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resid) {
        Bitmap bitmap = Utils.decodeResource(getContext(), resid);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        execJavascript("RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    @Override
    public void setBackground(Drawable background) {
        Bitmap bitmap = Utils.toBitmap(background);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        execJavascript("RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    public void setBackground(String url) {
        execJavascript("RE.setBackgroundImage('url(" + url + ")');");
    }

    public EditableWebView setEditorWidth(int px) {
        execJavascript("RE.setWidth('" + px + "px');", false);
        return this;
    }

    public EditableWebView setEditorHeight(int px) {
        execJavascript("RE.setHeight('" + px + "px');", false);
        return this;
    }

    public void setPlaceholder(String placeholder) {
        execJavascript("RE.setPlaceholder('" + placeholder + "');", false);
    }

    public void setInputEnabled(Boolean inputEnabled) {
        execJavascript("RE.setInputEnabled(" + inputEnabled + ");", false);
        this.mIsEditMode = inputEnabled;
    }

    public void loadCSS(String cssFile) {
        execJavascript("RE.insertCSS(" + cssFile + ");", false);
    }

    public void undo() {
        execJavascript("RE.undo();");
    }

    public void redo() {
        execJavascript("RE.redo();");
    }

    public void setBold() {
        execJavascript("RE.setBold();");
    }

    public void setItalic() {
        execJavascript("RE.setItalic();");
    }

    public void setSubscript() {
        execJavascript("RE.setSubscript();");
    }

    public void setSuperscript() {
        execJavascript("RE.setSuperscript();");
    }

    public void setStrikeThrough() {
        execJavascript("RE.setStrikeThrough();");
    }

    public void setUnderline() {
        execJavascript("RE.setUnderline();");
    }

    public void setTextColor(int color) {
        execJavascript("RE.prepareInsert();", false);

        String hex = ColorUtils.colorToHexString(color);
        execJavascript("RE.setTextColor('" + hex + "');");
    }

    public void setTextBackgroundColor(int color) {
        execJavascript("RE.prepareInsert();", false);

        String hex = ColorUtils.colorToHexString(color);
        execJavascript("RE.setTextBackgroundColor('" + hex + "');");
    }

    public void setFontSize(int fontSize) {
        if (fontSize > 7 || fontSize < 1) {
            Log.e("RichEditor", "Font size should have a value between 1-7");
        }
        execJavascript("RE.setFontSize('" + fontSize + "');");
    }

    public void removeFormat() {
        execJavascript("RE.removeFormat();");
    }

    public void setHeading(int heading) {
        if (heading > 6 || heading < 1) {
            Log.e("RichEditor", "Heading should have a value between 1-6");
        }
        execJavascript("RE.setHeading('" + heading + "');");
    }

    public void setIndent() {
        execJavascript("RE.setIndent();");
    }

    public void setOutdent() {
        execJavascript("RE.setOutdent();");
    }

    public void setAlignLeft() {
        execJavascript("RE.setJustifyLeft();");
    }

    public void setAlignCenter() {
        execJavascript("RE.setJustifyCenter();");
    }

    public void setAlignRight() {
        execJavascript("RE.setJustifyRight();");
    }

    public void setBlockquote() {
        execJavascript("RE.setBlockquote();");
    }

    public void setCode() {
        execJavascript("RE.setCode();");
    }

    public void setBullets() {
        execJavascript("RE.setBullets();");
    }

    public void setNumbers() {
        execJavascript("RE.setNumbers();");
    }

    public void insertImage(String url, String alt) {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.insertImage('" + url + "', '" + alt + "');");
    }

    public void insertImage(String url, int width, int height) {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.insertImage('" + url + "', " + width + ", " + height + ");");
    }

    public void insertYoutubeVideo(String url) {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.insertYoutubeVideo('" + url + "');");
    }

    public void insertLink(String href, String title) {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.insertLink('" + href + "', '" + title + "');");
    }

    public void createLink(String href) {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.createLink(href);");
    }

    public void removeLink() {
//        execJavascript("RE.prepareInsert();");
        execJavascript("RE.removeLink();");
    }
    public void insertTodo(String item) {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.insertTodo('" + item + "');");
    }

    public void insertLine() {
        execJavascript("RE.prepareInsert();", false);
        execJavascript("RE.insertLine();");
    }

    public void focusEditor() {
        requestFocus();
        execJavascript("RE.focus();", false);
    }

    public void clearFocusEditor() {
        execJavascript("RE.clearFocus();", false);
    }

    public void clearAndFocusEditor() {
        execJavascript("RE.clearAndFocusEditor();", false);
    }

    public String getEditableHtml() {
        return mHtml;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public boolean isEditorJSLoaded() {
        return mIsEditorJSLoaded;
    }

    public boolean isHtmlRequestMade() {
        return mIsHtmlRequestMade;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (mScrollListener != null) {
            if (!canScrollVertically(1)) {
                mScrollListener.onScrolledToBottom();
            }
            else if (!canScrollVertically(-1)) {
                mScrollListener.onScrolledToTop();
            } else {
                // положительно - вниз
                // отрицательно - вверх
                int direction = y - oldY;
                if (Math.abs(direction) < 5) {
                    mScrollListener.onScrollEnd();
                } else {
                    mScrollListener.onScrolledVertical(direction);
                }
            }
        }
    }

    /**
     *
     * @param listener
     */
    public void setOnTextChangeListener(ITextChangeListener listener) {
        this.mTextChangeListener = listener;
    }

    public void setOnStateChangeListener(ITextStateListener listener) {
        this.mStateListener = listener;
    }

    public void setOnPageLoadListener(IPageLoadListener listener) {
        this.mPageListener = listener;
    }

    public void setOnUrlLoadListener(ILinkLoadListener listener) {
        mUrlLoadListener = listener;
    }

    public void setOnHtmlReceiveListener(IHtmlReceiveListener listener) {
        mReceiveHtmlListener = listener;
    }

    public void setYoutubeLoadLinkListener(IYoutubeLinkLoadListener listener) {
        this.mLoadYoutubeLinkListener = listener;
    }

    public void setScrollListener(IScrollListener listener) {
        this.mScrollListener = listener;
    }

    private void onPageStarted() {
        this.mIsEditorJSLoaded = false;
        this.mIsHtmlRequestMade = false;
        if (mPageListener != null) {
            mPageListener.onStartPageLoading();
        }
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        onPageStarted();
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
        onPageStarted();
    }

    @Override
    public void loadData(String data, @Nullable String mimeType, @Nullable String encoding) {
        this.mHtml = data;
        super.loadData(data, mimeType, encoding);
        onPageStarted();
    }

    @Override
    public void loadDataWithBaseURL(@Nullable String baseUrl, String data, @Nullable String mimeType, @Nullable String encoding, @Nullable String historyUrl) {
        this.mBaseUrl = baseUrl;
        this.mHtml = data;
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        onPageStarted();
    }

    /**
     *
     */
    protected class TetroidWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mPageListener != null) {
                mPageListener.onPageLoading(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    /**
     *
     */
    protected class EditorWebViewClient extends WebViewClient {

//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            EditableWebView.this.onPageStarted();
//            super.onPageStarted(view, url, favicon);
//        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            onPageLoaded();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (onUrlLoading(url))
                return true;
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
