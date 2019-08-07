package com.example.vanaiddriver.services;

import com.example.vanaiddriver.classes.Requestor;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("fcm_token", s);

        Requestor fcmRequestor = new Requestor("api/fcm/save", param, this);
        fcmRequestor.execute();
    }
}
