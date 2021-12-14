package com.jk.mytattooartist;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getDataFromDB();
        }
        // If user is not signed in then call createSignInIntent() -JK
        else {
            createSignInIntent();
        }

       /*  Intent intent = new Intent(this, FirstLoginActivity.class);
        startActivity(intent); */
    }

    public void getDataFromDB() {

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        // Read from the database values inside "artists"
        myRef.child("users").child("artists").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    // If there are no values, set error message into textview. If success, goto startFrontpage()
                    TextView tv = findViewById(R.id.textView);
                //    ArrayList<String> value = new ArrayList(Collections.singleton(((Map<String, ClipData.Item>) task.getResult().getValue())));
                    JSONObject obj=new JSONObject((Map<String, ClipData.Item>)task.getResult().getValue());
                    JSONArray array = new JSONArray();
                    try {
                        array=new JSONArray("["+obj.toString()+"]");
                        Log.d("JSONArray", String.valueOf(array));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //    JSONArray arr = new JSONArray();
                //    arr = (JSONArray) task.getResult().getValue();
                    Log.d("obj", String.valueOf(obj));
                //    Log.d("arrlen", String.valueOf(value.size()));
                //    Log.d("arraylist", String.valueOf(value));
                    if (array != null) startFrontPage(array);
                    else tv.setText("Ei dataa");
                }
                }




            });
    }

    // Take the data to frontpage activity
    public void startFrontPage(JSONArray dbData) {

        Bundle extra = new Bundle();
        extra.putString("array", String.valueOf(dbData));
        Intent intent = new Intent(this, FrontPageActivity.class);
        intent.putExtra("Data", extra);

        startActivity(intent);
    }
}