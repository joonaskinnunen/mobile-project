package com.jk.mytattooartist;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;

public class FirstLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_login);

        // Hide the back button in action bar -JK
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

}
