package com.example.bon300_25.exitwatch.firebase;

public class SendNotificationModel {
    private String body,title;

    public SendNotificationModel(String title, String body) {
        this.body = body;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
