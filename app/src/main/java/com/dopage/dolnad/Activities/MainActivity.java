package com.dopage.dolnad.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dopage.dolnad.Adapters.CouponsAdapter;
import com.dopage.dolnad.Interfaces.ApiService;
import com.dopage.dolnad.Interfaces.OnItemClickListener;
import com.dopage.dolnad.Models.Coupon;
import com.dopage.dolnad.Models.GenericResponse;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.DialogHelper;
import com.dopage.dolnad.Utils.NetworkHelper;
import com.dopage.dolnad.Utils.Permissions;
import com.dopage.dolnad.Utils.RetrofitClient;
import com.dopage.dolnad.Utils.SharedPrefManager;
import com.dopage.dolnad.Utils.StringManipulation;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    private static final String TAG = "MainActivity";
    private final static int VERIFY_PERMISSION_REQUEST = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private CoordinatorLayout coordinatorLayout;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutMessage;
    private LinearLayoutManager linearLayoutManager;
    private TextView textErrorMessage, textLinkageCode, textLinker, textLinked,
    textDateCreated, textDateUpdated;

    private ImageView errorIcon;

    private CouponsAdapter couponsAdapter;

//    private EditText nameEditText, statusEditText, phoneEditText;
//
//    private RadioGroup radioSexGroup;

    private AlertDialog progressDialog;
    private AlertDialog confirmLogoutDialog;
    private SharedPrefManager prefManager;
    private Call<GenericResponse> call;
    private ApiService apiService;

    private Toolbar toolbar;
    private Coupon coupon;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = SharedPrefManager.getInstance(
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );

        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.createServiceWithAuth(
                ApiService.class,
                MainActivity.this
        );

        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0,0,0,0);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Coupons");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!checkPermissionsArray(Permissions.PERMISSIONS)){
            verifyPermissionRequest();
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        drawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        buildProgressDialog();

//        nameEditText = findViewById(R.id.nameEditText);
//        statusEditText = findViewById(R.id.statusEditText);
//        phoneEditText = findViewById(R.id.phoneEditText);
//        radioSexGroup = findViewById(R.id.radioSex);
//        radioSexGroup.setOnCheckedChangeListener(this);

//        findViewById(R.id.generateQR).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(isFormValid()){
//                            if(NetworkHelper.isOnline(MainActivity.this))
//                                onStartRegisteringClient();
//                            else
//                                Toast.makeText(MainActivity.this, "There is no internet",
//                                        Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );

        navigationView = findViewById(R.id.navigationDrawer);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.action_coupons:
                                break;

                            case R.id.action_clients:
                                goToAnotherActivity(ClientsActivity.class);
                                break;
                        }


                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    }
                }
        );

        buildConfirmLogoutDialog();

        progressBar = findViewById(R.id.progressBar);
        layoutMessage = findViewById(R.id.layoutMessage);
        textErrorMessage = findViewById(R.id.textMessage);
        errorIcon = findViewById(R.id.icon);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        couponsAdapter = new CouponsAdapter(this);

        if(recyclerView != null)
            recyclerView.setAdapter(couponsAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onStartLoadingCoupons();
            }
        },200);


        final SwipeRefreshLayout swipeRefreshLayout
                = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#95ABD8"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onStartLoadingCoupons();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void goToAnotherActivity(Class activity){
        startActivity(new Intent(MainActivity.this, activity));
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

//    private void onStartRegisteringClient(){
//
//        if(!progressDialog.isShowing())
//            progressDialog.show();

//        ApiService apiService = RetrofitClient.createService(ApiService.class);
//        Call<GenericResponse> call = apiService.createUser(
//              nameEditText.getText().toString(),
//              gender,
//              phoneEditText.getText().toString(),
//                UUID.randomUUID().toString(),
//                statusEditText.getText().toString()
//        );

//        call.enqueue(new Callback<GenericResponse>() {
//            @Override
//            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
//                if(progressDialog.isShowing())
//                    progressDialog.dismiss();
//
//                if(response.isSuccessful()){
//                    onSuccessFullyRegistered(response.body());
//                }else{
////                    Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
//                    try {
//                       String errorMessage = response.errorBody().string();
//                        Toast.makeText(MainActivity.this,
//                                errorMessage, Toast.LENGTH_LONG).show();
//                        Log.d(TAG, "onResponse: Error : " + errorMessage);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GenericResponse> call, Throwable t) {
//                if(progressDialog.isShowing())
//                    progressDialog.dismiss();
//
//                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void onSuccessFullyRegistered(GenericResponse genericResponse){
        User user = genericResponse.getResult().getUser();
        String userString = new Gson().toJson(user);
        Intent intent = new Intent(MainActivity.this, GenerateCode.class);
        intent.putExtra("userString", userString);
        startActivity(intent);
    }

    private void buildProgressDialog(){
        progressDialog = DialogHelper.buildProgressDialog(
                MainActivity.this,
                getString(R.string.please_wait)
        );
    }


//    private boolean isFormValid(){
//        if(TextUtils.isEmpty(nameEditText.getText().toString())){
//            nameEditText.setError("Name is required");
//            nameEditText.requestFocus();
//            return false;
//        }else if(TextUtils.isEmpty(statusEditText.getText().toString())){
//            statusEditText.setError("Status is required");
//            phoneEditText.requestFocus();
//            return false;
//        }else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
//            phoneEditText.setError("Phone Number is required");
//            phoneEditText.requestFocus();
//            return false;
//        }else
//            return true;
//    }

    private String createJSONString(String name, int  gender, String status){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name)
                    .put("gender", gender)
                    .put("status", status);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void verifyPermissionRequest(){

        ActivityCompat.requestPermissions(
                MainActivity.this,
                Permissions.PERMISSIONS,
                VERIFY_PERMISSION_REQUEST
        );
    }

    private boolean checkPermissionsArray(String[] permissions){
        for(int i=0; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermission(check))
                return false;
        }

        return true;
    }

    private boolean checkPermission(String permission){

        int requestPermission = ActivityCompat.checkSelfPermission(MainActivity.this, permission);

        if(requestPermission != PackageManager.PERMISSION_GRANTED){
            return false;
        }else
            return true;
    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        switch (checkedId){
//            case R.id.radioMale:
//                gender = 6;
//                break;
//
//            case R.id.radioFemale:
//                gender = 7;
//                break;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1000, 0, 0,getString(R.string.logout));
        return true;
    }

    private void buildConfirmLogoutDialog(){

        confirmLogoutDialog = DialogHelper.buildConfirmDialog(
                MainActivity.this, getString(R.string.exit_app),
                true, getString(R.string.ok), getString(R.string.cancel)
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == 0){

            confirmLogoutDialog.show();

            confirmLogoutDialog.findViewById(R.id.positiveButton).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmLogoutDialog.dismiss();

                            if(NetworkHelper.isOnline(MainActivity.this))
                                 logout();
                            else{
                               finishLogout();
                            }

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

            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    private void logout(){

        progressDialog = DialogHelper.buildProgressDialog(MainActivity.this,
                getString(R.string.please_wait)
        );

        if(!progressDialog.isShowing())
            progressDialog.show();

        call = apiService.logout();

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                if(response.isSuccessful()){
                    finishLogout();
                }else{

                    // handle error
                    try {
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finishLogout();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {

                progressDialog.dismiss();
                showToastMessage(StringManipulation.fetchErrorMessage(t, MainActivity.this));
                finishLogout();

            }
        });

    }

    private void finishLogout(){
        prefManager.clear();
        startActivity(
                new Intent(MainActivity.this, LoginActivity.class)
        );
        finishAffinity();
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSnackBarMessage(String message){
        Snackbar.make(coordinatorLayout, message, 3000);
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
    public void onItemClick(int position) {

        coupon = couponsAdapter.getItem(position);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_coupon_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;

        textLinkageCode = dialog.findViewById(R.id.textLinkageCode);
        textLinker = dialog.findViewById(R.id.textLinker);
        textLinked = dialog.findViewById(R.id.textLinked);
        textDateCreated = dialog.findViewById(R.id.textDateCreated);
        textDateUpdated = dialog.findViewById(R.id.textDateUpdated);

        if(coupon.getIsLinked() == 0){
            Intent intent = new Intent(MainActivity.this, CreateClientActivity.class);
            String couponString = new Gson().toJson(coupon);
            intent.putExtra("couponString", couponString);
            startActivity(intent);
        }else{

            if(!progressDialog.isShowing())
                progressDialog.show();

            call = apiService.couponShow(coupon.getId());
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    if(response.isSuccessful()){
                        onSuccessFullCouponLoad(response.body());
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

                    showToastMessage(StringManipulation.fetchErrorMessage(t, MainActivity.this));
                }
            });

        }

    }

    private void onSuccessFullCouponLoad(GenericResponse response){
        Coupon newCoupon = response.getResult().getCoupon();

        if(newCoupon != null){
            textLinkageCode.setText(newCoupon.getLinkage_code());
            textLinker.setText(coupon.getLinker());
            textLinked.setText(newCoupon.getLinked());
            textDateCreated.setText(newCoupon.getCreatedAt());
            textDateUpdated.setText(newCoupon.getUpdatedAt());
        }

        dialog.show();
    }

    private void onStartLoadingCoupons(){
        showLoading();

        layoutMessage.setVisibility(View.GONE);

        couponsAdapter.clear();
        couponsAdapter.notifyDataSetChanged();

        ApiService apiService = RetrofitClient
                .createServiceWithAuth(ApiService.class, MainActivity.this);
        call = apiService.getCoupons();

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                hideLoading();
                if(response.isSuccessful()){
                    if(response.body().getResult().getCoupons() != null
                            && response.body().getResult().getCoupons().size() > 0){
                        onRetrievingCoupons(response.body().getResult().getCoupons());
                    }else
                        onErrorOccurred("No Coupons", R.drawable.ic_error_outline);
                }else{
                    onErrorOccurred("Something went wrong", R.drawable.ic_error_outline);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                hideLoading();

                if(!NetworkHelper.isOnline(MainActivity.this)) {

                    onErrorOccurred(
                            getString(R.string.cant_show_coupons)
                                    + "\n" + getString(R.string.error_msg_no_internet),
                            R.drawable.ic_cloud_off);
                    retrySnackBar(getString(R.string.internet_try_again));

                }else if(t instanceof TimeoutException){

                    onErrorOccurred(
                            getString(R.string.cant_show_coupons),
                            R.drawable.ic_error_outline);
                    retrySnackBar(StringManipulation.fetchErrorMessage(t, MainActivity.this));

                }else{

                    onErrorOccurred(
                            getString(R.string.cant_show_coupons)
                            , R.drawable.ic_error_outline);
                    retrySnackBar(StringManipulation.fetchErrorMessage(t, MainActivity.this));

                }


            }
        });
    }

    private void onRetrievingCoupons(List<Coupon> coupons){

        String couponTitle = coupons.size() + " Coupons";
        toolbar.setTitle(couponTitle);
        couponsAdapter.addAll(coupons);
        couponsAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void retrySnackBar(String message){
        Snackbar.make(coordinatorLayout,
                message,
                Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStartLoadingCoupons();
                    }
                }).show();
    }

    public void showLoading(){
        if(progressBar.getVisibility() == View.GONE)
            progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        if(progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    private void onErrorOccurred(String message, int imageView){
        recyclerView.setVisibility(View.GONE);
        layoutMessage.setVisibility(View.VISIBLE);
        textErrorMessage.setText(message);
        errorIcon.setImageResource(imageView);
    }

}
