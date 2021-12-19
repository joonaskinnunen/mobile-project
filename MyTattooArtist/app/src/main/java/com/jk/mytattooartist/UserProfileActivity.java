package com.jk.mytattooartist;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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

    public void changePasswordClicked(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        database.getReference().child("users").child("clients").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String emailAddress = String.valueOf(task.getResult().getValue());
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Email", "Email sent.");
                                        Toast.makeText(UserProfileActivity.this, "Password reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });
    }


    public void userInfo() {


        // get child references to DB -VS
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        DatabaseReference users = myRef.child("users");
        DatabaseReference clients = users.child("clients");
        DatabaseReference userID = clients.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        DatabaseReference name = userID.child("name");

        // Read title and full name from database and place it to views name field. -VS
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("first").getValue(String.class)+" "+ dataSnapshot.child("last").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileNameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });

        // Read city from database and place it to views city field. -VS
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("city").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileCityField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });

        // Read e-mail address from database and place it to views mail field. -VS
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("email").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileMailField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });

/*
        // Read username from database and place it to views username field. -VS
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("username").getValue(String.class);
                Log.i("VALUE: ", "Value is: " + value);
                TextView tv = findViewById(R.id.userProfileUsernameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("VALUE: ", "onCancelled", databaseError.toException());
            }
        });
        */

    }

}