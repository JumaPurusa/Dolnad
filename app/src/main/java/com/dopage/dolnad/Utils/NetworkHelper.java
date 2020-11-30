package com.dopage.dolnad.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class NetworkHelper {

    // check network connection
    public static boolean isOnline(Context context){
        try{
            ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(context).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if(netInfo != null && netInfo.isConnected())
                return true;
            else
                return false;

        }catch (NullPointerException e){
            //e.printStackTrace();
            return false;
        }

    }
}