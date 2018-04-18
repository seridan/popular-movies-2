package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.DetailActivity.getReviews;
import static com.example.android.popularmovies.DetailActivity.getVideos;

public class DetailMainAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Object> mItems;
    private final int REVIEWS = 2;
    private final int VIDEOS = 1;

    public DetailMainAdapter(Context context, List<Object> items) {
        mContext = context;
        mItems = items;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder;

        switch (viewType){
            case REVIEWS:
                view = inflater.inflate(R.layout.detail_reviews_recycle_view,
                        parent, false);
                holder = new ReviewViewHolder(view);
                break;

            case VIDEOS:
                view = inflater.inflate(R.layout.detail_videos_recycle_view,
                        parent, false);
                holder = new VideoViewHolder(view);
                break;

                default:
                    view = inflater.inflate(R.layout.detail_reviews_recycle_view,
                            parent, false);
                    holder = new VideoViewHolder(view);
                    break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == REVIEWS)
            reviewView((ReviewViewHolder) holder);
        else if (holder.getItemViewType() == VIDEOS)
            videoView((VideoViewHolder) holder);

    }

    private void reviewView(ReviewViewHolder holder){
        ReviewsAdapter adapter1 = new ReviewsAdapter(getReviews(),mContext);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter1);
    }

    private void videoView(VideoViewHolder holder){
        VideosAdapter adapter2 = new VideosAdapter(getVideos(), mContext);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter2);
    }


    @Override
    public int getItemCount() {
        if (null == mItems) return 0;
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return REVIEWS;
        if (position == 1)
            return VIDEOS;
        return -1;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.reviews_recyclerView);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;

        VideoViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.videos_recyclerView);
        }
    }

    public void setObjectList (List<Object> itemsList) {
        mItems = itemsList;
        notifyDataSetChanged();
    }
}
