/*package com.jk.mytattooartist;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);

        recyclerView = findViewById(R.id.favouritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the Firebase data from through intent
        Bundle extras = getIntent().getExtras();
        ArrayList arrayList = extras.getStringArrayList("Data");
        ArrayList favouritesEmails = extras.getStringArrayList("favouritesEmails");
        ArrayList<String> filteredList = new ArrayList<>();
        if (favouritesEmails != null) {

            for(int i = 0; i < favouritesEmails.size(); i++) {
                for(int j = 0; j < arrayList.size(); j++) {
                    Log.d("value j", arrayList.get(j).toString());
                    if(favouritesEmails.get(i).equals(arrayList.get(j))) {
                        filteredList.add((String) arrayList.get(j));
                    }
                }
            }
        }
        JSONArray jsonArray2 = new JSONArray(filteredList);
        try {
            recyclerView.setAdapter(new ArtistAdapter(jsonArray2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("extras favourites: ", String.valueOf(arrayList.get(0)));
    }
}
*/