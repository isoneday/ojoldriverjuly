package com.imastudio.ojoldriverjuly.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String tokenku;

    @Override
    public void onTokenRefresh() {
        String refrestoken = FirebaseInstanceId.getInstance().getToken();
    tokenku =refrestoken;
    }


    public String tokenku(){
        return tokenku;
    }
}
