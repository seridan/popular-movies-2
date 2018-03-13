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

import java.util.Collections;
import java.util.List;

/**
 * Created by seridan on 08/03/2018.
 */
class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder>{

    private List<Movie> mMovieList;
    private Context context;


    public PopularMoviesAdapter(Context context, List<Movie> mMovieList) {
        this.context = context;
        this.mMovieList = mMovieList;
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

        urlBackDrop = NetworkUtils.buildImageUrl(mMovieList.get(position).getBackDropPath()).toString();
        PicassoUtils.getImageFromUrl(context, urlBackDrop, holder.imageMovie);
        //holder.imageMovie.setImageResource(movieList.get(position).getBackDropPath());

    }

    @Override
    public int getItemCount() {
       if (null == mMovieList) return 0;
       return mMovieList.size();
    }


    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageMovie;

        public PopularMoviesViewHolder(View itemView) {
            super(itemView);
            imageMovie = itemView.findViewById(R.id.image_movie);
        }
    }

    public void setMovieList (List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

}
