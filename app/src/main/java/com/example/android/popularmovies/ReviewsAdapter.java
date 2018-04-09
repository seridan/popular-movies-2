package com.example.android.popularmovies;

import android.content.Context;
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
    private static int mExpandedPosition = -1;
    //ViewGroup recycler = (ViewGroup)(R.layout.detail_reviews_recycle_view);
    //private Context mContext;

    public ReviewsAdapter (List<String> data){
        mReviewsList = data;
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
    public void onBindViewHolder(ReviewsAdapter.ReviewsAdapterViewHolder holder, final int position) {

        final boolean isExpanded = position==mExpandedPosition;
        holder.reviewTvExpandable.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                mExpandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition((ViewGroup) view.getParent());
                notifyDataSetChanged();
            }
        });

        //holder.reviewTv.setText(mReviewsList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null == mReviewsList) return 0;
        return mReviewsList.size();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView reviewTv;
        TextView reviewTvExpandable;


        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            reviewTv = itemView.findViewById(R.id.review_tv);
            reviewTvExpandable = itemView.findViewById(R.id.review_tv_expandable);

        }
    }
}
