package com.jk.mytattooartist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
BaseActivity that holds onCreateOptionsMenu and onOptionsItemSelected.
Also this class holds all Firebase related functions.
Every other class should extend this BaseActivity instead of AppCompatActivity.
 */

public class BaseActivity  extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
    final String[] userRole = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) getUserRoleFromDB(mAuth.getCurrentUser().getEmail());
        ActionBar actionBar = getSupportActionBar();

        // Showing the back button in action bar -JK
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myaccount:

                // TODO add condition to differ user / artist profile

                //"my account" will open intent for USER profile. -VS
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                return(true);

                // Opens a favourite page -JK
            case R.id.favourites:
                getFavourites();
                return(true);

                // Calls signOut function when logout selected -JK
            case R.id.logout:
                signOut();
                return(true);

                // Open MainActivity when back button is pressed -JK
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                super.onBackPressed();
        }

        return(super.onOptionsItemSelected(item));
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        /*        new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build(), */
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        // Successfully signed in
        if (result.getResultCode() == RESULT_OK) {
            Context context = getApplicationContext();
            String signInSuccessString = getString(R.string.sign_in_success);
            Toast toast = Toast.makeText(context, signInSuccessString, Toast.LENGTH_SHORT);
            toast.show();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
    // [END auth_fui_result]

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Context context = getApplicationContext();
                        String signOutSuccessString = getString(R.string.sign_out_success);
                        Toast toast = Toast.makeText(context, signOutSuccessString, Toast.LENGTH_SHORT);
                        toast.show();
                        createSignInIntent();
                    }
                });
        // [END auth_fui_signout]
    }

    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }

    public void getFavourites() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        // Read from the database
        myRef.child("users").child("artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                ArrayList<String> value = (ArrayList<String>) dataSnapshot.getValue();
                startFavourites(value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

    }

    public void startFavourites(ArrayList dbData) {
        Bundle extra = new Bundle();
        extra.putStringArrayList("array", dbData);
        Intent intent = new Intent(this, FavouriteActivity.class);
        intent.putExtra("Data", dbData);

        startActivity(intent);
    }

    public void getUserRoleFromDB(String email) {
        DatabaseReference artistUserRef = FirebaseDatabase.getInstance().getReference().child("users").child("artists");
        artistUserRef.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("datasnapShot artists", dataSnapshot.toString());
                    updateUserRoleCB("artist");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference clientUserRef = FirebaseDatabase.getInstance().getReference().child("users").child("clients");
        clientUserRef.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateUserRoleCB("client");
                    Log.d("datasnapShot clients", dataSnapshot.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void updateUserRoleCB(String role) {
        userRole[0] = role;
        Log.d("userRole: ", userRole[0]);
    }

    public String getUserRole() {
        return userRole[0];
    }
}
