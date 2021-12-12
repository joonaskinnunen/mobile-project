package com.jk.mytattooartist;
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

public class UserProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mAuth = FirebaseAuth.getInstance();
        userInfo();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("user", currentUser.getDisplayName());
            //reload();
        } else {
            createSignInIntent();
        }
    }

    //TODO: Change PW


    public void userInfo() {


        // get child references to DB -VS
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        DatabaseReference users = myRef.child("users");
        DatabaseReference clients = users.child("clients");
        DatabaseReference userID = clients.child(mAuth.getCurrentUser().getUid());
        DatabaseReference name = userID.child("name");

        // Read title and full name from database and place it to views name field. -VS
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("title").getValue(String.class) +". "+ dataSnapshot.child("first").getValue(String.class)+" "+ dataSnapshot.child("last").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileNameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });

        // Read city from database and place it to views city field. -VS
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("city").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileCityField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });

        // Read e-mail address from database and place it to views mail field. -VS
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("email").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileMailField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });


        // Read username from database and place it to views username field. -VS
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("username").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileUsernameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });

    }

}