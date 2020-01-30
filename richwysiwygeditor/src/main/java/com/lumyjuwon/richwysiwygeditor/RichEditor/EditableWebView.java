package com.lumyjuwon.richwysiwygeditor.RichEditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.Youtube;

import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public enum Type {
        BOLD,
        ITALIC,
        SUBSCRIPT,
        SUPERSCRIPT,
        STRIKETHROUGH,
        UNDERLINE,
        H1,
        H2,
        H3,
        H4,
        H5,
        H6,
        ORDEREDLIST,
        UNORDEREDLIST,
        JUSTIFYCENTER,
        JUSTIFYFULL,
        JUSTUFYLEFT,
        JUSTIFYRIGHT,
        BACKGROUND_COLOR_WHITE(255, 255, 255),
        BACKGROUND_COLOR_BLACK(0, 0, 0),
        BACKGROUND_COLOR_MAROON(128, 0, 0),
        BACKGROUND_COLOR_RED(255, 0, 0),
        BACKGROUND_COLOR_MAGENTA(255, 0, 255),
        BACKGROUND_COLOR_PINK(255, 153, 204),
        BACKGROUND_COLOR_ORANGE(255, 102, 0),
        BACKGROUND_COLOR_YELLOW(255, 255, 0),
        BACKGROUND_COLOR_LIME(0, 255, 0),
        BACKGROUND_COLOR_AQUA(0, 255, 255),
        BACKGROUND_COLOR_BLUE(0, 0, 255),
        BACKGROUND_COLOR_SKY_BLUE(0, 204, 255),
        BACKGROUND_COLOR_PALE_CYAN(204, 255, 255),
        BACKGROUND_COLOR_GREEN(0, 128, 0),
        FONT_COLOR_WHITE(255, 255, 255),
        FONT_COLOR_BLACK(0, 0, 0),
        FONT_COLOR_MAROON(128, 0, 0),
        FONT_COLOR_RED(255, 0, 0),
        FONT_COLOR_MAGENTA(255, 0, 255),
        FONT_COLOR_PINK(255, 153, 204),
        FONT_COLOR_ORANGE(255, 102, 0),
        FONT_COLOR_YELLOW(255, 255, 0),
        FONT_COLOR_LIME(0, 255, 0),
        FONT_COLOR_AQUA(0, 255, 255),
        FONT_COLOR_BLUE(0, 0, 255),
        FONT_COLOR_SKY_BLUE(0, 204, 255),
        FONT_COLOR_PALE_CYAN(204, 255, 255),
        FONT_COLOR_GREEN(0, 128, 0);


        private int r;
        private int g;
        private int b;

        Type(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        Type() {
            this(-1, -1, -1);
        }
    }


    public interface ITextChangeListener {

        void onTextChange(String text);
    }

    public interface IDecorationStateListener {

        void onStateChangeListener(String text, List<Type> types);
    }

    public interface IPageLoadListener {

        void onPageStartLoading();
        void onPageLoaded();
    }

    public interface IUrlLoadListener {

        boolean onUrlLoad(String url);
    }

    public interface IYoutubeLoadLinkListener {
        void onReceivedEvent(String videoId);
    }

    public static final int EXEC_TRY_DELAY_MSEC = 100;
    public static final String EDITOR_JS_FILE = "editor.js";
    private static final String CALLBACK_SCHEME = "re-callback://";
    private static final String STATE_SCHEME = "re-state://";
    private static final String CALLBACK_STATE_SCHEME_PATTERN = "("+CALLBACK_SCHEME+".*)("+STATE_SCHEME+".*)";

    private boolean isPageLoaded = false;
    private String mHtml;
    private ITextChangeListener mTextChangeListener;
    private IDecorationStateListener mDecorationStateListener;
    private IPageLoadListener mPageListener;
    private IUrlLoadListener mUrlLoadListener;
    private IYoutubeLoadLinkListener mLoadYoutubeLinkListener;

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
    }

    public void onPageLoaded() {
        // load main javascript code
        loadEditorScript();
        // send first javascript request to receive page html
        makeHtmlRequest();

        EditableWebView.this.isPageLoaded = true;

        if (mPageListener != null) {
            mPageListener.onPageLoaded();
        }
    }

    public Boolean onUrlLoading(String url) {
//            if (!isEditMode) {
//                if (mUrlLoadListener != null)
//                    mUrlLoadListener.onUrlLoad(url);
//                return true;
//            }
        String decode;
        String re_callback = "";
        String re_state = "";
        boolean isRegexFound;
        try {
            decode = URLDecoder.decode(url, "UTF-8");
            Pattern p;
            Matcher m;
            p = Pattern.compile(CALLBACK_STATE_SCHEME_PATTERN);
            m = p.matcher(decode);
            isRegexFound = m.find();
            if (isRegexFound) {
                re_callback = m.group(1);
                re_state = m.group(2);
            }
        } catch (UnsupportedEncodingException e) {
            // No handling
            return false;
        }

        // User clicks the link that is youtube then post video id.
        if (!Youtube.getVideoId(url).equals("error")) {
            String videoid = Youtube.getVideoId(url);
            if (!videoid.equals("error")) {
                if (mLoadYoutubeLinkListener != null) {
                    mLoadYoutubeLinkListener.onReceivedEvent(videoid);
                }
            }
            return true;
        } else if (isRegexFound) {
            callback(re_callback);
            stateCheck(re_state);
            return true;
        } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
            stateCheck(decode);
            return true;
        } else if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
            callback(decode);
            return true;
        } else if (mUrlLoadListener != null)
            return mUrlLoadListener.onUrlLoad(url);
        return null;
    }

    private void callback(String text) {
//        this.mHtml = text.replaceFirst(CALLBACK_SCHEME, "");
        this.mHtml = text.replaceFirst(CALLBACK_SCHEME, "").replaceFirst(STATE_SCHEME+".*$", "");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(mHtml);
        }
    }

    protected void exec(final String trigger) {
        if (isPageLoaded) {
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

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    public void loadEditorScript() {
        byte[] buffer = Utils.readFileFromAssets(getContext(), EDITOR_JS_FILE);
        if (buffer != null) {
            load("javascript:" + new String(buffer));
        }
    }

    /**
     * FIXME:
     * Результат в onReceiveValue() возврашается:
     * 1) в формате unescape
     * 2) обрамленный ненужными кавычками
     * Как вернуть html с помощью Javascript без этих наворотов ?
     *
     */
    @JavascriptInterface
    public void makeHtmlRequest() {
//        addJavascriptInterface(new JavascriptHandler(), "AndroidFunction");
//        load("javascript: AndroidFunction.onPageLoaded(document.body.innerHTML);");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("(function() { return document.body.innerHTML; })();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // delete unescape literals
                    String html = StringEscapeUtils.unescapeJava(value);
                    if (html != null && html.length() > 3) {
                        // delete quotes
                        html = html.substring(1, html.length() - 1);
                    }
                    EditableWebView.this.mHtml = html;
                }
            });
        }
    }

    public class JavascriptHandler {
        JavascriptHandler() { }

        @JavascriptInterface
        public void onPageLoaded(String html) {
            EditableWebView.this.mHtml = html;
        }
    }

    /**
     *
     * @param text
     */
    private void stateCheck(String text) {
        String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ENGLISH);
        List<Type> types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (type.r == -1) {
                if (TextUtils.indexOf(state, type.name()) != -1) {
                    types.add(type);
                }
            } else {
                if (type.name().contains("FONT_COLOR")) {
                    String color = "FONT_COLOR_RGB(" + type.r + ", " + type.g + ", " + type.b + ")";
                    if (TextUtils.indexOf(state, color) != -1) {
                        types.add(type);
                    }
                } else if (type.name().contains("BACKGROUND_COLOR")) {
                    String color = "BACKGROUND_COLOR_RGB(" + type.r + ", " + type.g + ", " + type.b + ")";
                    if (TextUtils.indexOf(state, color) != -1) {
                        types.add(type);
                    }
                }
            }
        }

        if (mDecorationStateListener != null) {
            mDecorationStateListener.onStateChangeListener(state, types);
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

//    public void setHtml(String contents) {
//        if (contents == null) {
//            contents = "";
//        }
//        try {
//            exec("javascript:RE.setHtml('" + URLEncoder.encode(contents, "UTF-8") + "');");
//        } catch (UnsupportedEncodingException e) {
//            // No handling
//        }
//        mHtml = contents;
//    }

//    public String getHtml() {
//        return mHtml;
//    }

    public EditableWebView setEditorFontColor(int color) {
        String hex = convertHexColorString(color);
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

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextColor('" + hex + "');");
    }

    public void setTextBackgroundColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
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

    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime() + "');");
    }

    public void focusEditor() {
        requestFocus();
        exec("javascript:RE.focus();");
    }

    public void clearFocusEditor() {
        exec("javascript:RE.blurFocus();");
    }

    public void clearAndFocusEditor() {
        exec("javascript:RE.clearAndFocusEditor();");
    }

    private String convertHexColorString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
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

    public void setOnDecorationChangeListener(IDecorationStateListener listener) {
        this.mDecorationStateListener = listener;
    }

    public void setOnPageLoadListener(IPageLoadListener listener) {
        this.mPageListener = listener;
    }

    public void setOnUrlLoadListener(IUrlLoadListener listener) {
        mUrlLoadListener = listener;
    }

    public void setYoutubeLoadLinkListener(IYoutubeLoadLinkListener listener) {
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
