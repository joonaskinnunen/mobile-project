package com.jk.mytattooartist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class InstagramAdapter extends RecyclerView.Adapter<InstagramAdapter.MediaHolder> {

    private Context context;
    private List<InstagramMedia> instagramMediaList;


    public InstagramAdapter(Context context , List<InstagramMedia> instagramMedia){
        this.context = context;
        instagramMediaList = instagramMedia;
    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.instagram_media_item, parent,  false);
        return new MediaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position) {
        if(instagramMediaList.get(position)!=null)
        {
            InstagramMedia media = instagramMediaList.get(position);
            holder.caption.setText(media.getCaption());
            Glide.with(context).load(media.getMediaURL()).into(holder.imageView);
        }else
            holder.caption.setText("Null value");
    }

    @Override
    public int getItemCount() {
        return instagramMediaList.size();
    }

    public class MediaHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView caption;
        LinearLayout linearLayout;

        public MediaHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.instagramMediaImage);
            caption = (TextView) itemView.findViewById(R.id.imageCaption);
            linearLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}