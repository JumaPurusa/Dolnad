package com.dopage.dolnad.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.SharedPrefManager;

public class SplashScreen extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPrefManager prefManager =
                SharedPrefManager.getInstance(getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE));

        setContentView(R.layout.activity_spash_screen);

//        Typewriter typewriter = findViewById(R.id.typewriter);
//        //Add a character every 150ms
//        typewriter.setCharacterDelay(150);
//        typewriter.animateText("ICAP");

        ImageView imageView = findViewById(R.id.imageView);
        imageView.startAnimation(AnimationUtils.loadAnimation(
                this,
                R.anim.splash
        ));

        runnable = new Runnable() {
            @Override
            public void run() {

                if(prefManager.getToken().getAccessToken() != null)
                    startActivity(
                            new Intent(SplashScreen.this, MainActivity.class)
                                    .putExtra("from", "splash")
                    );
                else
                    startActivity(
                            new Intent(SplashScreen.this, LoginActivity.class)
                    );

                finish();

            }

        };

        handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }
}
