package com.example.android.popularmovies;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewsAdapter extends
        RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<String> mReviewsList;
    private Context mContext;

    //ViewGroup recycler = (ViewGroup)(R.layout.detail_reviews_recycle_view);
    //private Context mContext;

    public ReviewsAdapter (List<String> data, Context context){
        mReviewsList = data;
        mContext = context;
    }

    @Override
    public ReviewsAdapter.ReviewsAdapterViewHolder
    onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewsAdapterViewHolder viewHolder = new ReviewsAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.ReviewsAdapterViewHolder holder, final int position) {


        holder.reviewTv.setText(mReviewsList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ReviewViewActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mReviewsList.get(position));
                mContext.startActivity(intent);

                }



        });

    }

    @Override
    public int getItemCount() {
        if (null == mReviewsList) return 0;
        return mReviewsList.size();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView reviewTv;


        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            reviewTv = itemView.findViewById(R.id.review_tv);


        }
    }
}
