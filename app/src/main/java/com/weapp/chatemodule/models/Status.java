package com.weapp.chatemodule.models;

public class Status{
    public boolean isOnline;
    public long timestamp;

    public Status() {
    }

    public Status(boolean isOnline, long timestamp) {
        this.isOnline = isOnline;
        this.timestamp = timestamp;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Status{" +
                "isOnline=" + isOnline +
                ", timestamp=" + timestamp +
                '}';
    }
}