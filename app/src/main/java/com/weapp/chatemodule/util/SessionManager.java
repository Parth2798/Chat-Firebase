package com.weapp.chatemodule.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.weapp.chatemodule.controller.AuthResource;
import com.weapp.chatemodule.models.Friend;
import com.weapp.chatemodule.models.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionManager {

    private MediatorLiveData<Friend> friend = new MediatorLiveData<>();
    private MediatorLiveData<AuthResource<User>> cachedUser = new MediatorLiveData<>();

    private MediatorLiveData<Boolean> isLogin = new MediatorLiveData<>();

    @Inject
    public SessionManager() {
    }

    public void authenticateWithId(final User user, String msg) {
        if (cachedUser != null) {
            if (user != null) {
                cachedUser.setValue(AuthResource.authenticated(user));
            } else {
                cachedUser.setValue(AuthResource.error(msg, null));
            }
        }
    }

    public MutableLiveData<AuthResource<User>> getUser() {
        return cachedUser;
    }

    public void setIsLogin(boolean login) {
        isLogin.setValue(login);
    }

    public LiveData<Boolean> isLogin() {
        return isLogin;
    }

    public MediatorLiveData<Friend> getFriend() {
        return friend;
    }
}