package com.weapp.chatemodule.ui.main.friendGroupFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.FirebaseDatabase;
import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.ClickCallbacks;
import com.weapp.chatemodule.databinding.FragmentFriendGroupBinding;
import com.weapp.chatemodule.models.Friend;
import com.weapp.chatemodule.ui.BaseFragment;
import com.weapp.chatemodule.ui.chat.ChatActivity;
import com.weapp.chatemodule.util.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class FriendFragment extends BaseFragment<FragmentFriendGroupBinding> implements ClickCallbacks {

    private FriendViewModel friendGroupViewModel;

    @Inject
    @Named("friend")
    FriendGroupAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_group;
    }

    private LinearLayoutManager manager;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();

        friendGroupViewModel = new ViewModelProvider(this, factory).get(FriendViewModel.class);

        manager = new LinearLayoutManager(activity);
        adapter.setClick(this);
        binding.dataList.setLayoutManager(new LinearLayoutManager(activity));
        binding.dataList.setAdapter(adapter);

        friendGroupViewModel.setFList();

        setObserve();
    }

    private void setObserve() {
        friendGroupViewModel.getFList().observe(getViewLifecycleOwner(), friendList -> {
            if (friendList != null) {
//                friendGroupViewModel.getAllGroups(friendList.getFriends());
                friendGroupViewModel.getLastMsg(friendList.getFriends());
            }
        });

        friendGroupViewModel.getGroup().observe(getViewLifecycleOwner(), friendList -> {
            if (friendList != null) {
//                friendGroupViewModel.getLastMsg(friendList.getFriends());
            }
        });

        friendGroupViewModel.getFListMsg().observe(getViewLifecycleOwner(), friendList -> {

            List<Friend> friends = filterData(friendList.getFriends());

            Parcelable recyclerViewState;
            recyclerViewState = manager.onSaveInstanceState();
            adapter.updateItems(friends);
            adapter.notifyDataSetChanged();
            manager.onRestoreInstanceState(recyclerViewState);

        });
    }

    private List<Friend> filterData(List<Friend> friends) {
        List<Friend> data = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getUser() != null && !preferencesClass.getUserID().equals(friends.get(i).getUser().getUserId())) {
                data.add(friends.get(i));
            }
        }

        return data;
    }

    @Override
    public void getCallback(View v, Object o) {
        if (o != null) {
            if (o instanceof Friend) {

                if (((Friend) o).getIdRoom() == null || ((Friend) o).getIdRoom().isEmpty()) {
                    FirebaseDatabase.getInstance().getReference().child(Constants.FRIENDS + preferencesClass.getUserID() + "/" + preferencesClass.getUserID() + ((Friend) o).getUser().getUserId())
                            .setValue(new Friend(((Friend) o).getId(), preferencesClass.getUserID() + ((Friend) o).getUser().getUserId(), false));

                    FirebaseDatabase.getInstance().getReference().child(Constants.FRIENDS + ((Friend) o).getUser().getUserId() + "/" + ((Friend) o).getUser().getUserId() + preferencesClass.getUserID())
                            .setValue(new Friend(preferencesClass.getUserID(), preferencesClass.getUserID() + ((Friend) o).getUser().getUserId(), false));
                    ((Friend) o).setIdRoom(preferencesClass.getUserID() + ((Friend) o).getUser().getUserId());
                }

                friendGroupViewModel.setFriendClick((Friend) o);
                startActivity(new Intent(activity, ChatActivity.class));
            }
        }
    }
}