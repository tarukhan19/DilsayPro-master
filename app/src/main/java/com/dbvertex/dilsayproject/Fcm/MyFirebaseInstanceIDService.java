package com.dbvertex.dilsayproject.Fcm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    SessionManager sessionManager;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //    String refreshedToken = FirebaseInstanceId.getInstance().getInstanceId ();


        Log.e("refreshedToken",s);
//
//        storeRegIdInPref(refreshedToken);
//
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", refreshedToken);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

        sendRegistrationToServer(s);

    }

    private void sendRegistrationToServer(String refreshedToken) {
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
