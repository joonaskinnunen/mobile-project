package com.jk.mytattooartist;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       if(currentUser != null){
            getDataFromDB();
        }
        // If user is not signed in then call createSignInIntent() -JK
        else {
            createSignInIntent();
        }

       /*  Intent intent = new Intent(this, FirstLoginActivity.class);
        startActivity(intent); */
    }

    public void getDataFromDB() {
      
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        // Read from the database values inside "artists"
        myRef.child("users").child("artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Get values as a String ArrayList
                ArrayList<String> value = new ArrayList(((Map<String, ClipData.Item>) dataSnapshot.getValue()).values());

                // If there are no values, set error message into textview. If success, goto startFrontpage()
                TextView tv = findViewById(R.id.textView);
                if (value != null) startFrontPage(value); else tv.setText("Ei dataa");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }
    // Take the data to frontpage activity
    public void startFrontPage(ArrayList<String> dbData) {

        Bundle extra = new Bundle();
        extra.putStringArrayList("array", dbData);
        Intent intent = new Intent(this, FrontPageActivity.class);
        intent.putExtra("Data", dbData);

        startActivity(intent);
    }
}