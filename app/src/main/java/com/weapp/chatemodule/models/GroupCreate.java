package com.weapp.chatemodule.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class GroupCreate {

    private int index;
    private boolean isSelect = false;
    private String roomId;

    private String fName;
    private String lName;
    private String userId;
    private String mobile;
    private String email;
    private String password;
    private String imgName;
    private Uri path;
    private Bitmap profile;

    private Status status;
    private Message message;

    public GroupCreate() {
    }

    public GroupCreate(String userId, String fName, String lName, String mobile, String email, String password, String imgName, Uri path, Bitmap profile) {
        this.userId = userId;
        this.fName = fName;
        this.lName = lName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.path = path;
        this.imgName = imgName;
        this.profile = profile;
    }

    public GroupCreate(String userId, String fName, String lName, String mobile, String email, String password, String imgName) {
        this.userId = userId;
        this.fName = fName;
        this.lName = lName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.imgName = imgName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public String getFullName() {
        return fName + " " + lName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "User{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", userId='" + userId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imgName='" + imgName + '\'' +
                ", path=" + path +
                ", profile=" + profile +
                ", status=" + status +
                ", message=" + message +
                '}';
    }
}