package com.jk.mytattooartist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    DatabaseReference myRef = database.getReference();
    String userId = currentUser.getUid();

    private JsonArray localDataSet;
    private JsonArray wholeSet;
    JsonArray filteredByStyle;
    JsonArray filteredByPerson;
    JsonArray filteredByDistance;
    private Gson gson = new Gson();
    private Boolean favorite = false;
    JsonObject location = new JsonObject();


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder). -ET
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView emailTextView;
        private final TextView phoneTextView;
        private final ImageView imageView;
        private final ImageButton favoriteButton;
        private final ConstraintLayout artistInfo;

        public ViewHolder(View view) {
            super(view);
            // TODO: Define click listener for the ViewHolder's View

            nameTextView = view.findViewById(R.id.nameTextView);
            emailTextView = view.findViewById(R.id.emailTextView);
            phoneTextView = view.findViewById(R.id.phoneTextView);
            imageView = view.findViewById(R.id.imageView);
            favoriteButton = view.findViewById(R.id.favoriteButton);
            artistInfo = view.findViewById(R.id.artistInfoLayout);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getEmailTextView() {
            return emailTextView;
        }

        public TextView getPhoneTextView() {
            return phoneTextView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageButton getFavoriteButton() {
            return favoriteButton;
        }

        public ConstraintLayout getArtistInfo() {
            return artistInfo;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param jsonArray JsonArray containing the data to populate views to be used
     *                  by RecyclerView. -ET
     */
    public ArtistAdapter(JsonArray jsonArray) throws JSONException {
        localDataSet = jsonArray;
        wholeSet = jsonArray.deepCopy();
        filteredByStyle = jsonArray.deepCopy();
        filteredByPerson = jsonArray.deepCopy();
        filteredByDistance = jsonArray.deepCopy();

        // Getting the user's location from Firebase
        myRef.child("users").child("clients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String json = gson.toJson(snapshot.getValue());
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                    JsonObject client = jsonObject.getAsJsonObject(userId);
                    location = client.getAsJsonObject("latLng");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("esate", "ei onnistu käyttäjän paikannus");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Create new views (invoked by the layout manager) -ET
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item -ET
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.artist_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager) -ET
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get the JsonObjects -ET
        JsonObject artist = localDataSet.get(position).getAsJsonObject();
        JsonObject pic = artist.getAsJsonObject("picture");

        // Get the String format values of desired fields -ET
        String firstName = artist.getAsJsonObject("name").get("first").getAsString();
        String lastName = artist.getAsJsonObject("name").get("last").getAsString();
        String email = artist.get("email").getAsString();
        String phone = artist.get("phone").getAsString();

        // Set a placeholder image
        String image = "https://png.pngtree.com/png-vector/20210604/ourmid/pngtree-gray-avatar-placeholder-png-image_3416697.jpg";

        // Check if user has profile image in DB -JK
        if (pic != null) image = pic.get("medium").getAsString();

        // Set image into viewholder with Glide -ET
        Glide.with(viewHolder.getImageView().getContext()).load(image).into(viewHolder.imageView);

        //  Replace the contents of the views with the strings -ET
        viewHolder.getNameTextView().setText(firstName + " " + lastName);
        viewHolder.getEmailTextView().setText(email);
        viewHolder.getPhoneTextView().setText(phone);

        // Click listener for setting the the favorite star as outline or filled. -ET
        viewHolder.getFavoriteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favorite) {
                    viewHolder.getFavoriteButton().setImageResource(R.drawable.ic_star);
                    favorite = true;
                } else {
                    viewHolder.getFavoriteButton().setImageResource(R.drawable.ic_star_border_black);
                    favorite = false;
                }
            }
        });

        // Set a click listener that starts a new activity when a list item is clicked -ET
        viewHolder.getArtistInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extra = new Bundle();
                extra.putString("artist", gson.toJson(artist));
                Intent intent = new Intent(v.getContext(), UserUiForArtistActivity.class);
                intent.putExtra("extra", extra);
                v.getContext().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager) -ET
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    // Filter the recyclerview list by the list of filters given as parameter -ET
    // TODO: Find ways to reduce the amount of resetting arrays used in the method. -ET
    public void filterList(ArrayList<String> filterList, int distanceFilter) {

        // Create array for comparing different titles for female gender from dataset.
        ArrayList<String> female = new ArrayList<>();
        female.add("Ms");
        female.add("Miss");
        female.add("Mrs");

        // If there is a filter option set, iterate through the filter arrays and remove items
        // not matching filter criteria. -ET
        if (!filterList.isEmpty() || distanceFilter != 0) {
            localDataSet = wholeSet.deepCopy();
            // TODO: Can the two iterator blocks be combined?
            filteredByStyle = wholeSet.deepCopy();
            Iterator i = filteredByStyle.iterator();
            while (i.hasNext()) {
                boolean remove = true;
                JsonElement artist = (JsonElement) i.next();
                try {
                    JsonArray styles = artist.getAsJsonObject().getAsJsonArray("styles");
                    for (JsonElement style : styles) {
                        if (filterList.contains(style.getAsString())) {
                            remove = false;
                        }
                    }
                } catch (Exception e) {
                }
                if (remove) {
                    i.remove();
                }
            }

            filteredByPerson = wholeSet.deepCopy();
            Iterator j = filteredByPerson.iterator();
            while (j.hasNext()) {
                boolean remove = true;
                JsonElement artist = (JsonElement) j.next();
                String title;
                try {
                    title = artist.getAsJsonObject().getAsJsonObject("name").get("title").getAsString();
                } catch (Exception e) {
                    e.printStackTrace();
                    title = "Other";
                }
                if (female.contains(title)) {
                    title = "Female";
                } else if (title.equals("Mr")) {
                    title = "Male";
                } else {
                    title = "Other";
                }
                try {
                    if (filterList.contains(title)) {
                        remove = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (remove) {
                    j.remove();
                }
            }

            filteredByDistance = wholeSet.deepCopy();
            Iterator k = filteredByDistance.iterator();
            while (k.hasNext()) {
                boolean remove = true;
                double userLat = location.get("latitude").getAsDouble();
                double userLng = location.get("longitude").getAsDouble();
                JsonElement artist = (JsonElement) k.next();
                JsonObject artistLatLng = new JsonObject();
                double artistLat = 0;
                double artistLng = 0;

                // Trying and catching to find the coordinates for artist
                try {
                    JsonObject artistLocation = artist.getAsJsonObject().getAsJsonObject("location");
                    artistLatLng = artistLocation.getAsJsonObject("coordinates");
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        artistLatLng = artist.getAsJsonObject().getAsJsonObject("latLng");
                    } catch (Exception er) {
                        er.printStackTrace();
                    }
                }
                try {
                    artistLat = artistLatLng.get("latitude").getAsDouble();
                    artistLng = artistLatLng.get("longitude").getAsDouble();
                } catch (Exception err) {
                    err.printStackTrace();
                }

                // Call getDistance method
                double distance = getDistance(
                        userLat,
                        artistLat,
                        userLng,
                        artistLng
                );
                if (distance < distanceFilter) remove = false;
                if (remove) k.remove();
            }

            /*
            When filters are set, compare the filtered lists and find common items.
            Set common items as dataset. -ET
             */

            if (!filteredByPerson.isEmpty() && !filteredByStyle.isEmpty()) {
                localDataSet = getCommonElements(filteredByPerson, filteredByStyle).deepCopy();
            } else {
                // If other filter array is empty, reset it and set the other one as dataset -ET
                if (filteredByStyle.isEmpty() && !filteredByPerson.isEmpty()) {
                    localDataSet = filteredByPerson.deepCopy();
                }
                if (filteredByPerson.isEmpty() && !filteredByStyle.isEmpty()) {
                    localDataSet = filteredByStyle.deepCopy();
                }
            }
            if (!localDataSet.isEmpty() && !filteredByDistance.isEmpty())
                localDataSet = getCommonElements(filteredByDistance, localDataSet);
        } else {
            localDataSet = wholeSet.deepCopy();
        }
        notifyDataSetChanged();
    }

    public JsonArray getCommonElements(JsonArray arr1, JsonArray arr2) {

        // Create an array to to hold found common items -ET
        JsonArray commonElements = new JsonArray();

        // Nested loopings to find common items -ET
        for (int i = 0; i < arr1.size(); i++) {
            for (int j = 0; j < arr2.size(); j++) {
                if (arr1.get(i).getAsJsonObject().getAsJsonObject("name").equals(arr2.get(j).getAsJsonObject().getAsJsonObject("name"))) {
                    commonElements.add(arr1.get(i));
                }
            }
        }
        return commonElements;
    }

    public static double getDistance(double lat1,
                                     double lat2, double lon1,
                                     double lon2) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r);
    }
}
