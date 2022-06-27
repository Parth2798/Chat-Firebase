package com.weapp.chatemodule.ui.main.friendGroupFragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weapp.chatemodule.models.Friend;
import com.weapp.chatemodule.models.FriendList;
import com.weapp.chatemodule.models.Message;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.util.Constants;
import com.weapp.chatemodule.util.Logger;
import com.weapp.chatemodule.util.PreferencesClass;
import com.weapp.chatemodule.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FriendViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final PreferencesClass preferencesClass;

    private List<Friend> friends = new ArrayList<>();
    private FriendList friendList = new FriendList();

    @Inject
    public FriendViewModel(SessionManager sessionManager, PreferencesClass preferencesClass) {
        this.sessionManager = sessionManager;
        this.preferencesClass = preferencesClass;
    }

    private MutableLiveData<FriendList> fList = new MutableLiveData<>();
    private MutableLiveData<FriendList> fListWithMsg = new MutableLiveData<>();
    private MutableLiveData<FriendList> group = new MutableLiveData<>();

    public MutableLiveData<FriendList> getGroup() {
        return group;
    }

    public LiveData<FriendList> getFList() {
        return fList;
    }

    public LiveData<FriendList> getFListMsg() {
        return fListWithMsg;
    }

    public void setFList() {
        FirebaseDatabase.getInstance().getReference().child(Constants.FRIENDS + preferencesClass.getUserID()).addValueEventListener(getFriends);
    }

    ValueEventListener getFriends = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            friends.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {

                if (ds.getValue() != null) {
                    Friend friend = ds.getValue(Friend.class);
                    if (friend != null && !friend.isGroup()) {
                        friends.add(friend);
                    }
                }
            }

            getAllUsers();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void getAllUsers() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.USER)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            boolean isAdd = true;

                            User user = ds.getValue(User.class);

                            if (user != null) {
                                for (int i = 0; i < friends.size(); i++) {
                                    if (friends.get(i).getId() != null && friends.get(i).getId().equals(user.getUserId())) {
                                        friends.get(i).setUser(user);
                                        isAdd = false;
                                    }
                                }

                                if (isAdd) {
                                    Friend friend = new Friend(user.getUserId(), "", false);
                                    FirebaseDatabase
                                            .getInstance()
                                            .getReference()
                                            .child(Constants.FRIENDS + preferencesClass.getUserID() + "/" + preferencesClass.getUserID() + user.getUserId())
                                            .setValue(friend);
                                }
                            }
                        }

                        friendList.setFriends(friends);
                        fList.setValue(friendList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Logger.e("KKKKKKKKK      ", "!!!!! getAllUsers  + " + error.getMessage());
                    }
                });
    }

//    public void getAllGroups(List<Friend> friends) {
//        FirebaseDatabase
//                .getInstance()
//                .getReference()
//                .child(Constants.GROUP)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            if (ds.getValue() != null) {
//
//                                User mUser = ds.getValue(User.class);
//                                if (mUser != null) {
//                                    for (int i = 0; i < friends.size(); i++) {
//
//                                        if (friends.get(i).getId().equals(mUser.getUserId())) {
//
//                                            for (DataSnapshot dp : ds.getChildren()) {
//                                                if (dp.getValue() != null) {
//
//                                                    if (!(dp.getValue() instanceof String)) {
//                                                        User user = dp.getValue(User.class);
//                                                        if (user != null) {
//
//                                                            for (int j = 0; j < friends.size(); j++) {
//                                                                if (friends.get(j).getUser() != null && user.getUserId() != null) {
//                                                                    if (friends.get(j).getUser().getUserId().equals(user.getUserId())) {
//                                                                        friends.get(i).setUser(mUser);
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        FriendList friendList = new FriendList(friends);
//                        getGroup().setValue(friendList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Logger.e("KKKKKKKKK      ", "!!!!! getAllGroups  + " + error.getMessage());
//                    }
//                });
//    }

    public void getLastMsg(List<Friend> friends) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getIdRoom() != null && !friends.get(i).getIdRoom().isEmpty()) {
                int finalI = i;
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child(Constants.MESSAGE + friends.get(i).getIdRoom())
                        .limitToLast(1)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (ds.getValue() != null) {
                                        Message message = ds.getValue(Message.class);
                                        if (message != null) {
                                            if (friends.get(finalI).getUser() != null && friends.get(finalI).getUser().getMessage() != null) {
                                                friends.get(finalI).getUser().setMessage(message);
                                            }
                                        }
                                    }
                                }
                                FriendList friendList = new FriendList(friends);
                                fListWithMsg.setValue(friendList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Logger.e("KKKKKKKKK      ", "!!!!! getLastMsg  + " + error.getMessage());
                            }
                        });
            } else {
                FriendList friendList = new FriendList(friends);
                fListWithMsg.setValue(friendList);
            }
        }
    }

    public void setFriendClick(Friend o) {
        sessionManager.getFriend().setValue(o);
    }
}