package com.gee12.htmlwysiwygeditor;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.lumyjuwon.richwysiwygeditor.R;

import java.util.Locale;

public class Dialogs {

    public interface IApplyResult {
        void onApply();
    }

    public interface IApplyCancelResult extends IApplyResult {
        void onCancel();
    }

    public interface ITextSizeResult {
        void onApply(int size);
    }

    public interface IInsertLinkResult {
        void onApply(String link, String title);
    }

    public interface IImageDimensResult {
        void onApply(int width, int height, boolean setSimilar);
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

        builder.setPositiveButton(R.string.answer_ok, (dialog1, which) -> {
//            int size = Integer.parseInt(etSize.getText().toString());
            String s = etSize.getText().toString();
            Integer size = parseInt(s);
            if (size != null) {
                handler.onApply(size);
            } else {
                Toast.makeText(context, context.getString(R.string.invalid_number) + s, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(R.string.answer_cancel, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog12 -> {
            // получаем okButton уже после вызова show()
            final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (TextUtils.isEmpty(etSize.getText().toString())) {
                okButton.setEnabled(false);
            }
            etSize.setSelection(etSize.getText().length());
//            Keyboard.showKeyboard(etSize);
        });

        dialog.show();

        // получаем okButton тут отдельно после вызова show()
        final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        etSize.addTextChangedListener(new DimenTextWatcher(newText -> {
//            if (TextUtils.isEmpty(newText)) {
//                okButton.setEnabled(false);
//            } else {
//                int size = Integer.parseInt(etSize.getText().toString());
//                okButton.setEnabled(size >= 1 && size <= 7);
                Integer size = parseInt(etSize.getText().toString());
                okButton.setEnabled(size != null && size >= 1 && size <= 7);
//            }
        }));
    }

    /**
     * Диалог ввода размера изображения.
     * Значение должно быть > 0.
     * @param context
     * @param handler
     */
    public static void createImageDimensDialog(Context context, int curWidth, int curHeight, boolean isSeveral,
                                               IImageDimensResult handler) {
        AskDialogBuilder builder = AskDialogBuilder.create(context, R.layout.dialog_edit_image);

        EditText etWidth = builder.getView().findViewById(R.id.edit_text_width);
        if (curWidth > 0) {
            etWidth.setText(String.format(Locale.getDefault(), "%d", curWidth));
        }

        EditText etHeight = builder.getView().findViewById(R.id.edit_text_height);
        if (curHeight > 0) {
            etHeight.setText(String.format(Locale.getDefault(), "%d", curHeight));
        }

        CheckBox checkBox = builder.getView().findViewById(R.id.check_box_similar);
        checkBox.setVisibility((isSeveral) ? View.VISIBLE : View.GONE);

        builder.setPositiveButton(R.string.answer_ok, (dialog1, which) -> {
            String s = etWidth.getText().toString();
            Integer width = parseInt(s);
            if (width != null) {
                s = etHeight.getText().toString();
                Integer height = parseInt(s);
                if (height != null) {
                    boolean setSimilarParams = checkBox.isChecked();
                    handler.onApply(width, height, setSimilarParams);
                } else {
                    Toast.makeText(context, context.getString(R.string.invalid_number) + s, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.invalid_number) + s, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(R.string.answer_cancel, null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog12 -> {
            // получаем okButton уже после вызова show()
            final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (TextUtils.isEmpty(etWidth.getText().toString())
                || TextUtils.isEmpty(etHeight.getText().toString())) {
                okButton.setEnabled(false);
            }
            etWidth.setSelection(etWidth.getText().length());
//            Keyboard.showKeyboard(etWidth);
        });

        dialog.show();

        // получаем okButton тут отдельно после вызова show()
        final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        DimenTextWatcher textWatcher = new DimenTextWatcher(newText -> {
//            if (TextUtils.isEmpty(newText)) {
//                okButton.setEnabled(false);
//            } else {
//                int dimen = Integer.parseInt(newText);
//                okButton.setEnabled(dimen > 0);
//            }
            Integer size = parseInt(newText);
            okButton.setEnabled(size != null && size > 0);
        });
        etWidth.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
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

    public static Integer parseInt(String s) {
        if (s == null || s.length() == 0)
            return null;
        try {
            int res = Integer.parseInt(s);
            return res;
        } catch(Exception ignored) {}
        return null;
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

    public static void showAlertDialog(Context context, int messageRes,
                                       DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListerener) {
        showAlertDialog(context, context.getString(messageRes), yesListener, noListerener);
    }

    public static void showAlertDialog(Context context, String message,
                                       DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListerener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(R.string.answer_yes, yesListener)
                .setNegativeButton(R.string.answer_no, noListerener).show();
    }

}
