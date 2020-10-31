package com.lumyjuwon.richwysiwygeditor.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class Keyboard {

    public static void closeKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showSoftKeyboard(Dialog dialog){
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     *
     * @param context
     * @param rootView
     */
    public static void toggleClipboard(Context context, View rootView) {
        if (rootView == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (isClipboardShowed(rootView)) {
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } else {
            imm.showSoftInput(rootView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     *
     * @param rootView
     * @return
     */
    public static boolean isClipboardShowed(View rootView) {
        Rect r = new Rect();
        //r will be populated with the coordinates of your rootView that area still visible.
        rootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = rootView.getRootView().getHeight() - r.height();
        // if more than 25% of the screen, its probably a keyboard...
        return (heightDiff > 0.25 * rootView.getRootView().getHeight());
    }
}
