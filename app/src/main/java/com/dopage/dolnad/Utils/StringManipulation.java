package com.dopage.dolnad.Utils;

import android.content.Context;

import com.dopage.dolnad.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

public class StringManipulation {

    public static String fetchErrorMessage(Throwable throwable, Context context) {
        String errorMsg = context.getString(R.string.error_msg_unknown);

        if (!NetworkHelper.isOnline(context)) {
            errorMsg = context.getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = context.getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    public static String formatDateString(String dateString){
        DateFormat outputFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());

        String outputText = null;
        try {
            Date date = inputFormat.parse(dateString);
            outputText = outputFormat.format(date);
            return outputText;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
