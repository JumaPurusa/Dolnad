package com.dopage.dolnad.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dopage.dolnad.Interfaces.ApiService;
import com.dopage.dolnad.Models.Coupon;
import com.dopage.dolnad.Models.GenericResponse;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.DialogHelper;
import com.dopage.dolnad.Utils.NetworkHelper;
import com.dopage.dolnad.Utils.RetrofitClient;
import com.dopage.dolnad.Utils.StringManipulation;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateClientActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private EditText firstNameEditText, lastNameEditText, mobileEditText, kvpStatusEditText;
    private TextView textLinkageCode, textLinkedBy;
    private CoordinatorLayout coordinatorLayout;
    private RadioGroup radioSexGroup;

    private Coupon coupon;
    private AlertDialog progressDialog;
    private Call<GenericResponse> call;

    private int gender = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_client);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Client Registration");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        kvpStatusEditText = findViewById(R.id.kvpStatusEditText);
        textLinkageCode = findViewById(R.id.textLinkageCode);
        textLinkedBy = findViewById(R.id.textLinkedBy);
        radioSexGroup = findViewById(R.id.radioSex);

        radioSexGroup.setOnCheckedChangeListener(this);

        progressDialog = DialogHelper.buildProgressDialog(
                CreateClientActivity.this,
                "Please Wait..."
        );

        if(getIntent().hasExtra("couponString")){
            coupon = new Gson().fromJson(
                    getIntent().getStringExtra("couponString"),
                    Coupon.class
            );

            textLinkageCode.setText("Linkage Code: " + coupon.getLinkage_code());
            textLinkedBy.setText("Linked By " + coupon.getLinker());
        }

        if(getIntent().hasExtra("from")){
            textLinkageCode.setVisibility(View.GONE);
            textLinkedBy.setVisibility(View.GONE);
        }


        findViewById(R.id.registerButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isFormValid()){
                            if(NetworkHelper.isOnline(CreateClientActivity.this))
                                onStartRegisteringClient();
                            else
                                showSnackBarMessage(getString(R.string.device_offline));

                        }
                    }
                }
        );
    }

    private void showSnackBarMessage(String message){
        Snackbar.make(coordinatorLayout,
                message,
                2000).show();
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
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isFormValid(){

        if(TextUtils.isEmpty(firstNameEditText.getText().toString())){
            firstNameEditText.setError("First Name is required");
            firstNameEditText.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(lastNameEditText.getText().toString())){
            lastNameEditText.setError("Last Name is required");
            lastNameEditText.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(mobileEditText.getText().toString())){
            mobileEditText.setError("Mobile number is required");
            mobileEditText.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(kvpStatusEditText.getText().toString())){
            kvpStatusEditText.setError("Kvp status is required");
            kvpStatusEditText.requestFocus();
            return false;
        }else
            return true;
    }

    private void onStartRegisteringClient(){

        if(!progressDialog.isShowing())
            progressDialog.show();

        ApiService apiService = RetrofitClient.createServiceWithAuth(ApiService.class,
                CreateClientActivity.this);

        call = apiService.registerClient(
                firstNameEditText.getText().toString(),
                lastNameEditText.getText().toString(),
                mobileEditText.getText().toString(),
                String.valueOf(gender),
                kvpStatusEditText.getText().toString(),
                coupon != null? coupon.getId() : 0
        );

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                if(response.isSuccessful()){
                    onSuccessfullyRegistered(response.body());
                }else{
                    try {
                        String errorString = response.errorBody().string();
                        Toast.makeText(CreateClientActivity.this,
                                errorString, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                showSnackBarMessage(StringManipulation.fetchErrorMessage(t,
                        CreateClientActivity.this));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radioMale:
                gender = 6;
                break;

            case R.id.radioFemale:
                gender = 7;
                break;
        }
    }

    private void onSuccessfullyRegistered(GenericResponse response){
        Toast.makeText(this,
                response.getMessage(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CreateClientActivity.this, ClientsActivity.class)
        .putExtra("from", "CreateClientActivity"));
        finishAffinity();
    }
}
