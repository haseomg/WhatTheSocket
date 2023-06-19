package com.example.whatthesocket;

public class ChatModel {
    private String uuid;
    private String uuid2;
    private String myname;
    private String myname2;
    private String yourname;
    private String yourname2;
    public String name;
    private String message;
    private String timestamp;
    private int is_read;
    private String image;

    public ChatModel(String uuid, String myname, String yourname, String uuid2, String myname2, String yourname2,
                     String name, String message, String timestamp, int is_read, String image) {
        this.uuid = uuid;
        this.myname = myname;
        this.yourname = yourname;
        this.uuid2 = uuid2;
        this.myname2 = myname2;
        this.yourname2 = yourname2;
        this.name = name;
        this.message = message;
        this.timestamp = timestamp;
        this.is_read = is_read;
        this.image = image;
    } // con

    public ChatModel(String name, String message, String timestamp, int is_read, String image) {
        this.name = name;
        this.message = message;
        this.timestamp = timestamp;
        this.is_read = is_read;
        this.image = image;
    } // constructor END

    public String getName() {
        return name;
    } // getName END

    public String getScript() {
        return message;
    } // getScript END

    public String getProfile_image() {
        return image;
    } // getProfile_image END

    public String getDate_time() {
        return timestamp;
    } // getDate_time END

    public int getIs_read() {
        return is_read;
    } // getDate_time END

    ChatModel() {

    } // default constructor END
} // ChatModel Class END
