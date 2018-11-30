package com.example.ydd.csci571ddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public final static int RESULTS_LIST = 0;
    public final static int FAVORITES_LIST = 1;
    private ArrayList<EventItem> eventList;
    private int eventListType;
    private OnItemClickListener mListener;
    private Context mContext;
    private FavoritesManager mFavoritesManager;
    private OnFavoriteButtonClickListener mFavoriteButtonClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnFavoriteButtonClickListener {
        void onFavoriteButtonClick(int position);
    }

    public EventAdapter(Context context, ArrayList<EventItem> list, int placeListType) {
        mContext = context;
        eventList = list;
        this.eventListType = placeListType;
        mFavoritesManager = new FavoritesManager();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnFavoriteButtonClickListener(OnFavoriteButtonClickListener listener) {
        mFavoriteButtonClickListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        EventViewHolder holder = new EventViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventItem currentItem = eventList.get(position);
        String iconUrl = currentItem.getIconUrl();
        String placeName = currentItem.getName();
        String address = currentItem.getAddress();
        String time = currentItem.getDate();

        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(address);
        holder.timeTextView.setText(time);

        // "Music", "Sports", "Art & Theatre", "Film", "Miscellanenous"
        if (iconUrl.equals("0")){
            holder.eventIconView.setImageResource(R.drawable.music_icon);
        } else if (iconUrl.equals("1")){
            holder.eventIconView.setImageResource(R.drawable.sport_icon);
        } else if (iconUrl.equals("2")){
            holder.eventIconView.setImageResource(R.drawable.art_icon);
        } else if (iconUrl.equals("3")){
            holder.eventIconView.setImageResource(R.drawable.film_icon);
        } else {
            holder.eventIconView.setImageResource(R.drawable.miscellaneous_icon);
        }

        // Picasso.get().load(iconUrl).into(holder.placeIconView);

        if (eventListType == FAVORITES_LIST) {
            holder.favoriteBtn.setImageResource(R.drawable.heart_fill_red);
        } else {
            if (mFavoritesManager.isFavorited(mContext, currentItem.getEventId())) {
                holder.favoriteBtn.setImageResource(R.drawable.heart_fill_red);
            } else {
                holder.favoriteBtn.setImageResource(R.drawable.heart_outline_black);
            }
        }

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


    public void removeItemAt(int position) {
        eventList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventList.size());
    }



    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventIconView;
        public TextView nameTextView;
        public TextView addressTextView;
        public TextView timeTextView;

        public ImageView favoriteBtn;
        public ConstraintLayout textAndImage;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventIconView = itemView.findViewById(R.id.categoryIcon);
            nameTextView = itemView.findViewById(R.id.event_name);
            addressTextView = itemView.findViewById(R.id.event_address);
            timeTextView = itemView.findViewById(R.id.event_time);

            favoriteBtn = itemView.findViewById(R.id.favorite_button);
            textAndImage = itemView.findViewById(R.id.event_text_image);

            textAndImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFavoriteButtonClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mFavoriteButtonClickListener.onFavoriteButtonClick(position);
                        }
                    }
                }
            });


            View.OnClickListener clickListener;
            if (eventListType == RESULTS_LIST) {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Log.d("On click favoBtn", "position:" + position);
                        if (position != RecyclerView.NO_POSITION) {
                            EventItem placeItem = eventList.get(position);
                            mFavoritesManager.onResultsFavoriteButtonClick(mContext, placeItem, favoriteBtn);
                        }
                    }
                };
                favoriteBtn.setOnClickListener(clickListener);
            }
        }
    }


}
