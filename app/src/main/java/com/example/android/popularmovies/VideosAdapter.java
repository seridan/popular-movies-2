package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class VideosAdapter extends
        RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder>{

    private List<String> mVideosList;
    private Context mContext;



    public VideosAdapter (List<String> data, Context context){
        mVideosList = data;
        mContext = context;

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
    public void onBindViewHolder(VideosAdapter.VideosAdapterViewHolder holder, final int position) {

        int numTrailer = position +1;

        holder.videoTv.setText(mContext.getString(R.string.trailers_label) + " " + numTrailer);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String youtubePath = mVideosList.get(position);
                openYouTubeTrailer(youtubePath, mContext);
            }
        });

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

    private void openYouTubeTrailer(String path, Context context){
        URL youtubePage = NetworkUtils.buildYouTubeUrl(path);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(youtubePage)));
        if (intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(intent);
        }
    }
}
