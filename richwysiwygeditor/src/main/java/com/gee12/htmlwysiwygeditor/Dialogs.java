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
//        void onCancel();
    }

    public interface IInsertLinkResult {
        void onApply(String link, String title);
//        void onCancel();
    }

    /**
     *
     * @param context
     * @param handler
     */
    public static void createTextSizeDialog(Context context, ITextSizeResult handler) {
        AskDialogBuilder builder = AskDialogBuilder.create(context, R.layout.dialog_text_size);

        EditText etSize = builder.getView().findViewById(R.id.edit_text_size);
        builder.setPositiveButton(R.string.answer_ok, (dialog1, which) -> {
            int size = Integer.parseInt(etSize.getText().toString());
            handler.onApply(size);
        }).setNegativeButton(R.string.answer_cancel, null);

        final AlertDialog dialog = builder.create();
        dialog.show();
        final Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        dialog.setOnShowListener(dialog12 -> okButton.setEnabled(false));

        etSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    okButton.setEnabled(false);
                } else {
                    int size = Integer.parseInt(etSize.getText().toString());
                    okButton.setEnabled(size >= 1 && size <= 7);
                }
            }
        });
    }

    /**
     *
     * @param context
     * @param onlyLink
     * @param handler
     */
    public static void createInsertLinkDialog(Context context, boolean onlyLink, IInsertLinkResult handler) {
        AskDialogBuilder builder = AskDialogBuilder.create(context, R.layout.dialog_insert_link);
        EditText etLink = builder.getView().findViewById(R.id.edit_text_link);
        EditText etTitle = builder.getView().findViewById(R.id.edit_text_title);
        if (onlyLink)
            etTitle.setVisibility(View.GONE);
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
}
