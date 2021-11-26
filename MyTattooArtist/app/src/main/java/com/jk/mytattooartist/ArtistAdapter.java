package com.jk.mytattooartist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private JsonArray localDataSet;
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

        public ViewHolder(View view) {
            super(view);
            // TODO: Define click listener for the ViewHolder's View

            nameTextView = view.findViewById(R.id.nameTextView);
            emailTextView = view.findViewById(R.id.emailTextView);
            phoneTextView = view.findViewById(R.id.phoneTextView);
            imageView = view.findViewById(R.id.imageView);
            favoriteButton = view.findViewById(R.id.favoriteButton);
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
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param jsonArray JSONArray containing the data to populate views to be used
     * by RecyclerView.
     */
    public ArtistAdapter(JsonArray jsonArray) throws JSONException {
        localDataSet = jsonArray;

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
        String image = pic.get("medium").getAsString();

        //  Replace the contents of the views with the strings
        Glide.with(viewHolder.getImageView().getContext()).load(image).into(viewHolder.imageView);
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
