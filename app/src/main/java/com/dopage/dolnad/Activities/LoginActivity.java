package com.dopage.dolnad.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dopage.dolnad.Interfaces.ApiService;
import com.dopage.dolnad.Models.GenericResponse;
import com.dopage.dolnad.Models.Token;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.DialogHelper;
import com.dopage.dolnad.Utils.NetworkHelper;
import com.dopage.dolnad.Utils.RetrofitClient;
import com.dopage.dolnad.Utils.SharedPrefManager;
import com.dopage.dolnad.Utils.StringManipulation;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CoordinatorLayout coordinatorLayout;
    private AlertDialog progressDialog;
    private Call<GenericResponse> call;
    private SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = SharedPrefManager.getInstance(
                getSharedPreferences(
                        getString(R.string.app_name),
                        MODE_PRIVATE
                )
        );

        setSupportActionBar((Toolbar) findViewById(R.id.mToolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Login");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        progressDialog = DialogHelper.buildProgressDialog(
                LoginActivity.this,
                "Please Wait..."
        );

        findViewById(R.id.buttonLogin).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard();
                        if(isFormValid()){
                            if(NetworkHelper.isOnline(LoginActivity.this))
                                onStartLogin();
                            else
                                showSnackBarMessage(getString(R.string.device_offline));
                        }
                    }
                }
        );

        hideKeyboard();
    }

    private void showSnackBarMessage(String message){
        Snackbar.make(coordinatorLayout,
                message,
                2000).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1000, 0, 0, "Help");
        return true;
    }

    @Override
    public void onBackPressed() {
        // kill all activities to exit
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // exit app
                onBackPressed();
                return true;

            case 1000:
                Toast.makeText(LoginActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        View currentFocus = getCurrentFocus();

        if (inputManager != null && currentFocus != null)
            inputManager.hideSoftInputFromWindow(
                    currentFocus.getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private boolean isFormValid(){
        if(TextUtils.isEmpty(emailEditText.getText().toString())
            || TextUtils.isEmpty(passwordEditText.getText().toString())) {
            showSnackBarMessage(getString(R.string.please_enter_all_required_fields));
            return false;
        }else
            return true;
    }

    private void onStartLogin(){
        if(!progressDialog.isShowing())
            progressDialog.show();

        ApiService apiService = RetrofitClient.createService(ApiService.class);
        call = apiService.login(emailEditText.getText().toString(),
                passwordEditText.getText().toString());

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                if(response.isSuccessful()){
                    onSuccessFullyLogin(response.body());
                }else{
                    try {
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(LoginActivity.this,
                                errorMessage, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {

                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                showSnackBarMessage(StringManipulation.fetchErrorMessage(t, LoginActivity.this));
            }
        });
    }

    private void onSuccessFullyLogin(GenericResponse response){

        User user = response.getResult().getUser();
        if(user != null)
            prefManager.saveUser(user);

        Token token = response.getResult().getToken();
        if(token != null)
            prefManager.saveToken(token);

        startActivity(new Intent(LoginActivity.this, MainActivity.class));

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }
}
