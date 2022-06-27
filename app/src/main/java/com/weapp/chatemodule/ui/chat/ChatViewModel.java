package com.weapp.chatemodule.ui.chat;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.weapp.chatemodule.models.Message;
import com.weapp.chatemodule.models.Status;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.Constants;
import com.weapp.chatemodule.util.Logger;
import com.weapp.chatemodule.util.PreferencesClass;
import com.weapp.chatemodule.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class ChatViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;
    private final PreferencesClass preferencesClass;
    private MutableLiveData<Integer> isProgress = new MutableLiveData<>();

    @Inject
    public ChatViewModel(SessionManager sessionManager, AuthUtils authUtils, PreferencesClass preferencesClass) {
        this.sessionManager = sessionManager;
        this.authUtils = authUtils;
        this.preferencesClass = preferencesClass;
    }

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private MutableLiveData<Message> message = new MutableLiveData<>();
    private MutableLiveData<User> opponentUser = new MutableLiveData<>();

    public MutableLiveData<Integer> getIsProgress() {
        return isProgress;
    }

    public void getSingleChat(Activity activity) {
        if (sessionManager.getFriend().getValue() == null) {
            activity.finish();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGE + sessionManager.getFriend().getValue().getIdRoom())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                        if (dataSnapshot.getValue() != null) {
                            Message message = dataSnapshot.getValue(Message.class);
                            if (message != null) {
                                getMessage().setValue(message);
                            }

                            Logger.e("KKKKKKKK      ", "!!!!! Single   + " + dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getChatList(Activity activity) {
        if (sessionManager.getFriend().getValue() == null) {
            activity.finish();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGE + sessionManager.getFriend().getValue().getIdRoom()).addValueEventListener(chatList);
    }

    ValueEventListener chatList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            FirebaseDatabase.getInstance().getReference().removeEventListener(chatList);

            List<Message> messages = new ArrayList<>();

            for (DataSnapshot ds : snapshot.getChildren()) {
                if (ds.getValue() != null) {
                    Logger.e("KKKKKKKK      ", "!!!!! Bundle   + " + ds.getValue().toString());
                    messages.add(ds.getValue(Message.class));
                }
            }

            getMessages().setValue(messages);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void sendMsg(Activity activity, String s, String extension, Uri uri) {
        if (sessionManager.getFriend().getValue() == null || sessionManager.getUser().getValue() == null || sessionManager.getUser().getValue().data == null) {
            Toast.makeText(activity, "Room Id Not Found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uri != null) {
            String uriName = sessionManager.getFriend().getValue().getIdRoom() + new Date().getTime();
            getIsProgress().setValue(0);
            FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(Constants.CHAT + uriName)
                    .putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        setMessage(activity, s, extension, uriName);
                        getIsProgress().setValue(1);
                    })
                    .addOnFailureListener(e -> {
                        getIsProgress().setValue(2);
                        e.printStackTrace();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        Logger.e("KKKKKKKKKKK   ", "!!!!! Image Upload Progress  + " + progress);
                    });
        } else {
            setMessage(activity, s, extension, "");
        }
    }

    private void setMessage(Activity activity, String s, String extension, String uri) {
        if (sessionManager.getFriend().getValue() == null || sessionManager.getUser().getValue() == null || sessionManager.getUser().getValue().data == null) {
            Toast.makeText(activity, "Room Id Not Found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = new Message(preferencesClass.getUserID()
                , sessionManager.getUser().getValue().data.getFullName()
                , sessionManager.getFriend().getValue().getIdRoom()
                , s
                , extension
                , ((uri != null && !uri.isEmpty()) ? uri : "")
                , new Date().getTime()
                , new SimpleDateFormat("mm/dd/yyyy", Locale.US).format(new Date()));

        FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGE + sessionManager.getFriend().getValue().getIdRoom())
                .push()
                .setValue(message);
    }

    public void getOpponentUser(String id) {
        FirebaseDatabase.getInstance().getReference().child(Constants.USER + id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    getOpponentUser().setValue(dataSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadImage(Activity activity, ImageView imageView, String path) {
        authUtils.setImage(activity, path, imageView);
    }

    public MutableLiveData<List<Message>> getMessages() {
        return messages;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public MutableLiveData<User> getOpponentUser() {
        return opponentUser;
    }

    public MutableLiveData<Message> getMessage() {
        return message;
    }
}