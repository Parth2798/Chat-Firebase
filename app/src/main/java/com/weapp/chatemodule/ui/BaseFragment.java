package com.weapp.chatemodule.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.weapp.chatemodule.dialogs.ProgressBarDialog;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.PreferencesClass;
import com.weapp.chatemodule.util.SessionManager;
import com.weapp.chatemodule.viewmodels.ViewModelProviderFactory;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public abstract class BaseFragment<T extends ViewDataBinding> extends DaggerFragment {

    public void getUser(User user){}

    public AppCompatActivity activity;

    public T binding;

    protected T getViewDataBinding() {
        return binding;
    }

    @LayoutRes
    protected abstract int getLayoutId();

    private ProgressBarDialog progressBarDialog;

    @Inject
    public PreferencesClass preferencesClass;

    @Inject
    public SessionManager sessionManager;

    @Inject
    public ViewModelProviderFactory factory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();

        setObserve();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    private void setObserve() {
        sessionManager.getUser().observe(this, userAuthResource -> {
            if (userAuthResource != null) {
                switch (userAuthResource.status) {
                    case AUTHENTICATED:
                        if (userAuthResource.data != null) {
                            getUser(userAuthResource.data);
                        }

                        break;
                    case ERROR:
                        Toast.makeText(activity, userAuthResource.message, Toast.LENGTH_SHORT).show();
                        break;

                    case LOADING:
                        break;

                    case NOT_AUTHENTICATED:
                        break;
                }
            }
        });

        progressBarDialog = new ProgressBarDialog(activity);
    }

    public void isShowPg(boolean visible) {
        if (visible) {
            progressBarDialog.openDialog();
        } else {
            progressBarDialog.finishDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}