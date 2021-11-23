package com.jk.mytattooartist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;

import java.util.ArrayList;

public class FrontPageActivity extends BaseActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab, fab1, fab2, fab3;
    Boolean isFABOpen = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
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

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }
}