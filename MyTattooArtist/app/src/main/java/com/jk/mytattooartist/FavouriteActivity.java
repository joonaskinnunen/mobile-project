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
import com.google.gson.JsonObject;
import org.json.JSONException;
import java.util.ArrayList;

public class FavouriteActivity extends BaseActivity {

    // Initialize recyclerView and FirebaseAuth -JK
    RecyclerView recyclerView;
    private FirebaseAuth mAuth;

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

        // Get the data from through intent
        Bundle extras = getIntent().getExtras();
        ArrayList arrayList = extras.getStringArrayList("Data");
        JsonArray jsonArray = new Gson().toJsonTree(arrayList).getAsJsonArray();

        // Get the user email -JK
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Get the users favourite artists from DB -JK
        myRef.child("users").child("clients").orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

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

                // Check if the user has any favourite artists -JK
                if (favouritesEmails != null) {

                    // Loop through all artists and compare artists emails to emails in favouritesEmails ArrayList -JK
                    for (int i = 0; i < favouritesEmails.size(); i++) {
                        for (int j = 0; j < arrayList.size(); j++) {
                            JsonObject jsonObj = jsonArray.get(j).getAsJsonObject();
                            String email = String.valueOf(jsonObj.get("email"));

                            // Remove extra characters from email string -JK
                            email = email.replaceAll("\"", "");

                            // If emails match, add emails to the filteredList -JK
                            if(favouritesEmails.get(i) != null) {
                                if (favouritesEmails.get(i).equals(email)) {
                                    filteredList.add(arrayList.get(j));
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
                JsonArray jsonArray2 = new Gson().toJsonTree(filteredList).getAsJsonArray();
                try {
                    recyclerView.setAdapter(new ArtistAdapter(jsonArray2));
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