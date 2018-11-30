package com.example.ydd.csci571ddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{

    private ArrayList<String> photoList;
    private ArrayList<Double> ratioheightList;
    private Context mcontext;

    public PhotoAdapter(Context context, String currentPhotosItem) {
        // this.photoList = photoList;
        mcontext = context;
        photoList = new ArrayList<String>();
        ratioheightList = new ArrayList<Double>();
        try {
            JSONObject photos = new JSONObject(currentPhotosItem);
            if (photos.has("items")){
                JSONArray photosArray = photos.getJSONArray("items");
                for(int i = 0; i < photosArray.length(); i++){
                    String urlname = photosArray.getJSONObject(i).getString("link");

                    Integer height = Integer.parseInt(photosArray.getJSONObject(i).getJSONObject("image").getString("height"));
                    Integer width = Integer.parseInt(photosArray.getJSONObject(i).getJSONObject("image").getString("width"));
                    Double ratio = (double) height / width;
                    Log.d("urlname", urlname);

                    //InputStream in = new java.net.URL(urlname).openStream();
                    //Bitmap bmp = BitmapFactory.decodeStream(in);

                    // URL url = new URL(urlname);
                    // Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ratioheightList.add(ratio);
                    photoList.add(urlname);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String url = photoList.get(position);
        int heigth = ratioheightList.get(position).intValue() * 1000;
        Picasso.get().load(url).resize(1000, heigth).into(holder.photoView);
    }
    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.place_photo);
        }
    }


}
