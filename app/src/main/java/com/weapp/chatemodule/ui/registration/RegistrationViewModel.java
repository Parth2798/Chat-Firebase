package com.weapp.chatemodule.ui.registration;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.ui.main.MainActivity;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.SessionManager;

import javax.inject.Inject;

public class RegistrationViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;

    @Inject
    public RegistrationViewModel(SessionManager sessionManager, AuthUtils utils) {
        this.sessionManager = sessionManager;
        this.authUtils = utils;
    }

    public void setObserve(RegistrationActivity registrationActivity) {
        sessionManager.isLogin().observe(registrationActivity, aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    sendIntent(registrationActivity, MainActivity.class);
                    registrationActivity.finishAffinity();
                }
            }
        });
    }

    public void signUp(RegistrationActivity registrationActivity, User user) {
        authUtils.createUserWithEmail(registrationActivity, user);
    }

    public void sendIntent(RegistrationActivity registrationActivity, Class<?> aClass) {
        registrationActivity.startActivity(new Intent(registrationActivity, aClass));
    }

    public void signUpWithPhone(RegistrationActivity registrationActivity, String user, ClickCallbacks clickCallbacks) {
        authUtils.createUserWithPhone(registrationActivity, user, clickCallbacks);
    }

    public void verifyOTP(User user, Activity activity, PhoneAuthCredential credential) {
        authUtils.signInWithPhoneAuthCredential(user, activity, credential);
    }
}