package com.jk.mytattooartist;

import android.os.Bundle;
import android.view.View;

import androidx.collection.ArrayMap;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public class FrontPageActivity extends BaseActivity {

    RecyclerView recyclerView;
    FloatingActionButton fabFilters, fabDistance, fabGender, fabStyles;
    Boolean isFABOpen = false;

    // Create an arraymap for storing floating action button views and
    // the popup layouts they refer to
    ArrayMap<View, Integer> arrayMap = new ArrayMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);
        ActionBar actionBar = getSupportActionBar();

        // Hide the back button in action bar -JK
        actionBar.setDisplayHomeAsUpEnabled(false);

        fabFilters = findViewById(R.id.fabFilters);
        fabDistance = findViewById(R.id.fabDistance);
        fabGender = findViewById(R.id.fabGender);
        fabStyles = findViewById(R.id.fabStyles);

        arrayMap.put(fabDistance, R.layout.popup_window_distance);
        arrayMap.put(fabGender, R.layout.popup_window_person);
        arrayMap.put(fabStyles, R.layout.popup_window_styles);

        // Iterate arraymap and set onclick listeners to the views it holds
        // The onclick methods create instances of popupclass and call showPopupWindow()
        // giving the respective layout as parameter
        for (int i=0; i<arrayMap.size(); i++) {
            View key = arrayMap.keyAt(i);
            key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Pop up window
                    PopUpClass popUpClass = new PopUpClass();
                    popUpClass.showPopupWindow(view, arrayMap.get(key));
                }
            });
        }
        // Set onclick listener to the fab that brings out the filter menu
        fabFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the Firebase data through intent
        Bundle extras = getIntent().getExtras();
        ArrayList<String> arrayList = extras.getStringArrayList("Data");
        JsonArray jsonArray2 = new Gson().toJsonTree(arrayList).getAsJsonArray();
        try {
            recyclerView.setAdapter(new ArtistAdapter(jsonArray2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // The animation for opening the filter menu
    private void showFABMenu(){
        isFABOpen=true;
        int y = -150;
        for (Map.Entry<View,Integer> entry: arrayMap.entrySet()) {
            entry.getKey().animate().translationY(y);
            y -= 140;
        }
    }

    // The animation for closing the filter menu
    private void closeFABMenu(){
        isFABOpen=false;
        for (Map.Entry<View,Integer> entry: arrayMap.entrySet()) {
            entry.getKey().animate().translationY(0);
        }
    }
}