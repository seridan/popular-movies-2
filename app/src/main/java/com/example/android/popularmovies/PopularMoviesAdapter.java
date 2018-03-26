package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.PicassoUtils;

import java.util.List;

/**
 * Created by seridan on 08/03/2018.
 */
class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder>{

    private List<Movie> mMovieList;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {
        void onListItemClick(Movie selectedMovie);
    }


    public PopularMoviesAdapter(Context context, List<Movie> movieList, ListItemClickListener listener) {
        mContext = context;
        mMovieList = movieList;
        mOnClickListener = listener;
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

        //Call the method getImageFromUrl in PicassoUtils to handle the image a possible error.
        PicassoUtils.getImageFromUrl(mContext, mMovieList.get(position).getPosterPath(), holder.imageMovie);
        holder.movieTittle.setText(mMovieList.get(position).getOriginalTitle());

    }

    @Override
    public int getItemCount() {
       if (null == mMovieList) return 0;
       return mMovieList.size();
    }


    public class PopularMoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView imageMovie;
        TextView movieTittle;

        public PopularMoviesViewHolder(View itemView) {
            super(itemView);

            imageMovie = itemView.findViewById(R.id.image_movie);
            movieTittle = itemView.findViewById(R.id.movie_tittle_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mMovieList.get(clickedPosition));
        }
    }

    public void setMovieList (List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

}
