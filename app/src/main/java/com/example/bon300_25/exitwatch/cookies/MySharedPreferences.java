package com.example.bon300_25.exitwatch.cookies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

public class MySharedPreferences {
    public static final String KEY_COOKIE = "yjc.wdb.saw.cookies";
    private Context mContext;
    private SharedPreferences preferences;

    private static MySharedPreferences mySharedPreferences = null;

    public static MySharedPreferences getInstanceOf(Context context) {
        if(mySharedPreferences==null) {
            mySharedPreferences = new MySharedPreferences(context);
        }
        return mySharedPreferences;
    }

    public MySharedPreferences(Context context) {
        mContext = context;
        final String PREF_NAME = context.getPackageName();
        preferences = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }

    public void putHashSet(String key, HashSet<String> set) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, set);
        editor.commit();
    }

    public HashSet<String> getHashSet(String key, HashSet<String> dftValue) {
        try {
            return (HashSet<String>) preferences.getStringSet(key, dftValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dftValue;
    }
}
