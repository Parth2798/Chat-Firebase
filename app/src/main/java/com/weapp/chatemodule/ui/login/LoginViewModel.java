package com.weapp.chatemodule.ui.login;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.ui.main.MainActivity;
import com.weapp.chatemodule.ui.registration.RegistrationActivity;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.SessionManager;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;

    @Inject
    public LoginViewModel(SessionManager sessionManager, AuthUtils authUtils) {
        this.sessionManager = sessionManager;
        this.authUtils = authUtils;
    }

    public void setObserve(LoginActivity authActivity) {
        sessionManager.isLogin().observe(authActivity, aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    authActivity.startActivity(new Intent(authActivity, MainActivity.class));
                    authActivity.finishAffinity();
                }
            }
        });
    }

    public void authenticateUser(LoginActivity authActivity, String email, String password) {
        authUtils.signIn(authActivity, email, password);
    }

    public void resetPassword(LoginActivity authActivity, String email) {
        authUtils.resetPassword(authActivity, email);
    }

    public void sendIntent(LoginActivity authActivity, Class<?> aClass) {
        authActivity.startActivity(new Intent(authActivity, aClass));
    }

    public void signUpWithPhone(LoginActivity registrationActivity, String user, ClickCallbacks clickCallbacks) {
        authUtils.createUserWithPhone(registrationActivity, user, clickCallbacks);
    }

    public void verifyOTP(Activity activity, PhoneAuthCredential credential) {
        authUtils.signInWithPhoneAuthCredential(null, activity, credential);
    }

    public void onStop() {
        authUtils.onStop();
    }
}