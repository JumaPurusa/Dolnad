package com.dopage.dolnad.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private Token token;

    @SerializedName("clients")
    private List<User> clients;

    @SerializedName("coupons")
    private List<Coupon> coupons;

    @SerializedName("coupon")
    private Coupon coupon;

    public User getUser() {
        return user;
    }

    public Token getToken() {
        return token;
    }

    public List<User> getClients() {
        return clients;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public Coupon getCoupon() {
        return coupon;
    }
}
