package com.dopage.dolnad.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dopage.dolnad.Interfaces.ApiService;
import com.dopage.dolnad.Models.Coupon;
import com.dopage.dolnad.Models.GenericResponse;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.DialogHelper;
import com.dopage.dolnad.Utils.NetworkHelper;
import com.dopage.dolnad.Utils.RetrofitClient;
import com.dopage.dolnad.Utils.StringManipulation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientDetailsActivity extends AppCompatActivity {

    TextView textName, textMobile, textGender, textStatus, textLinkageCode,
                textLinkedBy;

    private FloatingActionButton fab;
    private AlertDialog confirmLogoutDialog;
    private AlertDialog progressDialog;
    private ApiService apiService;
    private Call<GenericResponse> call;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);

        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, ClientDetailsActivity.this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.client_details));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        textName = findViewById(R.id.textName);
        textMobile = findViewById(R.id.textMobile);
        textGender = findViewById(R.id.textGender);
        textStatus = findViewById(R.id.textStatus);
        textLinkageCode = findViewById(R.id.textLinkageCode);
        textLinkedBy = findViewById(R.id.textLinkedBy);

        fab = findViewById(R.id.fab);


        if(getIntent().hasExtra("userString")){
            String userString = getIntent().getStringExtra("userString");
            user = new Gson().fromJson(userString, User.class);
        }

        if(user != null) {
            setProperties(user);
            loadCouponInformation();
        }


        buildConfirmLogoutDialog();
        buildProgressDialog();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogoutDialog.show();

                confirmLogoutDialog.findViewById(R.id.positiveButton).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmLogoutDialog.dismiss();
                                openCreateCouponDialog();
                            }
                        }
                );

                confirmLogoutDialog.findViewById(R.id.negativeButton).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmLogoutDialog.dismiss();
                            }
                        }
                );
            }
        });

    }

    private void buildProgressDialog(){
        progressDialog = DialogHelper.buildProgressDialog(
                ClientDetailsActivity.this,
                getString(R.string.please_wait)
        );
    }

    private void openCreateCouponDialog(){

        final AlertDialog createCouponDialog = DialogHelper.createCouponDialog(
                ClientDetailsActivity.this,
                true,
                "CREATE"
        );

        createCouponDialog.show();

        final EditText couponEditText = createCouponDialog.findViewById(R.id.textCouponQuantity);

        createCouponDialog.findViewById(R.id.positiveButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createCouponDialog.dismiss();
                        onStartUploadingCoupon(couponEditText.getText().toString());

                    }
                }
        );
    }

    private void onStartUploadingCoupon(String couponQty){
        if(!progressDialog.isShowing())
            progressDialog.show();

        call = apiService.createCoupon(user.getId(), Integer.valueOf(couponQty));

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                if(response.isSuccessful()){
                    onSuccessFullyCouponCreate(response.body());
                }else{
                    try {
                        String errorString = response.errorBody().string();
                        showToastMessage(errorString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {

                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void onSuccessFullyCouponCreate(GenericResponse genericResponse){

        showToastMessage("Coupons SuccessFully created");

        startActivity(new Intent(ClientDetailsActivity.this, MainActivity.class));
        finishAffinity();
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setProperties(User user){
        String fullName = user.getFirstName() + " " + user.getLastName();
        textName.setText(fullName);
        textMobile.setText(user.getPhone());

        if(user.getGender() == 6)
            textGender.setText("Male");
        else if(user.getGender() == 7)
            textGender.setText("Female");

        textStatus.setText(user.getKvp());
        textLinkageCode.setText(user.getCode());
        textLinkedBy.setText(" ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    private void buildConfirmLogoutDialog(){

        confirmLogoutDialog = DialogHelper.buildConfirmDialog(
                ClientDetailsActivity.this,
                "Do you want to create coupons for this user",
                true, getString(R.string.ok), getString(R.string.cancel)
        );
    }

    private void loadCouponInformation(){

        call = apiService.showCoupon(user.getId());

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                if(response.isSuccessful()){
                    Coupon coupon = response.body().getResult().getCoupon();

                    if(coupon != null){
                        textLinkageCode.setText(coupon.getLinkage_code());
                        textLinkedBy.setText(coupon.getLinker());
                    }else{
                        textLinkageCode.setText("N/A");
                        textLinkedBy.setText("N/A");
                    }

                }else{
                    try {
                        String errorString = response.errorBody().string();
                        showToastMessage(errorString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {

                showToastMessage(StringManipulation.fetchErrorMessage(t, ClientDetailsActivity.this));
            }
        });
    }

}
