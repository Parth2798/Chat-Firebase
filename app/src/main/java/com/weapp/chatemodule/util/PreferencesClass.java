package com.weapp.chatemodule.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.weapp.chatemodule.models.Status;
import com.weapp.chatemodule.models.User;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesClass {

    private static SessionManager sessionManager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;
    private static AuthUtils utils;

    @Inject
    public PreferencesClass(SessionManager sessionManager, AuthUtils utils, Application application) {
        PreferencesClass.utils = utils;
        PreferencesClass.sessionManager = sessionManager;

        preferences = application.getSharedPreferences("RWL", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    public void setUserID(String src) {
        prefEditor.putString("UID", src).commit();
    }

    public String getUserID() {
        return preferences.getString("UID", "");
    }

    public void setEmail(String age) {
        prefEditor.putString("Email", age).commit();
    }

    public String getEmail() {
        return preferences.getString("Email", "");
    }

    public void setPassword(String age) {
        prefEditor.putString("Password", age).commit();
    }

    public String getPassword() {
        return preferences.getString("Password", "");
    }

    public void clearData() {
        prefEditor.clear();
        prefEditor.commit();
    }

    public static void isOnline(boolean b) {
        if (sessionManager != null && sessionManager.getUser() != null && sessionManager.getUser().getValue() != null && sessionManager.getUser().getValue().data != null) {
            User user = sessionManager.getUser().getValue().data;
            user.setStatus(new Status(b, new Date().getTime()));
            utils.setNewUser(user);
        }
    }
}