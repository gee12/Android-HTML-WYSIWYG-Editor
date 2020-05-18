package com.gee12.htmlwysiwygeditor;

import android.text.Editable;
import android.text.TextWatcher;

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
}
