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
import android.webkit.WebChromeClient;
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

    public interface ITextChangeListener {

        void onTextChange(String text);
    }

    public interface ITextStateListener {

        void onStateChange(String text, Map<ActionType, String> types);
    }

    public interface IPageLoadListener {

        void onPageStartLoading();
        void onPageLoaded();
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

    /**
     * Class with methods to be called from javascript page.
     */
    public class JavascriptInterface {

        JavascriptInterface() { }

        @android.webkit.JavascriptInterface
        public void textChange(/*String text, */String html) {
            EditableWebView.this.onTextChanged(/*text, */html);
        }

        @android.webkit.JavascriptInterface
        public void stateChange(String formatsAsQuery, String selectedText) {
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
    private boolean mIsEditMode = false;
    private String mHtml;
    // listeners
    private ITextChangeListener mTextChangeListener;
    private ITextStateListener mStateListener;
    private IPageLoadListener mPageListener;
    private ILinkLoadListener mUrlLoadListener;
    private IHtmlReceiveListener mReceiveHtmlListener;
    private IYoutubeLinkLoadListener mLoadYoutubeLinkListener;

    public EditableWebView(Context context) {
        this(context, null);
    }

    public EditableWebView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public EditableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);

        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new EditorWebViewClient());
        applyAttributes(context, attrs);
        addJavascriptInterface(new JavascriptInterface(), "Android");
    }

    /**
     * Page load event handler (caused by WebView).
     */
    public void onPageLoaded() {
        // load main javascript code
        loadEditorScript();
        // send first javascript request to receive page html
//        if (!mIsPageLoaded) {
            makeEditableHtmlRequest();
//        }
        mIsPageLoaded = true;

        if (mPageListener != null) {
            mPageListener.onPageLoaded();
        }
    }

    public void loadEditorScript() {
        byte[] buffer = Utils.readFileFromAssets(getContext(), EDITOR_JS_FILE);
        if (buffer != null) {
            load("javascript:" + new String(buffer));
        }
    }

    /**
     * Запрос на получение html-текста редактируемого фрагмента страницы.
     */
    public void makeEditableHtmlRequest() {
            load("javascript: Android.receiveHtml(RE.editor.innerHTML);");
    }

    private void onReceiveEditableHtml(String html) {
        // Javascript always returned html-text with excess line break ('\n') in the end,
        // remove it
        if (html.length() >= 2) {
            html = html.substring(0, html.length()-2);
        }
        EditableWebView.this.mHtml = html;
        if (mReceiveHtmlListener != null)
            mReceiveHtmlListener.onReceiveEditableHtml(html);
    }

/*    @JavascriptInterface
    public void makeEditableHtmlRequest() {
        //
        if (!TextUtils.isEmpty(mHtml)) {
            onReceiveEditableHtml(mHtml);
            return;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            evaluateJavascript("(function() { return document.body.innerHTML; })();",
                    new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // delete unescape literals
                    String html = StringEscapeUtils.unescapeJava(value);
                    if (html != null && html.length() > 3) {
                        // delete quotes
                        html = html.substring(1, html.length() - 1);
                    }
                    onReceiveEditableHtml(html);
                }
            });
        } else *//*if (Build.VERSION.SDK_INT <= 17)*//* {
            //
            addJavascriptInterface(new JavascriptHandler(), "AndroidFunction");
            load("javascript: AndroidFunction.onPageLoaded(document.body.innerHTML);");
        }
    }*/

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
    protected void exec(final String trigger) {
        if (mIsPageLoaded) {
            load(trigger);
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
    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
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
                exec("javascript:RE.setTextAlign(\"left\")");
                break;
            case Gravity.RIGHT:
                exec("javascript:RE.setTextAlign(\"right\")");
                break;
            case Gravity.TOP:
                exec("javascript:RE.setVerticalAlign(\"top\")");
                break;
            case Gravity.BOTTOM:
                exec("javascript:RE.setVerticalAlign(\"bottom\")");
                break;
            case Gravity.CENTER_VERTICAL:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                break;
            case Gravity.CENTER_HORIZONTAL:
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
            case Gravity.CENTER:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
        }

        ta.recycle();
    }

    public EditableWebView setEditorFontColor(int color) {
        String hex = ColorUtils.colorToHexString(color);
        exec("javascript:RE.setBaseTextColor('" + hex + "');");
        return this;
    }

    public EditableWebView setEditorFontSize(int px) {
        exec("javascript:RE.setBaseFontSize('" + px + "px');");
        return this;
    }

    public EditableWebView setEditorPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        exec("javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
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

        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    @Override
    public void setBackground(Drawable background) {
        Bitmap bitmap = Utils.toBitmap(background);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    public void setBackground(String url) {
        exec("javascript:RE.setBackgroundImage('url(" + url + ")');");
    }

    public EditableWebView setEditorWidth(int px) {
        exec("javascript:RE.setWidth('" + px + "px');");
        return this;
    }

    public EditableWebView setEditorHeight(int px) {
        exec("javascript:RE.setHeight('" + px + "px');");
        return this;
    }

    public void setPlaceholder(String placeholder) {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    public void setInputEnabled(Boolean inputEnabled) {
        exec("javascript:RE.setInputEnabled(" + inputEnabled + ")");
        this.mIsEditMode = inputEnabled;
    }

    public void loadCSS(String cssFile) {
//        String jsCSSImport = "(function() {" +
//                "    var head  = document.getElementsByTagName(\"head\")[0];" +
//                "    var link  = document.createElement(\"link\");" +
//                "    link.rel  = \"stylesheet\";" +
//                "    link.type = \"text/css\";" +
//                "    link.href = \"" + cssFile + "\";" +
//                "    link.media = \"all\";" +
//                "    head.appendChild(link);" +
//                "}) ();";
//        exec("javascript:" + jsCSSImport + "");
        exec("javascript:RE.insertCSS(" + cssFile + ")");
    }

    public void undo() {
        exec("javascript:RE.undo();");
    }

    public void redo() {
        exec("javascript:RE.redo();");
    }

    public void setBold() {
        exec("javascript:RE.setBold();");
    }

    public void setItalic() {
        exec("javascript:RE.setItalic();");
    }

    public void setSubscript() {
        exec("javascript:RE.setSubscript();");
    }

    public void setSuperscript() {
        exec("javascript:RE.setSuperscript();");
    }

    public void setStrikeThrough() {
        exec("javascript:RE.setStrikeThrough();");
    }

    public void setUnderline() {
        exec("javascript:RE.setUnderline();");
    }

    public void setTextColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = ColorUtils.colorToHexString(color);
        exec("javascript:RE.setTextColor('" + hex + "');");
    }

    public void setTextBackgroundColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = ColorUtils.colorToHexString(color);
        exec("javascript:RE.setTextBackgroundColor('" + hex + "');");
    }

    public void setFontSize(int fontSize) {
        if (fontSize > 7 || fontSize < 1) {
            Log.e("RichEditor", "Font size should have a value between 1-7");
        }
        exec("javascript:RE.setFontSize('" + fontSize + "');");
    }

    public void removeFormat() {
        exec("javascript:RE.removeFormat();");
    }

    public void setHeading(int heading) {
        if (heading > 6 || heading < 1) {
            Log.e("RichEditor", "Heading should have a value between 1-6");
        }
        exec("javascript:RE.setHeading('" + heading + "');");
    }

    public void setIndent() {
        exec("javascript:RE.setIndent();");
    }

    public void setOutdent() {
        exec("javascript:RE.setOutdent();");
    }

    public void setAlignLeft() {
        exec("javascript:RE.setJustifyLeft();");
    }

    public void setAlignCenter() {
        exec("javascript:RE.setJustifyCenter();");
    }

    public void setAlignRight() {
        exec("javascript:RE.setJustifyRight();");
    }

    public void setBlockquote() {
        exec("javascript:RE.setBlockquote();");
    }

    public void setCode() {
        exec("javascript:RE.setCode();");
    }

    public void setBullets() {
        exec("javascript:RE.setBullets();");
    }

    public void setNumbers() {
        exec("javascript:RE.setNumbers();");
    }

    public void insertImage(String url, String alt) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImage('" + url + "', '" + alt + "');");
    }

    public void insertYoutubeVideo(String url) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertYoutubeVideo('" + url + "');");
    }

    public void insertLink(String href, String title) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertLink('" + href + "', '" + title + "');");
    }

    public void createLink(String href) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.createLink(href);");
    }

    public void removeLink() {
//        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.removeLink();");
    }
    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime() + "');");
    }

    public void insertLine() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertLine();");
    }

    public void focusEditor() {
        requestFocus();
        exec("javascript:RE.focus();");
    }

    public void clearFocusEditor() {
        exec("javascript:RE.clearFocus();");
    }

    public void clearAndFocusEditor() {
        exec("javascript:RE.clearAndFocusEditor();");
    }

    public String getEditableHtml() {
        return mHtml;
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

    private void onPageLoading() {
        if (mPageListener != null)
            mPageListener.onPageStartLoading();
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        onPageLoading();
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
        onPageLoading();
    }

    @Override
    public void loadData(String data, @Nullable String mimeType, @Nullable String encoding) {
        super.loadData(data, mimeType, encoding);
        onPageLoading();
    }

    @Override
    public void loadDataWithBaseURL(@Nullable String baseUrl, String data, @Nullable String mimeType, @Nullable String encoding, @Nullable String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        onPageLoading();
    }

    /**
     *
     */
    protected class EditorWebViewClient extends WebViewClient {

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
