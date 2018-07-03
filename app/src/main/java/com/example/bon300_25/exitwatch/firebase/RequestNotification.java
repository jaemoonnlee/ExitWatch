package com.example.bon300_25.exitwatch.firebase;

import com.google.gson.annotations.SerializedName;

public class RequestNotification {
    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private SendNotificationModel sendNotificationModel;

    public SendNotificationModel getSendNotificationModel() {
        return sendNotificationModel;
    }

    public void setSendNotificationModel(SendNotificationModel sendNotificationModel) {
        this.sendNotificationModel = sendNotificationModel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
