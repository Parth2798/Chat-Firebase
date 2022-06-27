package com.weapp.chatemodule.ui.main.profileFragment;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.FirebaseStorage;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.Logger;
import com.weapp.chatemodule.util.SessionManager;

import javax.inject.Inject;

public class ProfileViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;
    private MutableLiveData<Integer> isProgress = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(SessionManager sessionManager, AuthUtils authUtils) {
        this.sessionManager = sessionManager;
        this.authUtils = authUtils;
    }

    public void updateUser(User user) {
        authUtils.setNewUser(user);
    }

    public void loadImage(AppCompatActivity activity, ImageView profile, String s) {
        authUtils.setImage(activity, s, profile);
    }

    public MutableLiveData<Integer> getIsProgress() {
        return isProgress;
    }

    public void updateProfile(Uri uri, String url) {
        getIsProgress().setValue(0);

        FirebaseStorage
                .getInstance()
                .getReference()
                .child(url)
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> getIsProgress().setValue(1))
                .addOnFailureListener(e -> {
                    getIsProgress().setValue(2);
                    e.printStackTrace();
                })
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    Logger.e("KKKKKKKKKKK   ", "!!!!! Image Upload Progress  + " + progress);
                });
    }

    public void saveUserInfo() {
        authUtils.saveUserInfo();
    }
}