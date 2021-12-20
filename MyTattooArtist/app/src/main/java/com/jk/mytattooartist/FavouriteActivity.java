package com.jk.mytattooartist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavouriteActivity extends BaseActivity {

    // Initialize recyclerView and FirebaseAuth -JK
    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    Gson gson = new Gson();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout file -JK
        setContentView(R.layout.favourite);

        // Get Firebase instance and add it to the mAuth variable -JK
        mAuth = FirebaseAuth.getInstance();

        // Access Firebase DB -JK
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        // Add LayoutManager to recyclerView -JK
        recyclerView = findViewById(R.id.recyclerViewFavourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String arrayList = getIntent().getExtras().getString("Data");

        // Get the user email -JK
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Get the users favourite artists from DB -JK
        myRef.child("users").child("clients").orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                List<String> keys = new ArrayList(map.keySet());
                ArrayList<Object> arList = new ArrayList<>();
                for (int i=0;i<map.size();i++) {
                    arList.add(map.get(keys.get(i)));
                }

                // Arraylist to hold users favourite artists emails -JK
                ArrayList<String> favouritesEmails;

                // Filteredlist to hold only users favorite artists objects -JK
                ArrayList<Object> filteredList = new ArrayList<>();

                String key = "0";

                // Get users object key in DB and save it to the variable -JK
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                }

                // Get users favourite artists emails from DB to the favouritesEmails variable -JK
                favouritesEmails = (ArrayList<String>) dataSnapshot.child(key).child("favourites").getValue();

                JSONArray arr = new JSONArray();
                try {
                    arr = new JSONArray(arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Check if the user has any favourite artists -JK
                if (favouritesEmails != null) {

                    // Loop through all artists and compare artists emails to emails in favouritesEmails ArrayList -JK
                    for (int i = 0; i < favouritesEmails.size(); i++) {
                        for (int j = 0; j < arr.length(); j++) {
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = new JSONObject(arr.get(j).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String email = null;
                            try {
                                email = String.valueOf(jsonObj.get("email"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Remove extra characters from email string -JK
                            email = email.replaceAll("\"", "");

                            // If emails match, add emails to the filteredList -JK
                            if(favouritesEmails.get(i) != null) {
                                if (favouritesEmails.get(i).equals(email)) {
                                    try {
                                        filteredList.add(arr.get(j));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    // Get the TextView and hide it when user has favourites -JK
                    TextView noFavouritesTv = findViewById(R.id.noFavouritesTextView);
                    noFavouritesTv.setVisibility(View.INVISIBLE);
                } else {
                    // Hide the RecyclerView when user has no favourites -JK
                    recyclerView.setVisibility(View.INVISIBLE);
                }

                // Convert ArrayList to JsonArray and add data to the adapter -JK
                try {
                    ArtistAdapter artistAdapter = new ArtistAdapter(filteredList.toString());
                    String json = gson.toJson(filteredList);
                    JsonArray filtered = gson.fromJson(json, JsonArray.class);
                    artistAdapter.favEmails = filtered;
                    recyclerView.setAdapter(artistAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });


    }
}