package com.jk.mytattooartist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class UserUiForArtistActivity extends BaseActivity {

    // Initialize variables for the Views -JK
    private TextView profileNameTagTv;
    private TextView profileNameFieldTv;
    private TextView profileMailTagTv;
    private TextView profileMailFieldTv;
    private TextView profilePhoneTagTv;
    private TextView profilePhoneFieldTv;
    private TextView profileAddressTagTv;
    private TextView profileStreetFieldTv;
    private TextView profileCityFieldTv;
    private TextView profileSocialsTagTv;
    private ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set layout for the activity -JK
        setContentView(R.layout.user_ui_for_artist);

        // Get Views and save them to variables -JK

        profileNameTagTv = findViewById(R.id.profileNameTagTv);
        profileNameFieldTv = findViewById(R.id.profileNameFieldTv);
        profileMailTagTv = findViewById(R.id.profileMailTagTv);
        profileMailFieldTv = findViewById(R.id.profileMailFieldTv);
        profilePhoneTagTv = findViewById(R.id.profilePhoneTagTv);
        profilePhoneFieldTv = findViewById(R.id.profilePhoneFieldTv);
        profileAddressTagTv = findViewById(R.id.profileAddressTagTv);
        profileStreetFieldTv = findViewById(R.id.profileStreetFieldTv);
        profileCityFieldTv = findViewById(R.id.profileCityFieldTv);
        profileSocialsTagTv = findViewById(R.id.profileSocialsTagTv);
        imageView = findViewById(R.id.imageView2);

        // Set texts to TextViews with a constant value
        profileNameTagTv.setText("Name: ");
        profileMailTagTv.setText("Email: ");
        profilePhoneTagTv.setText("Phone: ");
        profileAddressTagTv.setText("Address: ");

        getArtistInfo();
    }

        // Function to get data from intent and update views -JK
        public void getArtistInfo() {
            // Initialize JSONObject that holds artist data -JK
            JSONObject artist = new JSONObject();
            // Get bundle from intent
            Bundle extras = getIntent().getExtras().getBundle("extra");
        try {
            // Get string "artist" from bundle and convert it to JSONObject to the artist variable -JK
            artist = new JSONObject(extras.getString("artist"));
            Log.d("artist", artist.toString());
        } catch (JSONException e) {
            // Catch error -JK
            e.printStackTrace();
        }

        /*
        Get data from JSONObject and try to update Views
        Each field is updated separately on its own try-catch block
        -JK
        */

        try {
            JSONObject name = new JSONObject(artist.getString("name"));
            profileNameFieldTv.setText(name.getString("first") + " " + name.getString("last"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            profileMailFieldTv.setText(artist.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            profilePhoneFieldTv.setText(artist.getString("phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject location = new JSONObject(artist.getString("location"));
            JSONObject street = new JSONObject(location.getString("street"));
            profileStreetFieldTv.setText(street.getString("name") + " " + street.getString("number") +"\n" + location.getString("city"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            profileSocialsTagTv.setText(artist.getString("socials"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
