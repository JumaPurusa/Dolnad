package com.dopage.dolnad.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.dopage.dolnad.Activities.LoginActivity;
import com.dopage.dolnad.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private SharedPrefManager prefManager;
    private Context mContext;

    public AuthenticationInterceptor(Context context){
        this.mContext = context;
        this.prefManager = SharedPrefManager.getInstance(
                mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE)
        );
    }

    @NotNull
    @Override
    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {

        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .header("Authorization", "Bearer " + prefManager.getToken().getAccessToken());

        Request request = builder.build();

        Response response = chain.proceed(request);

        if(response.code() == 500) {

            //Toast.makeText(mContext, "Server is broken", Toast.LENGTH_SHORT).show();
//            onClearEverything();

        }

        if(response.code() == 401){
            onClearEverything();
        }

        return response;
    }

    private void onClearEverything(){
        prefManager.clear();

        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

}
