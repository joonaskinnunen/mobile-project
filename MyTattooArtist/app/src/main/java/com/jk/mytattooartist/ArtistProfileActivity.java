package com.jk.mytattooartist;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ArtistProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    private final String appId ="602591627694835";
    private final String redirectUrl = "https://mytattooartist-d2298.firebaseapp.com/__/auth/handler";
    private final String authUrl ="https://api.instagram.com/oauth/authorize?client_id="+appId+"&redirect_uri="+ redirectUrl+ "&scope=user_profile,user_media&response_type=code";
    private final String checkUrl ="https://mytattooartist-d2298.firebaseapp.com/__/auth/handler?code=";
    private String authCode;
    private final String igAppSecret = "1a302a11f9d6b0f0906987353193ee60";
    private String userID;
    private String token;
    private String longLiveToken;
    private RequestQueue requestQueue;
    private List<InstagramMedia> instagramMediaList;
    private RecyclerView recyclerView;
    private String tokenExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_profile);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.RecyclerViewInstagramMedia);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        instagramMediaList = new ArrayList<>();
        findViewById(R.id.userProfileChangePasswordButton2).setVisibility(View.VISIBLE);

        artistInfo();
        tokenExists();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //TODO: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.jk.mytattooartist/com.jk.mytattooartist.ArtistProfileActivity}: java.lang.NullPointerException: println needs a message
            Log.d("user", currentUser.getDisplayName());
        } else {
            createSignInIntent();
        }
    }

    private void tokenExists() {

        //Fetch Token of a current user from Firebase -VS
        //If the token doesn't exists, set connect to instagram button visible. -VS
        // if the token exists, start the process for getting the instagram media. -VS


        database.getReference().child("users").child("artists").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("instagram").child("access_token").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    findViewById(R.id.connectToInstagramButton).setVisibility(View.VISIBLE);
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    tokenExists = String.valueOf(task.getResult().getValue());
                    if(!tokenExists.equals("null")){

                        fetchInstagramMedia(tokenExists);
                    }else
                        findViewById(R.id.connectToInstagramButton).setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void fetchInstagramMedia(final String token) {

        // Send GET request to graph.instagram to receive JSon object containing media ids and captions. of certain user. -VS
        //(user is defined by the access token. -VS
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
                                String dataCaption;
                                data.getString("caption");
                                dataCaption =data.getString("caption");
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
                Toast.makeText(ArtistProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                                InstagramAdapter adapter = new InstagramAdapter(ArtistProfileActivity.this , instagramMediaList);
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
                Toast.makeText(ArtistProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    public void ConnectToInstagramClicked(View view){
        //Button to bring up the Instagram authentication window. -VS
        //If Authentication is already done OR the button is clicked, the button will be set to INVISIBLE -VS
        Button button = findViewById(R.id.connectToInstagramButton);
        findViewById(R.id.userProfileChangePasswordButton2).setVisibility(View.INVISIBLE);
        button.setVisibility(view.INVISIBLE);
        artistInstagramPermission();
    }


    //Enable JS so instagram auth can be used in web view. -VS
    @SuppressLint("SetJavaScriptEnabled")
    private void artistInstagramPermission() {
        WebView myWebView = (WebView) findViewById(R.id.instagramFeedWebView);
        myWebView.setVisibility(View.VISIBLE);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //Open IG auth to Web view -VS
        myWebView.loadUrl(authUrl);
        Log.i("VALUE: ", "AuthPage loaded!");

        myWebView.setWebViewClient(new WebViewClient() {
            String auth_code;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, authUrl, favicon);
            }

            //get Auth code from url after giving app permission to use instagram profile and media data -VS
            public void onPageFinished(WebView view, String viewUrl) {
                Log.i("VALUE: ", "URL open!");
                if(myWebView.getUrl().contains("access_denied")){
                    myWebView.setVisibility(View.GONE);
                    Toast.makeText(ArtistProfileActivity.this, "Authentication Failed, please try again.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.connectToInstagramButton).setVisibility(View.VISIBLE);
                }
                else if(viewUrl.contains(checkUrl)){
                    auth_code = viewUrl.substring(viewUrl.indexOf("code=")+5,viewUrl.length()-2);
                    Log.i("Auth_Code: ", auth_code);
                    authCode=auth_code;
                    requestAccessToken(authCode);
                    myWebView.destroy();
                }
            }
        });
    }

    //exchange code to access token with POST using Volley, get token + userId in respond. -VS
    public void requestAccessToken(final String authCode) {
        String postUrl = "https://api.instagram.com/oauth/access_token";
        StringRequest request = new StringRequest(Request.Method.POST, postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO: condition if fails to get token -> "error_type"

                //"response" is Access_token + UserID in JSON format -VS
                Log.i("Success Response = ", response);
                token = response.substring(response.indexOf("\"access_token\": \"")+17,response.indexOf("\","));
                userID= response.substring(response.indexOf("\"user_id\":")+11,response.length()-1);
                Log.i("token = ", token);
                Log.i("userID = ", userID);
                requestLongLiveToken(igAppSecret,token);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error Response = ", error.toString());
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id", appId);
                params.put("client_secret", igAppSecret);
                params.put("grant_type", "authorization_code");
                params.put("redirect_uri", redirectUrl);
                params.put("code", authCode);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    public void requestLongLiveToken(final String appSecret,final String token) {
        //Exchange short lived token (which lasts for 1 hour, to long lived token that lasts 60 days.
        String postUrl = "https://graph.instagram.com/access_token?grant_type=ig_exchange_token&client_secret="+appSecret+"&access_token="+token;
        StringRequest request = new StringRequest(Request.Method.GET, postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Success Response = ", response);
                longLiveToken = response.substring(response.indexOf("\"access_token\":\"")+16,response.indexOf("\",\"token_type\""));

                //Add current users UserID & access_token to firebase as a child of a current userUID -VS
                MediaAccess user = new MediaAccess(userID,longLiveToken);

                database.getReference().child("users").child("artists").child(mAuth.getCurrentUser().getUid()).child("instagram").setValue(user);
                tokenExists();      //Start regular process to display instagram media.
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Response = ", error.toString());
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    //TODO: refresh longlived token every 60 days.
    //renewLongLiveToken(database.getReference().child("users").child("artists").child(mAuth.getCurrentUser().getUid()).child("instagram").get("access_token"););
    /*public void renewLongLiveToken(final String token) {
        String postUrl = "https://graph.instagram.com/refresh_access_token ?grant_type=ig_refresh_token&access_token="+token;
        StringRequest request = new StringRequest(Request.Method.GET, postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Success Response = ", response);
                longLiveToken = response.substring(response.indexOf("\"access_token\":\"")+16,response.indexOf("\",\"token_type\""));


                //renew current users UserID & access_token to firebase as a child of a current userUID -VS
                MediaAccess user = new MediaAccess(userID,longLiveToken);
                //database.getReference().child("users").child("artists").child(mAuth.getCurrentUser().getUid()).child("instagram").setValue(user);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Response = ", error.toString());
            }
        });

        Volley.newRequestQueue(this).add(request);
    }*/


    public void artistInfo() {
        // Get references to the database -JM
        DatabaseReference myRef = database.getReference();
        DatabaseReference users = myRef.child("users");
        DatabaseReference artists = users.child("artists");
        DatabaseReference userID = artists.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        DatabaseReference name = userID.child("name");
        DatabaseReference location = userID.child("city");
        //DatabaseReference street = location.child("street");


        // Read name from the database -JM
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value =  dataSnapshot.child("first").getValue(String.class) + " " + dataSnapshot.child("last").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileNameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

       // Read e-mail from the database -JM
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("email").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileMailField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read phone number from the database -JM
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("phone").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profilePhoneField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read street from the database -JM
       /* street.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                String value = dataSnapshot.child("name").getValue(String.class) + " " + dataSnapshot.child("number").getValue();
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileStreetField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        }); */

        // Read postcode and city from the database -JM
        location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                String value = dataSnapshot.getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileCityField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void changePasswordClickedArtist(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        database.getReference().child("users").child("artists").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String emailAddres = String.valueOf(task.getResult().getValue());
                    Log.e("Email", emailAddres, task.getException());
                    auth.sendPasswordResetEmail(emailAddres)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Email", "Email sent.");
                                        Toast.makeText(ArtistProfileActivity.this, "Password reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });
    }

    //Static class for adding instagram userID and token to firebase as a child of a current user. -VS
    public static class MediaAccess {

        public String userID;
        public String access_token;

        public MediaAccess(String userID, String access_token) {
            this.userID = userID;
            this.access_token = access_token;
        }
    }
}