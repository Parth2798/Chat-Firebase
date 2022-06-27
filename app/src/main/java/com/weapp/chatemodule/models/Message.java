package com.weapp.chatemodule.models;

public class Message {

    private String idSender;
    private String idSenderName;
    private String idReceiver;
    private String text;
    private String ext;
    private String url;
    private String date;
    private long timestamp;

    public Message() {
    }

    public Message(String idSender, String idSenderName, String idReceiver, String text, String ext, String url, long timestamp, String date) {
        this.idSender = idSender;
        this.idSenderName = idSenderName;
        this.idReceiver = idReceiver;
        this.text = text;
        this.ext = ext;
        this.url = url;
        this.timestamp = timestamp;
        this.date = date;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdSenderName() {
        return idSenderName;
    }

    public void setIdSenderName(String idSenderName) {
        this.idSenderName = idSenderName;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idSender='" + idSender + '\'' +
                ", idReceiver='" + idReceiver + '\'' +
                ", text='" + text + '\'' +
                ", ext='" + ext + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}