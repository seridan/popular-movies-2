package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class VideosAdapter extends
        RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder>{

    private List<String> mVideosList;

    public VideosAdapter (List<String> data){
        mVideosList = data;
    }

    @Override
    public VideosAdapter.VideosAdapterViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        VideosAdapterViewHolder viewHolder = new VideosAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideosAdapter.VideosAdapterViewHolder holder, int position) {

        holder.videoTv.setText(mVideosList.get(position));

    }

    @Override
    public int getItemCount() {
        if (null == mVideosList) return 0;
        return mVideosList.size();
    }

    public class VideosAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView videoTv;

        public VideosAdapterViewHolder(View itemView) {
            super(itemView);
            videoTv = itemView.findViewById(R.id.video_tv);
        }
    }
}
