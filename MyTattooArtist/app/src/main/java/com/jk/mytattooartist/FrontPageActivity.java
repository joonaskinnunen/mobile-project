package com.jk.mytattooartist;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;

import java.util.ArrayList;

public class FrontPageActivity extends BaseActivity {

    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the Firebase data from through intent
        Bundle extras = getIntent().getExtras();
        ArrayList arrayList = extras.getStringArrayList("Data");
        JsonArray jsonArray2 = new Gson().toJsonTree(arrayList).getAsJsonArray();
        try {
            recyclerView.setAdapter(new ArtistAdapter(jsonArray2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("GSON",String.valueOf(jsonArray2.get(0)));

        Log.d("extras", String.valueOf(arrayList.get(0)));
    }
}