package com.weapp.chatemodule.ui.main;

import androidx.lifecycle.ViewModel;

import com.weapp.chatemodule.models.Status;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.SessionManager;

import java.util.Date;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    private static final String TAG = "AuthViewModel";

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;

    @Inject
    public MainViewModel(SessionManager sessionManager, AuthUtils authUtils) {
        this.sessionManager = sessionManager;
        this.authUtils = authUtils;
    }

    public void logOut() {
        authUtils.logOut();
    }

    public void onStop() {
        authUtils.onStop();
    }

    public void setIsOnline(boolean isOnline) {
        if (sessionManager.getUser().getValue() != null && sessionManager.getUser().getValue().data != null) {
            User user = sessionManager.getUser().getValue().data;
            user.setStatus(new Status(isOnline, new Date().getTime()));

            authUtils.setNewUser(user);
        }
    }
}