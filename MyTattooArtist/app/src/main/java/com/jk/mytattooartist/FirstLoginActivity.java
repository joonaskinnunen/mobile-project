package com.jk.mytattooartist;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    /*    getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem myAccountItem = menu.findItem(R.id.myaccount);
        MenuItem favouritesItem = menu.findItem(R.id.favourites);
        favouritesItem.setVisible(false);
        myAccountItem.setVisible(false); */
        return true;
    }

}
