package com.codeworm.barkapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Harvie Marcelino on 01/12/2018.
 */

public class SharedPreferencesManager {
    private static SharedPreferencesManager mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAME = "mysharedpref";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_MOBILENUM = "mobilenum";
    private static final String KEY_USERID = "userid";
    private static final String KEY_QR = "code";
    private static final String KEY_SLOT_ID = "slot_id";
    private static final String KEY_RACK_LOCATION = "rack_location";
    private static final String KEY_ADDRESS = "address";



    private SharedPreferencesManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesManager(context);
        }
        return mInstance;
    }

    public boolean loginUser(String fullname, String username, String mobilenumber, int id){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_MOBILENUM, mobilenumber);
        editor.putInt(KEY_USERID, id);

        editor.apply();
        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USERNAME, null) != null){
            return true;
        }
        return false;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public void clearSharePreference(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getFullname(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FULLNAME, null);
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public void setUsername(String username){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);

        editor.apply();
    }

    public String getMobilenum(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILENUM, null);
    }

    public boolean setCode(String code){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_QR, code);

        editor.apply();
        return true;
    }
    public String getCode(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_QR, null);
    }
    public boolean setParkingDetails(String slot_id, String rack_location, String address){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_SLOT_ID, slot_id);
        editor.putString(KEY_RACK_LOCATION, rack_location);
        editor.putString(KEY_ADDRESS, address);

        editor.apply();
        return true;
    }
    public String getSlotId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_SLOT_ID, null);
    }
    public String getRackLocation(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_RACK_LOCATION, null);
    }
    public String getAddress(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ADDRESS, null);
    }

    public void removeParkingDetails(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(KEY_SLOT_ID).commit();
        sharedPreferences.edit().remove(KEY_RACK_LOCATION).commit();
        sharedPreferences.edit().remove(KEY_ADDRESS).commit();
    }
}
