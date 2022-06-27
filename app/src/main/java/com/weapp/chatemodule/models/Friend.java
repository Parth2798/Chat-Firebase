package com.weapp.chatemodule.models;

public class Friend{
    private String id;
    private String idRoom;
    private boolean isGroup;
    private User user;

    public Friend() {
    }

    public Friend(String id, String idRoom, boolean isGroup) {
        this.id = id;
        this.idRoom = idRoom;
        this.isGroup = isGroup;
    }

    public Friend(String id, String idRoom, User user) {
        this.id = id;
        this.idRoom = idRoom;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id='" + id + '\'' +
                ", idRoom='" + idRoom + '\'' +
                ", isGroup=" + isGroup +
                ", user=" + user +
                '}';
    }
}