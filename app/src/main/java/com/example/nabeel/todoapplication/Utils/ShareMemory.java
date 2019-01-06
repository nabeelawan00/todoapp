package com.example.nabeel.todoapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareMemory {

    public static String sharename = "ToDoApp";

    SharedPreferences prefs;

    public static Context mContext;
    public static ShareMemory mInstence;

    public boolean isThisSessionFromLink;

    public ShareMemory(Context ctx) {
        prefs = ctx.getSharedPreferences(sharename, Context.MODE_PRIVATE);
        isThisSessionFromLink = false;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static ShareMemory getmInstence() {
        if (mInstence != null) {
            return mInstence;
        } else {
            mInstence = new ShareMemory(mContext.getApplicationContext());
        }
        return mInstence;
    }

    public void setUserID(String userID) {
        prefs.edit().putString("userID", userID).commit();
    }

    public String getUserID() {
        return prefs.getString("userID", "");
    }

}
