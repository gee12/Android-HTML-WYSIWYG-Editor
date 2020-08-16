package com.lumyjuwon.richwysiwygeditor.RichEditor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
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

public final class Utils {

    private Utils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap decodeResource(@NotNull Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static byte[] readFileFromAssets(@NotNull Context context, String fileName) {
        byte[] buffer = null;
        try {
            InputStream input = context.getAssets().open(fileName);
            buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Запись текста в буфер обмена.
     * @param context
     * @param label
     * @param text
     */
    public static void writeToClipboard(Context context, String label, String text, String html) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            clip = ClipData.newHtmlText(label, text, html);
        } else {
            clip = ClipData.newPlainText(label, text);
        }
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Получение текста из буфера обмена.
     * FIXME: нужно у обычного текста заменять символы '\n' на <br>
     * @param context
     * @param isHtml
     * @param isPlainText
     * @return
     */
    public static String readFromClipboard(Context context, boolean isHtml/*, boolean isPlainText*/) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clipboard.hasPrimaryClip() && clip.getItemCount() > 0) {
            ClipData.Item item = clip.getItemAt(0);
            if (isHtml) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    return item.getHtmlText();
                } else {
                    return item.coerceToHtmlText(context);
                }
//            } else if (isPlainText) {
//                return item.getText().toString();
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    return item.coerceToHtmlText(context);
//                } else {
//                    return item.getText().toString();
//                }
            } else {
//                return item.coerceToText(context).toString();
                    return item.getText().toString();
            }
        }
        return null;
    }
}
