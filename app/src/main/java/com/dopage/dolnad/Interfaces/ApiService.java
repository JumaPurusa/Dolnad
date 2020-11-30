package com.dopage.dolnad.Interfaces;

import com.dopage.dolnad.Models.GenericResponse;

import java.text.Normalizer;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @FormUrlEncoded
    @POST("register")
    Call<GenericResponse> registerClient(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("mobile") String mobile,
            @Field("gender") String gender,
            @Field("kvp_status") String kvp,
            @Field("coupon_id") int coupon_id
    );

    @FormUrlEncoded
    @POST("auth/login")
    Call<GenericResponse> login(
        @Field("email") String email,
        @Field("password") String password
    );

    @GET("auth/logout")
    Call<GenericResponse> logout();

    @Multipart
    @POST("{user}/store")
    Call<GenericResponse> uploadQR(@Path("user") int user_id, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("{user}/update")
    Call<GenericResponse> updateQR(@Path("user") int user_id, @Field("qr") String qr);

    @GET("users")
    Call<GenericResponse> getClients();

    @FormUrlEncoded
    @POST("{user}/coupon/create")
    Call<GenericResponse> createCoupon(@Path("user") int user_id, @Field("qty") int quantity);

    @GET("{user}/coupon/show")
    Call<GenericResponse> showCoupon(@Path("user") int user_id);

    @GET("{coupon}/coupon/showCoupon")
    Call<GenericResponse> couponShow(@Path("coupon") int coupon_id);

    @GET("coupons")
    Call<GenericResponse> getCoupons();
}
