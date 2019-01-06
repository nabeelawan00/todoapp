package com.example.nabeel.todoapplication;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.example.nabeel.todoapplication.Utils.ShareMemory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ToDoApp extends Application {

    private ShareMemory shareMemory;

    @Override
    public void onCreate() {
        super.onCreate();
        ShareMemory.init(this);
        shareMemory = ShareMemory.getmInstence();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.nabeel.todoapplication",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
