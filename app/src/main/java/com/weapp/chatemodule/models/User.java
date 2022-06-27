package com.weapp.chatemodule.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class User {

    private String fName;
    private String lName;
    private String userId;
    private String mobile;
    private String email;
    private String password;
    private String imgName;
    private Uri path;

    private Status status = new Status();
    private Message message = new Message();

    public User() {
    }

    public User(String userId, String fName, String lName, String mobile, String email, String password, String imgName, Uri path, Status status) {
        this.userId = userId;
        this.fName = fName;
        this.lName = lName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.path = path;
        this.status = status;
        this.imgName = imgName;
    }

    public User(String userId, String fName, String lName, String mobile, String email, String password, String imgName, Status status) {
        this.userId = userId;
        this.fName = fName;
        this.lName = lName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.imgName = imgName;
        this.status = status;
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
                ", status=" + status +
                ", message=" + message +
                '}';
    }
}