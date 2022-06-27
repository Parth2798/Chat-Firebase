package com.weapp.chatemodule.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.callbacks.UploadImage;
import com.weapp.chatemodule.callbacks.getImage;
import com.weapp.chatemodule.controller.AuthResource;
import com.weapp.chatemodule.models.User;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthUtils {

    private SessionManager sessionManager;
    private static final String TAG = "AuthUtils";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Inject
    public AuthUtils(SessionManager sessionManager) {
        this.sessionManager = sessionManager;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuthListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                sessionManager.getUser().setValue(AuthResource.loading(null));
                Constants.UID = user.getUid();
                Logger.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                saveUserInfo();
            } else {
                Logger.e(TAG, "onAuthStateChanged:signed_out");
            }
        };

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth.addAuthStateListener(mAuthListener);
    }

    public void signIn(Activity activity, String email, String password) {
        sessionManager.getUser().setValue(AuthResource.loading(null));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    Logger.e(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    if (!task.isSuccessful()) {
                        sessionManager.setIsLogin(false);
                        sessionManager.authenticateWithId(null, "Task not success.");
                    } else {
                        saveUserInfo();
                        Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();

                    sessionManager.setIsLogin(false);
                    sessionManager.authenticateWithId(null, "login Fail: " + e.getMessage());
                });
    }

    public void saveUserInfo() {
        databaseReference.child(Constants.USER + Constants.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User userInfo = dataSnapshot.getValue(User.class);
                if (userInfo != null) {
                    sessionManager.authenticateWithId(userInfo, " Login Success.");
                    new Handler().postDelayed(() -> sessionManager.setIsLogin(true), 1000);
                } else {
                    sessionManager.getUser().setValue(AuthResource.error("", null));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                sessionManager.setIsLogin(false);
                sessionManager.authenticateWithId(null, "User data not save");
            }
        });
    }

    public void createUserWithEmail(Activity activity, User mUser) {
        mUser.setImgName(new Date().getTime() + mUser.getfName());
        sessionManager.getUser().setValue(AuthResource.loading(null));

        mAuth.createUserWithEmailAndPassword(mUser.getEmail(), mUser.getPassword())
                .addOnCompleteListener(activity, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadImage(mUser.getPath(), Constants.PROFILE + mUser.getImgName(), new UploadImage() {
                            @Override
                            public void onSuccess() {
                                initNewUserInfo(mUser);
                            }
                        });
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void createUserWithPhone(Activity activity, String phoneNumber, ClickCallbacks clickCallbacks) {
        sessionManager.getUser().setValue(AuthResource.loading(null));

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                activity,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        e.printStackTrace();
                        sessionManager.getUser().setValue(AuthResource.error("Invalid Phone Number, Please enter correct phone number with your country code...", null));
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        sessionManager.getUser().setValue(AuthResource.error("Code has been sent, please check and verify...", null));
                        clickCallbacks.getCallback(null, s);
                    }
                });
    }

    public void signInWithPhoneAuthCredential(User mUser, Activity activity, PhoneAuthCredential credential) {
        sessionManager.getUser().setValue(AuthResource.loading(null));

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        if (mUser != null) {
                            mUser.setImgName(new Date().getTime() + mUser.getfName());
                            uploadImage(mUser.getPath(), Constants.PROFILE + mUser.getImgName(), new UploadImage() {
                                @Override
                                public void onSuccess() {
                                    initNewUserInfo(mUser);
                                }
                            });
                        } else {
                            saveUserInfo();
                        }
                    } else {
                        if (task.getException() != null) {
                            task.getException().printStackTrace();
                        }
                        sessionManager.getUser().setValue(AuthResource.error("Error : ", null));
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    sessionManager.getUser().setValue(AuthResource.error("Error : ", null));
                });
    }

    void initNewUserInfo(User mUser) {
        setNewUser(mUser);
        mUser.setUserId(user.getUid());

        sessionManager.authenticateWithId(mUser, " New User Created.");

        new Handler().postDelayed(() -> sessionManager.setIsLogin(true), 1000);

        saveUserInfo();
    }

    public void setNewUser(User mUser) {
        User nUser = new User(user.getUid(), mUser.getfName(), mUser.getlName(), mUser.getMobile(), mUser.getEmail(), mUser.getPassword(), mUser.getImgName(), mUser.getStatus());
        databaseReference.child(Constants.USER + user.getUid()).setValue(nUser);
    }

    public void resetPassword(Activity activity, final String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> Toast.makeText(activity, "Sent email to " + email, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity, "False to sent email to " + email, Toast.LENGTH_SHORT).show());
    }

    public void uploadImage(Uri file, String child, UploadImage uploadImage) {
        if (file != null) {

            StorageReference ref = storageReference.child(child);

            ref.putFile(file)
                    .addOnSuccessListener(taskSnapshot -> uploadImage.onSuccess())
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        uploadImage.onFail();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        uploadImage.progress(progress);
                        Logger.e("KKKKKKKKKKK   ", "!!!!! Image Upload Progress  + " + progress);
                    });
        }
    }

    private void downloadImage(Context activity, String path, getImage getImage) {
        storageReference.child(path).getDownloadUrl()
                .addOnSuccessListener(uri ->
                        Glide.with(activity)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .load(uri)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        getImage.getBitmap(null);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        getImage.getBitmap(resource);
                                        return false;
                                    }
                                })
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                }))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    getImage.getBitmap(null);
                });
    }

    public void setImage(Activity activity, String path, ImageView getImage) {
        storageReference
                .child(path)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    Glide.with(activity)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(getImage);
                });
    }

    public void onStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void logOut() {
        mAuth.signOut();
        sessionManager.getUser().setValue(AuthResource.logout());
    }
}