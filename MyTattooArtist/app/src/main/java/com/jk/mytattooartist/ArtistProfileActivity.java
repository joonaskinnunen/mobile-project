package com.jk.mytattooartist;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class ArtistProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private String appId ="602591627694835";
    private String redirectUrl = "https://mytattooartist-d2298.firebaseapp.com/__/auth/handler";
    private String authUrl ="https://api.instagram.com/oauth/authorize?client_id="+appId+"&redirect_uri="+ redirectUrl+ "&scope=user_profile,user_media&response_type=code";
    private String checkUrl ="https://mytattooartist-d2298.firebaseapp.com/__/auth/handler?code=";
    private String authCode;
    private String igAppSecret = "1a302a11f9d6b0f0906987353193ee60";
    private String token;
    private boolean codeReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_profile);
        mAuth = FirebaseAuth.getInstance();
        artistInfo();
        //webView();
        artistInstagramPermission();

    }


    //Enable Javascript to be used on web view -VS
    @SuppressLint("SetJavaScriptEnabled")
    public void webView() {
        WebView myWebView = (WebView) findViewById(R.id.instagramFeedWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("user", currentUser.getDisplayName());
            //    reload();
        } else {
            createSignInIntent();
        }
    }

    //Enable JS so instagram auth can be used in web view. -VS
    @SuppressLint("SetJavaScriptEnabled")
    private void artistInstagramPermission() {
        WebView myWebView = (WebView) findViewById(R.id.instagramFeedWebView);

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
                if(viewUrl.contains(checkUrl)){
                    auth_code = viewUrl.substring(viewUrl.indexOf("code=")+5,viewUrl.length()-2);
                    Log.i("Auth_Code: ", auth_code);
                    authCode=auth_code;
                    codeReceived=true;
                    if(codeReceived==true){
                        Log.i("VALUE: ", "codeReceived == true.");
                        requestAccessToken(authCode);
                    }
                    else
                        Log.i("VALUE: ", "codeReceived == false.");

                    //myWebView.destroy();
                }
                //else
                    //TODO: "&error_description=The+user+denied+your+request" close webview and/or process,
            }
        });
    }

    //exchange code to access token with POST using Volley, get token + userId in respond. -VS
    public void requestAccessToken(final String authCode) {
        String postUrl = "https://api.instagram.com/oauth/access_token";
        StringRequest request = new StringRequest(Request.Method.POST, postUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //"response" is Access_token + UserID in JSON format -VS
                //TODO: Access_token + UserID must be further processed and used to retrieve IG media.
                Log.i("Success Response = ", response);
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


    public void artistInfo() {

        //TODO:use proper UID
        String user = "0";

        // Get references to the database -JM
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference();
        DatabaseReference users = myRef.child("users");
        DatabaseReference artists = users.child("artists");
        DatabaseReference userID = artists.child(user);
        DatabaseReference name = userID.child("name");
        DatabaseReference location = userID.child("location");
        DatabaseReference street = location.child("street");


        // Read name from the database -JM
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("title").getValue(String.class) + ". " + dataSnapshot.child("first").getValue(String.class) + " " + dataSnapshot.child("last").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileNameField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

       // Read e-mail from the database -JM
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("email").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileMailField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read phone number from the database -JM
        userID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("phone").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profilePhoneField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read street from the database -JM
        street.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                String value = dataSnapshot.child("name").getValue(String.class) + " " + dataSnapshot.child("number").getValue();
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileStreetField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        // Read postcode and city from the database -JM
        location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                String value = dataSnapshot.child("postcode").getValue() + " " + dataSnapshot.child("city").getValue(String.class);
                Log.i("VALUE: ", "value is: " + value);
                TextView tv = findViewById(R.id.profileCityField);
                tv.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }
}