package com.example.whatthesocket;

public class ChatModel {
    public String name;
    private String script;
    private String profile_image;
    private String date_time;

    public ChatModel(String name, String script, String profile_image, String date_time) {
        this.name = name;
        this.script = script;
        this.profile_image = profile_image;
        this.date_time = date_time;
    } // constructor END

    public String getName() {
        return name;
    } // getName END

    public String getScript() {
        return script;
    } // getScript END

    public String getProfile_image() {
        return profile_image;
    } // getProfile_image END

    public String getDate_time() {
        return date_time;
    } // getDate_time END

    ChatModel() {

    } // default constructor END
} // ChatModel Class END
