package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.PicassoUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by seridan on 08/03/2018.
 */
class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder>{

    private List<Movie> movieList;
    private final Context mContext;

    public PopularMoviesAdapter(List<Movie> movieList, Context mContext) {
        this.movieList = movieList;
        this.mContext = mContext;
    }

    @Override
    public PopularMoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.image_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        PopularMoviesViewHolder viewHolder = new PopularMoviesViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PopularMoviesViewHolder holder, int position) {
        String urlBackDrop;
        urlBackDrop = NetworkUtils.buildImageUrl(movieList.get(position).getBackDropPath()).toString();
        PicassoUtils.getImageFromUrl(mContext, urlBackDrop, holder.imageMovie);
        //holder.imageMovie.setImageResource(movieList.get(position).getBackDropPath());

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageMovie;

        public PopularMoviesViewHolder(View itemView) {
            super(itemView);
            imageMovie = itemView.findViewById(R.id.image_movie);
        }
    }

}
