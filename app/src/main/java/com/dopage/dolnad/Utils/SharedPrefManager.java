package com.dopage.dolnad.Utils;

import android.content.SharedPreferences;

import com.dopage.dolnad.Models.Token;
import com.dopage.dolnad.Models.User;

public class SharedPrefManager {

    private SharedPreferences prefs;

    private SharedPreferences.Editor editor;

    private static SharedPrefManager mInstance;

    private SharedPrefManager(SharedPreferences prefs){

        this.prefs = prefs;
        this.editor = prefs.edit();

    }

    public static synchronized SharedPrefManager getInstance(SharedPreferences prefs){

        if(mInstance == null){
            mInstance = new SharedPrefManager(prefs);
        }

        return mInstance;
    }

    public void saveUser(User user){

        editor.putInt("id", user.getId());
        editor.putString("first_name", user.getFirstName());
        editor.putString("last_name", user.getLastName());
        editor.putString("email", user.getEmail());
        editor.putString("mobile", user.getPhone());
        editor.putInt("gender", user.getGender());
        editor.putString("linkage_code", user.getCode());
        editor.putString("qr", user.getQr());
        editor.putString("kvp_status", user.getKvp());
        editor.putInt("isactive", user.getIsActivie());
        editor.putString("uuid", user.getUuid());
        editor.putString("created_at", user.getCreated_at());
        editor.putString("updated_at", user.getUpdated_at());
        editor.putString("role", user.getRole());

        editor.apply();
    }

    public void saveToken(Token token) {
        editor.putString("access_token", token.getAccessToken());

        editor.apply();
    }

    public User getUser(){
        User user = new User();

        user.setId(prefs.getInt("id", 0));
        user.setFirstName(prefs.getString("first_name", null));
        user.setLastName(prefs.getString("last_name", null));
        user.setEmail(prefs.getString("email", null));
        user.setPhone(prefs.getString("mobile", null));
        user.setGender(prefs.getInt("gender", 6));
        user.setCode(prefs.getString("linkage_code", null));
        user.setQr(prefs.getString("qr", null));
        user.setKvp(prefs.getString("kvp_status", null));
        user.setIsActivie(prefs.getInt("isactive", 1));
        user.setUuid(prefs.getString("uuid", null));
        user.setCreated_at(prefs.getString("created_at", null));
        user.setUpdated_at(prefs.getString("updated_at", null));
        user.setRole(prefs.getString("role", null));

        return user;
    }

    public Token getToken(){

        Token token = new Token();

        token.setAccessToken(prefs.getString("access_token", null));

        return token;
    }

    public void clear(){
        editor.clear();
        editor.apply();
    }

}
