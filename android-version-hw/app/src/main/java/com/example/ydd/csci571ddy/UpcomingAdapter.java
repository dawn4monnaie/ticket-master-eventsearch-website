package com.example.ydd.csci571ddy;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder>{

    private ArrayList<UpcomingItem> upcomingsList;
    private View.OnClickListener mClickListener;

    public UpcomingAdapter(ArrayList<UpcomingItem> upcomingsList) {
        this.upcomingsList = upcomingsList;
    }

    public void setOnItemClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_item, parent, false);
        UpcomingViewHolder holder = new UpcomingViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(v);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        UpcomingItem upcomingItem = upcomingsList.get(position);

        holder.upcoming_displayName.setText(upcomingItem.getDisplayName());
        holder.upcoming_artist.setText(upcomingItem.getArtist());
        holder.upcoming_date.setText(upcomingItem.getDate());
        holder.upcoming_type.setText(upcomingItem.getType());

    }

    @Override
    public int getItemCount() {
        return upcomingsList.size();
    }

    public class UpcomingViewHolder extends RecyclerView.ViewHolder {
        public TextView upcoming_displayName;
        public TextView upcoming_artist;
        public TextView upcoming_date;
        public TextView upcoming_type;

        // public TableRow reviewText;

        public UpcomingViewHolder(View itemView) {
            super(itemView);
            upcoming_displayName = itemView.findViewById(R.id.upcoming_displayName);
            upcoming_artist = itemView.findViewById(R.id.upcoming_artist);
            upcoming_date = itemView.findViewById(R.id.upcoming_date);
            upcoming_type = itemView.findViewById(R.id.upcoming_type);
        }
    }
}