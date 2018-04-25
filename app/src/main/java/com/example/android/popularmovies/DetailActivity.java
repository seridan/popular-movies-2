package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;


import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesDbHelper;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.CheckIsOnline;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.NoConnectionDialogFragment;
import com.example.android.popularmovies.utilities.PicassoUtils;
import com.facebook.stetho.Stetho;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView mPosterIv;
    private TextView mTitleLabel;
    private TextView mSynopsisTv;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mReviewTv;
    private TextView mVideoTv;
    private ProgressBar mLoadingIndicator;
    private static Movie mMovie;
    private static List<String> review;
    private static List<String> video;
    private RecyclerView mRecyclerView;
    private DetailMainAdapter mDetailMainAdapter;
    private FloatingActionButton mFab;
    private static boolean isFavorite;

    private Context mContext;

    final static String TAG = DetailActivity.class.getSimpleName();
    final static int DETAIL_ACTIVITY_NETWORK_LOADER_ID = 0;
    final static int DETAIL_ACTIVITY_CURSOR_LOADER_ID = 100;
    private static List<Object> objects;
    private static String[] jsonResponse;
    public static Resources mResources;
    private SQLiteDatabase mDb;
    private static String backDropPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;
        mResources = getResources();
        objects = new ArrayList<>();


        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mPosterIv = findViewById(R.id.poster_iv);
        mTitleLabel = findViewById(R.id.title_movie);
        mSynopsisTv = findViewById(R.id.synopsis_tv);
        mVoteAverage = findViewById(R.id.vote_average_tv);
        mReleaseDate = findViewById(R.id.release_date_tv);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mFab = findViewById(R.id.fab_add_movie);

        mRecyclerView = findViewById(R.id.detail_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mDetailMainAdapter = new DetailMainAdapter(mContext, objects);
        mRecyclerView.setAdapter(mDetailMainAdapter);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            mMovie = intentThatStartedThisActivity.getParcelableExtra("movieObject");

        }

        initLoaders();

    }

    private void initLoaders() {
        getSupportLoaderManager().initLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID, null, new LoadNetworkData(DetailActivity.this));
        getSupportLoaderManager().initLoader(DETAIL_ACTIVITY_CURSOR_LOADER_ID, null, new LoadCursor(DetailActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all favorites movies

        getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID, null, new LoadNetworkData(DetailActivity.this));


        getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_CURSOR_LOADER_ID, null, new LoadCursor(DetailActivity.this));
    }



    @Override
    protected void onStart() {
        super.onStart();
        //Check if a loader is not null.
        Loader loader = getSupportLoaderManager().getLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID);
        Loader loaderCursor = getSupportLoaderManager().getLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID);
        //Check if a loader is already initialized then restart, if not then we initialize it.
        if (loader != null) {
            getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID, null, new LoadNetworkData(DetailActivity.this));
        } else if (loaderCursor != null) {
            getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_CURSOR_LOADER_ID, null, new LoadCursor(DetailActivity.this));
        }
    }

    /**
     * This method check if there is value to set in the TextxView. If not will set the error message.
     *
     * @param s        string which represent the text that is set.
     * @param textView corresponding with the textView to set the data.
     */
    private void checkAndSetTex(String s, TextView textView) {
        if (TextUtils.isEmpty(s)) {
            textView.setText(R.string.detail_error_message);
        } else {
            textView.setText(s);
        }
    }

    /**
     * Populate the movie data in views
     *
     * @param movie the movie object selected to show the details
     */
    private void setMovieDetails(Movie movie) {

        checkAndSetTex(movie.getOriginalTitle(), mTitleLabel);
        checkAndSetTex(movie.getOverview(), mSynopsisTv);
        checkAndSetTex(String.valueOf(movie.getVote_average()), mVoteAverage);
        checkAndSetTex(String.valueOf(movie.getReleaseDate()), mReleaseDate);

    }

    /**
     * This method creates a an alertDialog from NoConnectionDialogFragment class.
     */
    private void showDialog() {
        NoConnectionDialogFragment newFragment = new NoConnectionDialogFragment();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("jsonResponse", jsonResponse);
    }

    private List<Object> getObject() {
        objects.add(getReviews().get(0));
        objects.add(getVideos().get(0));
        return objects;
    }

    public static List<String> getReviews() {

        if (review.size() > 0) {
            return review;
        } else {
            review.add(mResources.getString(R.string.no_reviews_error));
            return review;
        }
    }

    public static List<String> getVideos() {
        if (video.size() > 0) {
            return video;
        } else {
            video.add(mResources.getString(R.string.no_videos_error));
            return video;
        }
    }

    public void onClickAddMovie(View view) {
        if (mMovie == null) {
            return;
        }

        if (isFavorite){
            int id = mMovie.getId();
            String stringId = Integer.toString(id);
            Uri uri = PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            getContentResolver().delete(uri, null, null);
            mFab.setImageResource(R.drawable.ic_fab);
            getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_CURSOR_LOADER_ID, null, new LoadCursor(DetailActivity.this));
            getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID, null, new LoadNetworkData(DetailActivity.this));


        }else {

            ContentValues contentValues = new ContentValues();

            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_TITTLE, mMovie.getOriginalTitle());
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, mMovie.getOverview());
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_USER_RATING, mMovie.getVote_average());
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH, backDropPath);
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            contentValues.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_REVIEWS, review.get(0));


            Uri uri = getContentResolver().insert(PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI, contentValues);

            Stetho.initializeWithDefaults(this);

            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                mFab.setImageResource(R.drawable.ic_delete);
                getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_CURSOR_LOADER_ID, null, new LoadCursor(DetailActivity.this));
                getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_NETWORK_LOADER_ID, null, new LoadNetworkData(DetailActivity.this));
            }

        }

    }

    private class LoadNetworkData implements LoaderManager.LoaderCallbacks<String[]> {

        Context context;

        public LoadNetworkData(Context context) {
            this.context = context;
        }

        @Override
        public Loader<String[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<String[]>(context) {

                String[] mJsonStrings;

                @Override
                protected void onStartLoading() {

                    if (mJsonStrings != null) {
                        deliverResult(mJsonStrings);
                    } else {
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public String[] loadInBackground() {

                    String[] jsons = new String[3];

                    URL backDropImageRequestUrl = NetworkUtils.buildBackdropImageUrl(mMovie.getId());
                    URL reviewRequestUrl = NetworkUtils.buildReviewsMovieUrl(mMovie.getId());
                    URL videoRequestUrl = NetworkUtils.buildVideosMovieUrl(mMovie.getId());

                    try {

                        jsons[0] = NetworkUtils.getResponseFromHttpUrl(backDropImageRequestUrl);
                        jsons[1] = NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);
                        jsons[2] = NetworkUtils.getResponseFromHttpUrl(videoRequestUrl);
                        return jsons;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String[] data) {
                    mJsonStrings = data;
                    super.deliverResult(data);
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] data) {

            jsonResponse = data;

            try {
                backDropPath = JsonUtils.getDetailImage(jsonResponse[0]);
                PicassoUtils.getImageFromUrl(mContext, backDropPath, mPosterIv);

                review = JsonUtils.getReviewsMovie(jsonResponse[1]);
                mMovie.setReviews(review);

                video = JsonUtils.getVideosMovie(jsonResponse[2]);
                mMovie.setVideo(video);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            setMovieDetails(mMovie);
            mDetailMainAdapter.setObjectList(getObject());

        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }
    }

    private class LoadCursor implements LoaderManager.LoaderCallbacks<Cursor> {

        Context context;

        public LoadCursor(Context context) {
            this.context = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(context) {
                Cursor mMovieCursor = null;

                @Override
                protected void onStartLoading() {
                    if (mMovieCursor != null) {
                        deliverResult(mMovieCursor);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                PopularMoviesContract.FavoriteMovieEntry.COLUMN_TITTLE);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data from database.");
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {
                    mMovieCursor = data;
                    super.deliverResult(data);
                }

            };

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            isFavorite = false;
            int currentMovieId = mMovie.getId();


            while (data.moveToNext()){

                int idMovie = data.getInt(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID));
                if (idMovie == currentMovieId){
                    isFavorite = true;
                    mFab.setImageResource(R.drawable.ic_delete);

                }
            }


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
