package com.jk.mytattooartist;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

//Next code makes splash screen duration a bit longer -JM
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread splashThread=new Thread() {
            public void run() {
                try {
                    sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        };
        splashThread.start();



    }
}