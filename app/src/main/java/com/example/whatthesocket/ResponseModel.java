package com.example.whatthesocket;

public class ResponseModel {
    private String uuid;
    private String me;
    private String you;
    private String from_idx;
    private String msg;
    private int msg_idx;
    private String timestamp;
    private int is_read;
    private String image;

    public ResponseModel(String uuid, String me, String you, String from_idx, String msg, int msg_idx, String timestamp, int is_read, String image) {
        this.uuid = uuid;
        this.me = me;
        this.you = you;
        this.from_idx = from_idx;
        this.msg = msg;
        this.msg_idx = msg_idx;
        this.timestamp = timestamp;
        this.is_read = is_read;
        this.image = image;
    }

    public ResponseModel(String me, String msg, String timestamp, int is_read, String image) {
        this.me = me;
        this.msg = msg;
        this.timestamp = timestamp;
        this.is_read = is_read;
        this.image = image;
    }

    public String getMyName() {
        return me;
    }

    public String getMsg() {
        return msg;
    }

    public String getImage() {
        return image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getReadCheck() {
        return is_read;
    }

    ResponseModel() {

    }
}
