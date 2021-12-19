package com.jk.mytattooartist;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FirstLoginActivity extends BaseActivity {

    // Initialize variables -JK
    private FirebaseAuth mAuth;
    DatabaseReference myRef = database.getReference();
    RadioGroup radioGroup;
    RadioButton artistRadioButton;
    RadioButton clientRadioButton;
    String selectedLocation = null;
    LatLng selectedLatLng = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_login);

        // Get Firebase instance to the variable -JK
        mAuth = FirebaseAuth.getInstance();

        // Get views to variables -JK
        radioGroup = findViewById(R.id.radioGroup);
        clientRadioButton = findViewById(R.id.radioButton);
        artistRadioButton = findViewById(R.id.radioButton2);

        // Hide the back button in action bar -JK
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        // Set background color to light gray -JM
        View rootView = (View) findViewById(android.R.id.content);
        rootView.setBackgroundColor(getResources().getColor(R.color.light_gray));



        // Initialize app variable -JK
        ApplicationInfo app = null;
        try {
            app = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Save app metadata to bundle variable -JK
        Bundle bundle = app.metaData;

        // Initialize places -JK
        Places.initialize(getApplicationContext(), bundle.getString("com.jk.mytattooartist.API_KEY"));

        // Initialize the AutocompleteSupportFragment -JK
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return -JK
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME));

        // Filter places and return only cities -JK
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);

        // Set up a PlaceSelectionListener to handle the response -JK
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Log.d("LatLng", String.valueOf(place.getLatLng()));

                // Store place name and LatLng location to variables -JK
                selectedLocation = place.getName();
                selectedLatLng = place.getLatLng();
                Log.i("PLACEAPI", "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NonNull Status status) {
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

        // Check if user has selected location -JK
        if(selectedLocation != null) {

            TextView firstNameTv = findViewById(R.id.editTextFirstName);
            TextView lastNameTv = findViewById(R.id.editTextLastName);
            TextView phoneTv = findViewById(R.id.editTextPhone);

            // Create new object from Name class -JK
            Name name = new Name(firstNameTv.getText().toString(), lastNameTv.getText().toString());

            ArrayList<String> favArr = new ArrayList<>();
            favArr.add("marcus.edwards@example.com");

            // Create new object from User class -JK
            User user = new User(firebaseUser.getEmail(), name, selectedLocation, selectedLatLng, phoneTv.getText().toString(), favArr);

        // If user selected client as user role, add new user to the database path 'users/clients' -JK
        if(selectedRadioButtonId == clientRadioButton.getId()) {

            // Check if user has inputted both names and phone number -JK
            if (firstNameTv.getText().length() < 2 || lastNameTv.getText().length() < 2 || phoneTv.getText().length() < 5) {
                Toast toast = Toast.makeText(this, R.string.nameInputError, Toast.LENGTH_SHORT);
                toast.show();
            } else {

                // Store user information to the DB  -JK
                myRef.child("users").child("clients").child(mAuth.getCurrentUser().getUid()).setValue(user);

                // Create Intent and go back to MainActivity -JK
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }

        // If user selected artist as user role, add new user to the database path 'users/artists' -JK
        else if(selectedRadioButtonId == artistRadioButton.getId()) {
            // Check if user has inputted both names and phone number -JK
            if (firstNameTv.getText().length() < 2 || lastNameTv.getText().length() < 2 || phoneTv.getText().length() < 5) {
                Toast toast = Toast.makeText(this, R.string.nameInputError, Toast.LENGTH_SHORT);
                toast.show();
            } else {
            // Store user information to the DB  -JK
            myRef.child("users").child("artists").child(mAuth.getCurrentUser().getUid()).setValue(user);

            // Create Intent and go back to MainActivity -JK
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            }
        }

        // If user didn't select either user role, show Toast -JK
        else {
            Toast toast = Toast.makeText(this, R.string.userRoleSelectionError, Toast.LENGTH_SHORT);
            toast.show();
        }
        }

        // If user didn't select any location, show Toast -JK
        else {
            Toast toast = Toast.makeText(this, R.string.locationSelectionError, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // Static class for creating new users -JK
    public static class User {

        public String email;
        public Name name;
        public String city;
        public LatLng latLng;
        public String phone;
        public ArrayList favourites;

        public User(String email, Name name, String city, LatLng latLng, String phone, ArrayList<String> favourites) {
            this.email = email;
            this.name = name;
            this.city = city;
            this.latLng = latLng;
            this.phone = phone;
            this.favourites = favourites;

        }

    }

    // Static class for creating Name objects -JK
    public static class Name {

        public String first;
        public String last;

        public Name(String firstName, String lastName) {
            this.first = firstName;
            this.last = lastName;

        }

    }

}
