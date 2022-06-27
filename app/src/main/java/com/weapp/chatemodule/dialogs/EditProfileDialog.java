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
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.databinding.EditProfileDialogBinding;
import com.weapp.chatemodule.models.User;

import java.util.Objects;

public class EditProfileDialog extends AppCompatDialog {

    private Context activity;
    private ClickCallbacks commonBtnClick;

    private EditProfileDialogBinding binding;
    private User user;

    public EditProfileDialog(Context context, User user, ClickCallbacks commonBtnClick) {
        super(context, R.style.AppThemeDialog);
        this.activity = context;
        this.commonBtnClick = commonBtnClick;
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.dialogAnimation;
        }

        binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.edit_profile_dialog, null, false);
        setContentView(binding.getRoot());
        setCancelable(true);

        binding.etfUserName.setText(user.getfName());
        binding.etlUserName.setText(user.getlName());
        binding.etlMobile.setText(user.getMobile());

        binding.ok.setOnClickListener(v -> binding.btnSignUp.performClick());
        binding.btnSignUp.setOnClickListener(v -> {

            user.setfName(Objects.requireNonNull(binding.etfUserName.getText()).toString());
            user.setlName(Objects.requireNonNull(binding.etlUserName.getText()).toString());
            user.setMobile(Objects.requireNonNull(binding.etlMobile.getText()).toString());
            commonBtnClick.getCallback(v, user);
        });
    }

    public void finishDialog() {
        dismiss();
    }

    public void openDialog() {
        finishDialog();
        if (!((AppCompatActivity) activity).isFinishing() && !isShowing() && getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            show();
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }
}