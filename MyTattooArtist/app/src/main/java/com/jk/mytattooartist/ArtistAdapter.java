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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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

    private JsonArray localDataSet;
    private JsonArray wholeSet;
    private Gson gson = new Gson();
    private Boolean favorite = false;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
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
     * @param jsonArray JSONArray containing the data to populate views to be used
     * by RecyclerView.
     */
    public ArtistAdapter(JsonArray jsonArray) throws JSONException {
        localDataSet = jsonArray;
        wholeSet = jsonArray;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.artist_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get the JsonObjects
        JsonObject artist = localDataSet.get(position).getAsJsonObject();
        JsonObject pic = artist.getAsJsonObject("picture");

        // Get the String format values of desired fields
        String firstName = artist.getAsJsonObject("name").get("first").getAsString();
        String lastName = artist.getAsJsonObject("name").get("last").getAsString();
        String email = artist.get("email").getAsString();
        String phone = artist.get("phone").getAsString();

        String image = "https://png.pngtree.com/png-vector/20210604/ourmid/pngtree-gray-avatar-placeholder-png-image_3416697.jpg";
        // Check if user has profile image in DB -JK
        if(pic != null) {
            image = pic.get("medium").getAsString();
        } else {

        }

        //  Replace the contents of the views with the strings
        viewHolder.getNameTextView().setText(firstName + " " + lastName);
        viewHolder.getEmailTextView().setText(email);
        viewHolder.getPhoneTextView().setText(phone);

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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateList(JsonArray array) {
        localDataSet = array;
        notifyDataSetChanged();
    }

    public void filterList(ArrayList<String> filterList) {
        localDataSet = wholeSet.deepCopy();
        Log.d("esate", "filterlist koko " + filterList.size());
        Log.d("esate ", "localdataset: " + localDataSet.toString());
        ArrayList<String> female = new ArrayList<>();
        female.add("Ms");
        female.add("Miss");
        female.add("Mrs");

        if (!filterList.isEmpty()) {
            Iterator i = localDataSet.iterator();
            while (i.hasNext()) {
                boolean remove = true;
                JsonElement artist = (JsonElement) i.next();
                try {
                    JsonArray styles = artist.getAsJsonObject().getAsJsonArray("styles");
                    for (JsonElement style : styles) {
                        Log.d("Esate ", "style: " + style.toString());
                        if (filterList.contains(style.getAsString())) {
                            Log.d("Esate", "Artisti ei poisteta: " + artist.toString());
                            remove = false;
                        }
                    }
                } catch (Exception e) {
                    Log.d("Esate", "Ei löydy tyyliä");
                }
                if (remove) {
                    i.remove();
                }
            }
/*            Iterator j = localDataSet.iterator();
            while (j.hasNext()) {
                boolean remove = true;
                JsonElement artist = (JsonElement) j.next();
                String title = artist.getAsJsonObject().getAsJsonObject("name").get("title").getAsString();
                if (female.contains(title)) {
                    title = "Female";
                } else if (title.equals("Mr")){
                    title = "Male";
                } else {
                    title = "Other";
                }
                try {
                    if (filterList.get("title").contains(title)) remove = false;
                } catch (Exception e) {
                    Log.d("esate", "Ei titteliä");
                }
                if (remove) {
                    j.remove();
                }
            }*/
        }
        notifyDataSetChanged();

    }

}
