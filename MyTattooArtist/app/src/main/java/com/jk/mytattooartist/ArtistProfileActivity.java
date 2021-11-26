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


public class ArtistProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_profile);
        mAuth = FirebaseAuth.getInstance();
        artistInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("user", currentUser.getDisplayName());
            //    reload();
        } else {
            createSignInIntent();
        }
    }

    public void artistInfo() {

        String user = "0";

        // Get references to the database -JM
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference();
        DatabaseReference users = myRef.child("users");
        DatabaseReference artists = users.child("artists");
        DatabaseReference userID = artists.child(user);
        DatabaseReference name = userID.child("name");
        DatabaseReference location = userID.child("location");
        DatabaseReference street = location.child("street");


        // Read name from the database -JM
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("title").getValue(String.class) + ". " + dataSnapshot.child("first").getValue(String.class) + " " + dataSnapshot.child("last").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileNameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

       // Read e-mail from the database -JM
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("email").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileMailField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read phone number from the database -JM
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("phone").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profilePhoneField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read street from the database -JM
        street.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                String value = dataSnapshot.child("name").getValue(String.class) + " " + dataSnapshot.child("number").getValue();
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileStreetField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read postcode and city from the database -JM
        location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                String value = dataSnapshot.child("postcode").getValue() + " " + dataSnapshot.child("city").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileCityField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }
}
