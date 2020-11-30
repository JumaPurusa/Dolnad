package com.dopage.dolnad.Models;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
