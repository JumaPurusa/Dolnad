package com.dopage.dolnad.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dopage.dolnad.Adapters.ClientsAdapter;
import com.dopage.dolnad.Interfaces.ApiService;
import com.dopage.dolnad.Interfaces.OnItemClickListener;
import com.dopage.dolnad.Models.GenericResponse;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.NetworkHelper;
import com.dopage.dolnad.Utils.RetrofitClient;
import com.dopage.dolnad.Utils.StringManipulation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientsActivity extends AppCompatActivity implements OnItemClickListener {

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutMessage;
    private LinearLayoutManager linearLayoutManager;
    private TextView textErrorMessage;
    private ImageView errorIcon;

    private ClientsAdapter clientsAdapter;
    private ApiService apiService;
    private Call<GenericResponse> call;

    private String from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        apiService = RetrofitClient.createServiceWithAuth(ApiService.class,
                ClientsActivity.this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.clients));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("from")){
            from = getIntent().getStringExtra("from");
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        progressBar = findViewById(R.id.progressBar);
        layoutMessage = findViewById(R.id.layoutMessage);
        textErrorMessage = findViewById(R.id.textMessage);
        errorIcon = findViewById(R.id.icon);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        clientsAdapter = new ClientsAdapter(this);

        if(recyclerView != null)
            recyclerView.setAdapter(clientsAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onStartLoadingClients();
            }
        },200);


        final SwipeRefreshLayout swipeRefreshLayout
                = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#95ABD8"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onStartLoadingClients();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ClientsActivity.this,
                                CreateClientActivity.class)
                        .putExtra("from", "ClientsActivity"));
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if(from != null && from.contains("CreateClientActivity")){
            Intent intent = new Intent(ClientsActivity.this,
                    MainActivity.class);
            (ClientsActivity.this).finish();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }else
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onItemClick(int position) {

        User user = clientsAdapter.getItem(position);

        String userString = new Gson().toJson(user);
        Intent intent = new Intent(ClientsActivity.this, ClientDetailsActivity.class);
        intent.putExtra("userString", userString);
        startActivity(intent);
    }

    private void onStartLoadingClients(){
        showLoading();

        layoutMessage.setVisibility(View.GONE);

        clientsAdapter.clear();
        clientsAdapter.notifyDataSetChanged();

        call = apiService.getClients();

        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                hideLoading();
                if(response.isSuccessful()){
                    if(response.body().getResult().getClients() != null
                        && response.body().getResult().getClients().size() > 0){
                         onRetrievingClients(response.body().getResult().getClients());
                    }else
                        onErrorOccurred("No Clients", R.drawable.ic_error_outline);
                }else{
                    onErrorOccurred("Something went wrong", R.drawable.ic_error_outline);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                hideLoading();

                if(!NetworkHelper.isOnline(ClientsActivity.this)) {

                    onErrorOccurred(
                            getString(R.string.cant_show_clients)
                                    + "\n" + getString(R.string.error_msg_no_internet),
                            R.drawable.ic_cloud_off);
                    retrySnackBar(getString(R.string.internet_try_again));

                }else if(t instanceof TimeoutException){

                    onErrorOccurred(
                            getString(R.string.cant_show_clients),
                            R.drawable.ic_error_outline);
                    retrySnackBar(StringManipulation.fetchErrorMessage(t, ClientsActivity.this));

                }else{

                    onErrorOccurred(
                            getString(R.string.cant_show_clients)
                            , R.drawable.ic_error_outline);
                    retrySnackBar(StringManipulation.fetchErrorMessage(t,
                            ClientsActivity.this));

                }


            }
        });
    }

    private void onRetrievingClients(List<User> clients){

        clientsAdapter.addAll(clients);
        clientsAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void retrySnackBar(String message){
        Snackbar.make(coordinatorLayout,
                message,
                Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onStartLoadingClients();
                    }
                }).show();
    }
}
