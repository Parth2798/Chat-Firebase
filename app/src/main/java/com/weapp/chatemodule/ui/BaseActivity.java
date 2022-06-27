package com.weapp.chatemodule.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.weapp.chatemodule.dialogs.ProgressBarDialog;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.ui.login.LoginActivity;
import com.weapp.chatemodule.util.PreferencesClass;
import com.weapp.chatemodule.util.SessionManager;
import com.weapp.chatemodule.viewmodels.ViewModelProviderFactory;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity<T extends ViewDataBinding> extends DaggerAppCompatActivity {

    protected Activity activity;

    protected T binding;

    @LayoutRes
    protected abstract int getLayoutId();

    @Inject
    public SessionManager sessionManager;

    @Inject
    public ViewModelProviderFactory factory;

    @Inject
    public PreferencesClass preferencesClass;

    public void getUser(User user){}

    private ProgressBarDialog progressBarDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, getLayoutId());

        setObserve();
    }

    private void setObserve() {
        sessionManager.getUser().observe(this, userAuthResource -> {
            if (userAuthResource != null) {
                switch (userAuthResource.status) {
                    case AUTHENTICATED:
                        if (userAuthResource.data != null) {
                            isShowPg(false);
                            preferencesClass.setEmail(userAuthResource.data.getEmail());
                            preferencesClass.setPassword(userAuthResource.data.getPassword());
                            preferencesClass.setUserID(userAuthResource.data.getUserId());

                            getUser(userAuthResource.data);
                        }

                        break;
                    case ERROR:
                        isShowPg(false);
                        if (userAuthResource.message != null && !userAuthResource.message.isEmpty()) {
                            Toast.makeText(activity, userAuthResource.message, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case LOADING:
                        isShowPg(true);
                        break;

                    case NOT_AUTHENTICATED:
                        isShowPg(false);

                        preferencesClass.clearData();

                        sessionManager.setIsLogin(false);
                        sessionManager.getUser().setValue(null);

                        startActivity(new Intent(activity, LoginActivity.class));
                        finishAffinity();

                        break;
                }
            }
        });

        progressBarDialog = new ProgressBarDialog(activity);
    }

    protected void isShowPg(boolean visible) {
        if (visible) {
            progressBarDialog.openDialog();
        } else {
            progressBarDialog.finishDialog();
        }
    }
}