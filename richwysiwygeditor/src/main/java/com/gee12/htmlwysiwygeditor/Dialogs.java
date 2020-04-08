package com.gee12.htmlwysiwygeditor;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.lumyjuwon.richwysiwygeditor.R;
import com.lumyjuwon.richwysiwygeditor.WysiwygUtils.Keyboard;

import java.util.Locale;

public class Dialogs {

    public interface IApplyResult {
        void onApply();
    }

    public interface IApplyCancelResult {
        void onApply();
        void onCancel();
    }

    public interface ITextSizeResult {
        void onApply(int size);
    }

    public interface IInsertLinkResult {
        void onApply(String link, String title);
    }

    public interface IImageDimensResult {
        void onApply(int width, int height);
    }

    /**
     * Диалог ввода размера текста.
     * Значение должно быть в диапазоне 1-7.
     * @param context
     * @param handler
     */
    public static void createTextSizeDialog(Context context, int curSize, ITextSizeResult handler) {
        AskDialogBuilder builder = AskDialogBuilder.create(context, R.layout.dialog_text_size);

        EditText etSize = builder.getView().findViewById(R.id.edit_text_size);
        if (curSize >= 1 && curSize <= 7) {
            etSize.setText(String.format(Locale.getDefault(), "%d", curSize));
        }
        etSize.setSelection(0, etSize.getText().length());

        builder.setPositiveButton(R.string.answer_ok, (dialog1, which) -> {
            int size = Integer.parseInt(etSize.getText().toString());
            handler.onApply(size);
        }).setNegativeButton(R.string.answer_cancel, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog12 -> {
            // получаем okButton уже после вызова show()
            final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (TextUtils.isEmpty(etSize.getText().toString())) {
                okButton.setEnabled(false);
            }
//                Keyboard.showKeyboard(etSize);
            Keyboard.showKeyboard(builder.getView());
        });

        dialog.show();

        // получаем okButton тут отдельно после вызова show()
        final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        etSize.addTextChangedListener(new DimenTextWatcher(newText -> {
            if (TextUtils.isEmpty(newText)) {
                okButton.setEnabled(false);
            } else {
                int size = Integer.parseInt(etSize.getText().toString());
                okButton.setEnabled(size >= 1 && size <= 7);
            }
        }));
    }

    /**
     * Диалог ввода размера изображения.
     * Значение должно быть > 0.
     * @param context
     * @param handler
     */
    public static void createImageDimensDialog(Context context, int curWidth, int curHeight, IImageDimensResult handler) {
        AskDialogBuilder builder = AskDialogBuilder.create(context, R.layout.dialog_edit_image);

        EditText etWidth = builder.getView().findViewById(R.id.edit_text_width);
        if (curWidth > 0) {
            etWidth.setText(String.format(Locale.getDefault(), "%d", curWidth));
        }
        etWidth.setSelection(0, etWidth.getText().length());

        EditText etHeight = builder.getView().findViewById(R.id.edit_text_height);
        if (curHeight > 0) {
            etHeight.setText(String.format(Locale.getDefault(), "%d", curHeight));
        }
        builder.setPositiveButton(R.string.answer_ok, (dialog1, which) -> {
            int width = Integer.parseInt(etWidth.getText().toString());
            int height = Integer.parseInt(etHeight.getText().toString());
            handler.onApply(width, height);
        }).setNegativeButton(R.string.answer_cancel, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog12 -> {
            // получаем okButton уже после вызова show()
            final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (TextUtils.isEmpty(etWidth.getText().toString())
                || TextUtils.isEmpty(etHeight.getText().toString())) {
                okButton.setEnabled(false);
            }
//                Keyboard.showKeyboard(etWidth);
            Keyboard.showKeyboard(builder.getView());
        });

        dialog.show();

        // получаем okButton тут отдельно после вызова show()
        final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        etWidth.addTextChangedListener(new DimenTextWatcher(newText -> {
            if (TextUtils.isEmpty(newText)) {
                okButton.setEnabled(false);
            } else {
                int dimen = Integer.parseInt(newText);
                okButton.setEnabled(dimen > 0);
            }
        }));
    }

    /**
     * Диалог ввода url ссылки.
     * @param context
     * @param onlyLink
     * @param handler
     */
    public static void createInsertLinkDialog(Context context, boolean onlyLink, IInsertLinkResult handler) {
        AskDialogBuilder builder = AskDialogBuilder.create(context, R.layout.dialog_insert_link);
        EditText etLink = builder.getView().findViewById(R.id.edit_text_link);
        EditText etTitle = builder.getView().findViewById(R.id.edit_text_title);
        if (onlyLink) {
            etTitle.setVisibility(View.GONE);
        }
        builder.setPositiveButton(R.string.answer_ok, (dialog1, which) ->
                handler.onApply(etLink.getText().toString(), etTitle.getText().toString()))
            .setNegativeButton(R.string.answer_cancel, null)
            .create().show();
    }

    /**
     * AlertDialog с методом getView().
     */
    public static class AskDialogBuilder extends AlertDialog.Builder {

        private View mView;

        public AskDialogBuilder(@NonNull Context context) {
            super(context);
        }

        public AskDialogBuilder(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        @Override
        public AlertDialog.Builder setView(View view) {
            this.mView = view;
            return super.setView(view);
        }

        public View getView() {
            return mView;
        }

        public static AskDialogBuilder create(Context context, int layoutResId) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            LayoutInflater inflater = LayoutInflater.from(context);
            AskDialogBuilder dialogBuilder = new AskDialogBuilder(context);
            View dialogView = inflater.inflate(layoutResId, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(true);
            return dialogBuilder;
        }
    }

    interface ITextChanged {
        void onTextChanged(String newText);
    }

    /**
     *
     */
    static class DimenTextWatcher implements TextWatcher {

        ITextChanged mTextChangedCallback;

        public DimenTextWatcher(ITextChanged callback) {
            this.mTextChangedCallback = callback;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTextChangedCallback != null) {
                mTextChangedCallback.onTextChanged(s.toString());
            }
        }
    }
}
