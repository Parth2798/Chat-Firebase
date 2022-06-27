package com.weapp.chatemodule.models;

import java.util.List;

public class FriendList {
    private List<Friend> friends;

    public FriendList() {
    }

    public FriendList(List<Friend> friends) {
        this.friends = friends;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
}