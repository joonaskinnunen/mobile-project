package com.jk.mytattooartist;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Map;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://mytattooartist-d2298-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String[] userRole = {""};

    private JsonArray localDataSet;
    private JsonArray wholeSet;
    JsonArray filteredByStyle;
    JsonArray filteredByPerson;
    private Gson gson = new Gson();
    private Boolean favorite = false;

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
        public ConstraintLayout getArtistInfo() { return artistInfo; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param jsonArray JsonArray containing the data to populate views to be used
     * by RecyclerView. -ET
     */
    public ArtistAdapter(JsonArray jsonArray) throws JSONException {
        localDataSet = jsonArray;
        wholeSet = jsonArray.deepCopy();
        filteredByStyle = jsonArray.deepCopy();
        filteredByPerson = jsonArray.deepCopy();
        Log.d("esate", "Adapter constructor");
    }

    // Create new views (invoked by the layout manager) -ET
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(mAuth.getCurrentUser() != null) getUserRoleFromDB(mAuth.getCurrentUser().getEmail());
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

        isFavourite(email);

        if (!favorite) {
            viewHolder.getFavoriteButton().setImageResource(R.drawable.ic_star);
            isFavourite(email);
        } else {
            viewHolder.getFavoriteButton().setImageResource(R.drawable.ic_star_border_black);
            isFavourite(email);
        }

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
                    addFavourite(email);
                    isFavourite(email);
                } else {
                    viewHolder.getFavoriteButton().setImageResource(R.drawable.ic_star_border_black);
                    removeFavourite(email);
                    isFavourite(email);
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
    public void filterList(ArrayList<String> filterList) {

        // Create array for comparing different titles for female gender from dataset.
        ArrayList<String> female = new ArrayList<>();
        female.add("Ms");
        female.add("Miss");
        female.add("Mrs");

        // If there is a filter option set, iterate through the filter arrays and remove items
        // not matching filter criteria. -ET
        if (!filterList.isEmpty()) {

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
                    } else if (title.equals("Mr")){
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

            /*
            Whe two filters are set, compare to filtered sets and find common items.
            Set common items as dataset. -ET
             */

            // Find common elements by iterating arrays filteredByPerson and filteredByStyle -ET
            JsonArray commonElements = new JsonArray();
            for (int p=0; p<filteredByPerson.size(); p++) {
                for (int s=0; s<filteredByStyle.size(); s++) {
                    if (filteredByPerson.get(p).getAsJsonObject().getAsJsonObject("name").equals(filteredByStyle.get(s).getAsJsonObject().getAsJsonObject("name"))) {
                        commonElements.add(filteredByPerson.get(p));
                    }
                }
            }

            // Set common elements as new dataset for adapter -ET
            localDataSet = commonElements.deepCopy();

            // If other filter array is empty, reset it and set the other one as dataset -ET
            if (filteredByStyle.isEmpty()) {
                localDataSet = filteredByPerson.deepCopy();
            }
            if (filteredByPerson.isEmpty()) {
                localDataSet = filteredByStyle.deepCopy();
            }

        // If filterList is empty, reset all arrays to contain all items -ET
        } else {
            localDataSet = wholeSet.deepCopy();
            filteredByPerson = wholeSet.deepCopy();
            filteredByStyle = wholeSet.deepCopy();
        }
        notifyDataSetChanged();

    }

    public void isFavourite(String email) {
        DatabaseReference myRef = database.getReference();
        String userEmail = mAuth.getCurrentUser().getEmail();
        myRef.child("users").child(getUserRole() + "s").orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Arraylist to hold users favourite artists emails -JK
                ArrayList<String> favouritesEmails = new ArrayList<String>();

                String key = "0";

                // Get users object key in DB and save it to the variable -JK
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                }

                // Get users favourite artists emails from DB to the favouritesEmails variable -JK
                favouritesEmails = (ArrayList<String>) dataSnapshot.child(key).child("favourites").getValue();

                if (favouritesEmails == null) {
                    favouritesEmails = new ArrayList<>();
                }
                Log.d("favouritesEmails", favouritesEmails.toString());
                if(favouritesEmails.contains(email)) {
                    favorite = true;
                } else {
                    favorite = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void addFavourite(String favouriteEmail) {
        DatabaseReference myRef = database.getReference();
        String userEmail = mAuth.getCurrentUser().getEmail();
        myRef.child("users").child(getUserRole() + "s").orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // Arraylist to hold users favourite artists emails -JK
                ArrayList<String> favouritesEmails = new ArrayList<String>();

                String key = "0";

                // Get users object key in DB and save it to the variable -JK
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    key = childSnapshot.getKey();
                }

                // Get users favourite artists emails from DB to the favouritesEmails variable -JK
                favouritesEmails = (ArrayList<String>) dataSnapshot.child(key).child("favourites").getValue();

                if (favouritesEmails == null) {
                    favouritesEmails = new ArrayList<>();
                }
                if(!favouritesEmails.contains(favouriteEmail)) {
                    favouritesEmails.add(favouriteEmail);
                }
                myRef.child("users").child(userRole[0] + "s").child(mAuth.getCurrentUser().getUid()).child("favourites").setValue(favouritesEmails);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

    }

    public void removeFavourite(String favouriteToRemove) {
        DatabaseReference db = database.getReference();
        DatabaseReference myRef = db.child("users").child(getUserRole() + "s").child(mAuth.getUid()).child("favourites");
        Log.d("myRef", myRef.toString());
       // myRef.child("favourites").orderByValue().equalTo(favouriteToRemove).;

        myRef.orderByValue().equalTo(favouriteToRemove).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Log.d("data.getRef", data.getRef().toString());
                    data.getRef().removeValue();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR: ",  databaseError.toException());
            }
        });

    }

    public void getUserRoleFromDB(String email) {
        DatabaseReference artistUserRef = FirebaseDatabase.getInstance().getReference().child("users").child("artists");
        artistUserRef.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("datasnapShot artists", dataSnapshot.toString());
                    updateUserRoleCB("artist");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference clientUserRef = FirebaseDatabase.getInstance().getReference().child("users").child("clients");
        clientUserRef.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateUserRoleCB("client");
                    Log.d("datasnapShot clients", dataSnapshot.toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value -JK
                Log.w("ERROR: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void updateUserRoleCB(String role) {
        userRole[0] = role;
        Log.d("userRole: ", userRole[0]);
    }

    public String getUserRole() {
        return userRole[0];
    }

}
