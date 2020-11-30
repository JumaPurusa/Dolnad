package com.dopage.dolnad.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.dopage.dolnad.R;

public class DialogHelper {

    public static AlertDialog buildProgressDialog(Activity activity, String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);

        builder.setView(dialogView).setCancelable(false);

        ((TextView)dialogView.findViewById(R.id.textDialogMessage)).setText(message);

        Dialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;

        return (AlertDialog)dialog;
    }

    public static AlertDialog buildConfirmDialog(Activity activity, String message, boolean isCancelable,
                                                 String positiveText, String negativeText){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.layout_confirm_dialog, null);
        dialogBuilder.setView(dialogView).setCancelable(isCancelable);

        ((TextView)dialogView.findViewById(R.id.textDialogMessage)).setText(message);
        ((TextView)dialogView.findViewById(R.id.positiveButton)).setText(positiveText);
        ((TextView)dialogView.findViewById(R.id.negativeButton)).setText(negativeText);

        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;

        return (AlertDialog) dialog;
    }

    public static AlertDialog createCouponDialog(Activity activity,  boolean isCancelable,
                                                 String createText){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.layout_coupon_create, null);
        dialogBuilder.setView(dialogView).setCancelable(isCancelable);

        ((TextView)dialogView.findViewById(R.id.positiveButton)).setText(createText);


        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;

        return (AlertDialog) dialog;
    }

}
