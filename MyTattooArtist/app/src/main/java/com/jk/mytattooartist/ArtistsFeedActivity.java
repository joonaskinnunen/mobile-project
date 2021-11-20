package com.jk.mytattooartist;

import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;

public class ArtistsFeedActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artists_feed);
        mAuth = FirebaseAuth.getInstance();
    }
}
