package com.jk.mytattooartist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserUiForArtistActivity extends BaseActivity {

    // Initialize variables for the Views -JK
    private TextView profileNameTagTv;
    private TextView profileNameFieldTv;
    private TextView profileMailTagTv;
    private TextView profileMailFieldTv;
    private TextView profilePhoneTagTv;
    private TextView profilePhoneFieldTv;
    private TextView profileAddressTagTv;
   // private TextView profileStreetFieldTv;
    private TextView profileCityFieldTv;
    private TextView profileSocialsTagTv;
    private ImageView imageView;

    // Initialize variables for getting the instagram media -VS
    private RequestQueue requestQueue;
    private List<InstagramMedia> instagramMediaList;
    private RecyclerView recyclerView;


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
   //     profileStreetFieldTv = findViewById(R.id.profileStreetFieldTv);
        profileCityFieldTv = findViewById(R.id.profileCityFieldTv);
        profileSocialsTagTv = findViewById(R.id.profileSocialsTagTv);
        imageView = findViewById(R.id.imageView2);

        // Set texts to TextViews with a constant value
        profileNameTagTv.setText("Name: ");
        profileMailTagTv.setText("Email: ");
        profilePhoneTagTv.setText("Phone: ");
        profileAddressTagTv.setText("Location: ");

        // set up the variables for recycled view. -VS
        recyclerView = findViewById(R.id.RecyclerViewInstagramMediaUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        instagramMediaList = new ArrayList<>();

        getArtistInfo();
        getInstagramMedia();
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
        //    JSONObject location = new JSONObject(artist.getString("location"));
        //    JSONObject street = new JSONObject(location.getString("street"));
            profileCityFieldTv.setText(artist.getString("city"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            profileSocialsTagTv.setText(artist.getString("socials"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getInstagramMedia() {
        //get data from intent and fetch instagram media -VS

        // Initialize JSONObject that holds artist data -VS
        JSONObject artist = new JSONObject();
        // Get bundle from intent
        Bundle extras = getIntent().getExtras().getBundle("extra");
        try {
            // Get string "artist" from bundle and convert it to JSONObject to the artist variable -VS
            artist = new JSONObject(extras.getString("artist"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get access_token from selected artis, if it exists.
        //Call fetchInstagramMedia(); method and fetch instagram media to recycler view. -VS
        try {
            JSONObject ig = new JSONObject(artist.getString("instagram"));
            String token = ig.getString("access_token");
            fetchInstagramMedia(token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchInstagramMedia(final String token) {

        // Send GET request to graph.instagram to receive JSon object containing media ids and captions. of certain user. -VS
        //user is defined by the access token. -VS
        //Received ids and captions are passed down to another method alongside with the used token. -VS

        String url = "https://graph.instagram.com/me/media?fields=id,caption&access_token="+token;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                            for(int i = 0 ; i < dataArray.length() ; i ++){         //loop through every id and call fetchInstagramURL() for each of them.
                                JSONObject data = dataArray.getJSONObject(i);
                                String dataID =data.getString("id");
                                String dataCaption=null;
                                if(data.getString("caption")!=null){
                                    dataCaption =data.getString("caption");
                                }
                                if(dataCaption.contains("#")){
                                    dataCaption =dataCaption.substring(0,dataCaption.indexOf("#"));
                                }
                                fetchInstagramURL(dataID,dataCaption,token);
                                //Log.i("fetching media", dataID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("problem in when fetching media","Exception");
                Toast.makeText(UserUiForArtistActivity.this, "Failed to load Artists instagram media", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private void fetchInstagramURL(String id,String caption,final String token) {

        //Using id and token from parameters, make another Get request to receive URL and media type corresponding the media ID. -VS
        //Check if media_type==IMAGE  (VIDEO and CAROUSEL_ALBUM are skipped) -VS
        //Use InstagramMedia Class to store the information and store objects into <List> that will be passed to recyclerView adapter. -VS
        //Every object will be passed to the adapter separately. -VS

        String url = "https://graph.instagram.com/"+id+"?fields=media_type,media_url&access_token="+token;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.i("fetching media", id);
                        try {
                            if(response.getString("media_type").contains("IMAGE")){
                                String mediaURL = response.getString("media_url");

                                InstagramMedia instagramMedia = new InstagramMedia(id , caption , mediaURL);
                                instagramMediaList.add(instagramMedia);

                                //Log.i("fetching media", mediaURL);
                                InstagramAdapter adapter = new InstagramAdapter(UserUiForArtistActivity.this , instagramMediaList);
                                recyclerView.setAdapter(adapter);

                            }else
                                Log.i("fetching media", "notIMAGE");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserUiForArtistActivity.this, "Failed to load Artists instagram media", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }
}
