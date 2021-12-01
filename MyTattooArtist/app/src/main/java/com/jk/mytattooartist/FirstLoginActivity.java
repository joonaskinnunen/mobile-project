package com.jk.mytattooartist;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;

public class FirstLoginActivity extends BaseActivity {

    // Initialize variables -JK
    private FirebaseAuth mAuth;
    DatabaseReference myRef = database.getReference();
    RadioGroup radioGroup;
    RadioButton artistRadioButton;
    RadioButton clientRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_login);

        // Get Firebase instance to the variable
        mAuth = FirebaseAuth.getInstance();

        // Get views to variables -JK
        radioGroup = findViewById(R.id.radioGroup);
        clientRadioButton = findViewById(R.id.radioButton);
        artistRadioButton = findViewById(R.id.radioButton2);

        // Hide the back button in action bar -JK
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Initialize app variable -JK
        ApplicationInfo app = null;
        try {
            app = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Save app metadata to bundle variable -JK
        Bundle bundle = app.metaData;

        Places.initialize(getApplicationContext(), bundle.getString("com.jk.mytattooartist.API_KEY"));

        // Initialize the AutocompleteSupportFragment -JK
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return -JK
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response -JK
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACEAPI", "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("PLACEAPI", "An error occurred: " + status);
            }
        });
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

    // OnClick function. Called when user selects their role -JK
    public void onRoleSelected(View view) {

        // Get selected RadioButton id from RadioGroup -JK
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        // Get logged in FirebaseUser -JK
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // Create new object from User class -JK
        User user = new User(firebaseUser.getEmail(), firebaseUser.getDisplayName());

        // If user selected client as user role, add new user to the database path 'users/clients' -JK
        if(selectedRadioButtonId == clientRadioButton.getId()) {
            myRef.child("users").child("clients").child(mAuth.getCurrentUser().getUid()).setValue(user);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // If user selected artist as user role, add new user to the database path 'users/artists' -JK
        else if(selectedRadioButtonId == artistRadioButton.getId()) {
            myRef.child("users").child("artists").child(mAuth.getCurrentUser().getUid()).setValue(user);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

        // If user didn't select either user role, show Toast -JK
        else {
            Toast toast = Toast.makeText(this, R.string.userRoleSelectionError, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // Static class for creating new users -JK
    public static class User {

        public String email;
        public String name;

        public User(String email, String name) {
            this.email = email;
            this.name = name;

        }

    }

}
