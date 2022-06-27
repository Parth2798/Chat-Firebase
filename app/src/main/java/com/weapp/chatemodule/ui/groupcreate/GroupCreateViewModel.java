package com.weapp.chatemodule.ui.groupcreate;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.weapp.chatemodule.models.Friend;
import com.weapp.chatemodule.models.GroupCreate;
import com.weapp.chatemodule.models.Message;
import com.weapp.chatemodule.models.Status;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.AuthUtils;
import com.weapp.chatemodule.util.Constants;
import com.weapp.chatemodule.util.Logger;
import com.weapp.chatemodule.util.PreferencesClass;
import com.weapp.chatemodule.util.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class GroupCreateViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final AuthUtils authUtils;
    private final PreferencesClass preferencesClass;

    @Inject
    public GroupCreateViewModel(PreferencesClass preferencesClass, SessionManager sessionManager, AuthUtils authUtils) {
        this.sessionManager = sessionManager;
        this.preferencesClass = preferencesClass;
        this.authUtils = authUtils;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    private MutableLiveData<List<GroupCreate>> groupData = new MutableLiveData<>();
    private MutableLiveData<Integer> isProgress = new MutableLiveData<>();

    public void getAllUsers() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.USER)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<GroupCreate> list = new ArrayList<>();
                        int a = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            GroupCreate create = ds.getValue(GroupCreate.class);
                            if (create != null) {
                                create.setSelect(false);
                                create.setIndex(a);

                                list.add(create);
                                a++;
                            }
                        }

                        setGroupData(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void createGroup(GroupCreate create, List<GroupCreate> groupCreates) {
        getIsProgress().setValue(0);

        long time = new Date().getTime();
        String folder = preferencesClass.getUserID() + time;

        User nUser = new User(folder, create.getfName(), create.getlName(), create.getMobile(), create.getEmail(), create.getPassword(), create.getImgName(), new Status());
        nUser.setUserId(folder);
        nUser.setMessage(new Message());

        Friend friend = new Friend(folder, folder, true);

        if (create.getPath() != null) {
            FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(Constants.PROFILE + nUser.getImgName())
                    .putFile(create.getPath())
                    .addOnSuccessListener(taskSnapshot -> {
                        setData(nUser, friend, groupCreates, folder);
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
            setData(nUser, friend, groupCreates, folder);
        }
    }

    private void setData(User nUser, Friend friend, List<GroupCreate> groupCreates, String folder) {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.GROUP + folder)
                .setValue(nUser);

        for (int i = 0; i < groupCreates.size(); i++) {

            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(Constants.FRIENDS + groupCreates.get(i).getUserId() + "/" + folder)
                    .setValue(friend);

            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(Constants.GROUP + folder + "/" + groupCreates.get(i).getUserId())
                    .setValue(groupCreates.get(i));
        }

        getIsProgress().setValue(1);
    }

    public MutableLiveData<List<GroupCreate>> getGroupData() {
        return groupData;
    }

    public void setGroupData(List<GroupCreate> groupData) {
        this.groupData.setValue(groupData);
    }

    public MutableLiveData<Integer> getIsProgress() {
        return isProgress;
    }
}