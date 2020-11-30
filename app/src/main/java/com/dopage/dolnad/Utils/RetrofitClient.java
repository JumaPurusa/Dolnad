package com.dopage.dolnad.Utils;

import android.content.Context;

import com.dopage.dolnad.BuildConfig;
import com.dopage.dolnad.Interfaces.ApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/api/";

    private final static OkHttpClient okHttpClient = buildClient();

    private final static Retrofit retrofit = buildRetrofit(okHttpClient);

    private static OkHttpClient buildClient(){

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor
                = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

        if(BuildConfig.DEBUG)
            httpBuilder.addInterceptor(httpLoggingInterceptor);

        httpBuilder.connectTimeout(10, TimeUnit.SECONDS);
        httpBuilder.readTimeout(10, TimeUnit.SECONDS);
        httpBuilder.writeTimeout(10, TimeUnit.SECONDS);

        return httpBuilder.build();
    }

    private static Retrofit buildRetrofit(OkHttpClient client){

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static ApiService createService(Class<ApiService> service){
        return retrofit.create(service);
    }

    public static ApiService createServiceWithAuth(Class<ApiService> service, Context context){

        AuthenticationInterceptor authenticationInterceptor
                = new AuthenticationInterceptor(context);

        OkHttpClient newClient = null;

        if(!okHttpClient.interceptors().equals(authenticationInterceptor)){
            newClient = okHttpClient.newBuilder()
                    .addInterceptor(authenticationInterceptor)
                    .build();
        }

        Retrofit newRetrofit = retrofit.newBuilder()
                .client(newClient)
                .build();

        return newRetrofit.create(service);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
