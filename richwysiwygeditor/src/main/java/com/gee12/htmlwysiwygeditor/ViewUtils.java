package com.gee12.htmlwysiwygeditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ViewUtils {

    /**
     *
     */
    public interface ITextChanged {
        void onTextChanged(String newText);
    }

    /**
     *
     */
    public static class TextChangedListener implements TextWatcher {

        ITextChanged callback;

        public TextChangedListener(ITextChanged callback) {
            this.callback = callback;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (callback != null) {
                callback.onTextChanged(s.toString());
            }
        }
    }

    /**
     *
     * @param context
     * @param rootView
     */
    public static void toggleClipboard(Context context, View rootView) {
        if (rootView == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Rect r = new Rect();
        //r will be populated with the coordinates of your rootView that area still visible.
        rootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = rootView.getRootView().getHeight() - r.height();
        // if more than 25% of the screen, its probably a keyboard...
        if (heightDiff > 0.25 * rootView.getRootView().getHeight()) {
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } else {
            imm.showSoftInput(rootView, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
