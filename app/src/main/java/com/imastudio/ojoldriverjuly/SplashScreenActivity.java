package com.imastudio.ojoldriverjuly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.imastudio.ojoldriverjuly.authentication.LoginRegisterActivity;
import com.imastudio.ojoldriverjuly.helper.SessionManager;


public class SplashScreenActivity extends AppCompatActivity {

    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        manager = new SessionManager(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (manager.isLogin()==true){
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashScreenActivity.this, LoginRegisterActivity.class));
                    finish();
                }
            }
        }, 4000);
    }
}
