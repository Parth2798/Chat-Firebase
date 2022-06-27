package com.weapp.chatemodule.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;

import com.weapp.chatemodule.R;
import com.weapp.chatemodule.databinding.ProgrssBarDailogBinding;

public class ProgressBarDialog extends AppCompatDialog {

    private Context context;

    public ProgressBarDialog(Context context) {
        super(context, R.style.AppThemeDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.dialogAnimation;
        }

        ProgrssBarDailogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.progrss_bar_dailog, null, false);
        setContentView(binding.getRoot());
        setCancelable(false);
    }

    public void finishDialog() {
        if (isShowing()) {
            dismiss();
        }
    }

    public void openDialog() {
        finishDialog();
        if (!((AppCompatActivity) context).isFinishing() && !isShowing() && getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            show();
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }
}
