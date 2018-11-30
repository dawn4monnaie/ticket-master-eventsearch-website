package com.example.ydd.csci571ddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context mContext;
    private ArrayList<String> artistJsonArray;
    private ArrayList<String> photosJsonArray;
    private ArrayList<String> artistNameArray;
    private Context mcontext;

    public ArtistAdapter(Context context, ArrayList<String> ARTISTNAME, ArrayList<String> ARTISTSJSON, ArrayList<String> PHOTOSJSON) {
        mcontext = context;
        artistNameArray = ARTISTNAME;
        artistJsonArray = ARTISTSJSON;
        photosJsonArray = PHOTOSJSON;
    }


    private ArrayList<Bitmap> bitmaps;


    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_item, parent, false);
        ArtistViewHolder holder = new ArtistViewHolder(v);


        if (artistJsonArray.size() != 0){
            holder.artist_artistteamsname_row.setVisibility(View.VISIBLE);
            holder.artist_followers_row.setVisibility(View.VISIBLE);
            holder.artist_popularity_row.setVisibility(View.VISIBLE);
            holder.artist_spotify_row.setVisibility(View.VISIBLE);

            //holder.artist_artistteamsname.setVisibility(View.VISIBLE);
            //holder.artist_followers.setVisibility(View.VISIBLE);
            //holder.artist_popularity.setVisibility(View.VISIBLE);
            //holder.artist_spotify.setVisibility(View.VISIBLE);

        } else {
            holder.artist_artistteamsname_row.setVisibility(View.GONE);
            holder.artist_followers_row.setVisibility(View.GONE);
            holder.artist_popularity_row.setVisibility(View.GONE);
            holder.artist_spotify_row.setVisibility(View.GONE);
        }

        if (photosJsonArray.size() != 0) {
            holder.artist_no_photos.setVisibility(View.GONE);
        } else {
            holder.artist_no_photos.setVisibility(View.VISIBLE);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {

        // This part is for title
        holder.artist_title.setText(artistNameArray.get(position));

        // This part is for photo
        String currentPhotosItem = photosJsonArray.get(position);

        holder.artist_photos_recyclerView.setAdapter(new PhotoAdapter(mcontext, currentPhotosItem));
        holder.artist_photos_recyclerView.setLayoutManager(new LinearLayoutManager(mcontext));



        // This part is for table meta info
        if (artistJsonArray.size() != 0) {
            String currentArtistItem="";
            currentArtistItem = artistJsonArray.get(position);
            JSONObject artist = new JSONObject();
            try {


                artist = new JSONObject(currentArtistItem);
                JSONObject artist_info = artist.getJSONObject("artists").getJSONArray("items").getJSONObject(0);

                String name = artist_info.getString("name");
                String followers = artist_info.getJSONObject("followers").getString("total");
                String popularity = artist_info.getString("popularity");
                String url = artist_info.getJSONObject("external_urls").getString("spotify");
                Log.d("AritstURL:", url);

                Spanned hyperlink = Html.fromHtml("<a href=\"" + url+ "\">Spotify</a>");

                holder.artist_artistteamsname.setText(name);
                holder.artist_followers.setText(followers);
                holder.artist_popularity.setText(popularity);

                holder.artist_spotify.setMovementMethod(LinkMovementMethod.getInstance());
                holder.artist_spotify.setText(hyperlink);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public int getItemCount() {
        return artistNameArray.size();
    }


    public void removeItemAt(int position) {
        if (artistJsonArray.size() != 0) {
            artistJsonArray.remove(position);
        }
        photosJsonArray.remove(position);
        artistNameArray.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, artistNameArray.size());
    }



    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        public TableRow artist_artistteamsname_row;
        public TableRow artist_followers_row;
        public TableRow artist_popularity_row;
        public TableRow artist_spotify_row;

        public TextView artist_title;

        public TextView artist_artistteamsname;
        public TextView artist_followers;
        public TextView artist_popularity;
        public TextView artist_spotify;

        public RecyclerView artist_photos_recyclerView;
        public TextView artist_no_photos;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            artist_artistteamsname_row = itemView.findViewById(R.id.artist_artistteamsname_row);
            artist_followers_row = itemView.findViewById(R.id.artist_followers_row);
            artist_popularity_row = itemView.findViewById(R.id.artist_popularity_row);
            artist_spotify_row = itemView.findViewById(R.id.artist_spotify_row);

            artist_title = itemView.findViewById(R.id.artist_title);

            artist_artistteamsname = (TextView) itemView.findViewById(R.id.artist_artistteamsname);
            artist_followers =  (TextView) itemView.findViewById(R.id.artist_followers);
            artist_popularity =  (TextView) itemView.findViewById(R.id.artist_popularity);
            artist_spotify =  (TextView) itemView.findViewById(R.id.artist_spotify);

            artist_photos_recyclerView = itemView.findViewById(R.id.artist_photos_recyclerView);
            artist_no_photos = itemView.findViewById(R.id.artist_no_photos);

        }
    }


}
