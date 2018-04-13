package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;



import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.CheckIsOnline;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.NoConnectionDialogFragment;
import com.example.android.popularmovies.utilities.PicassoUtils;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String[]> {

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

    private Context mContext;

    final static String TAG = DetailActivity.class.getSimpleName();
    final static int DETAIL_ACTIVITY_LOADER_ID = 0;
    private static List<Object> objects = new ArrayList<>();
    private static String[] jsonResponse;
    public static Resources mResources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;
        mResources = getResources();

        mPosterIv = findViewById(R.id.poster_iv);
        mTitleLabel = findViewById(R.id.title_movie);
        mSynopsisTv = findViewById(R.id.synopsis_tv);
        mVoteAverage = findViewById(R.id.vote_average_tv);
        mReleaseDate = findViewById(R.id.release_date_tv);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.detail_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            mMovie = intentThatStartedThisActivity.getParcelableExtra("movieObject");

        }
        int loaderId = DETAIL_ACTIVITY_LOADER_ID;
        LoaderManager.LoaderCallbacks<String[]> callbacks = DetailActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);

    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        /**
         * Check if is connected to internet, if not will shown an AlertDialog to report the issue to the user
         */
        if (CheckIsOnline.checkConnection(this)) {
            return  new AsyncTaskLoader<String[]>(this) {

                String[] mJsonStrings;
                @Override
                protected void onStartLoading() {

                    if (mJsonStrings != null){
                        deliverResult(mJsonStrings);
                    }else {
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
                        return  jsons;

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
        }else {
            showDialog();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        //Check data is null, if it is them get the path of the main movie image and pass to PicassoUtils

        jsonResponse = data;

            try {
                String backDropPath = JsonUtils.getDetailImage(jsonResponse[0]);
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
        mDetailMainAdapter = new DetailMainAdapter(this, getObject());
        mRecyclerView.setAdapter(mDetailMainAdapter);

        mDetailMainAdapter.notifyDataSetChanged();


        mDetailMainAdapter.setObjectList(getObject());


    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if a loader is not null.
        Loader loader = getSupportLoaderManager().getLoader(DETAIL_ACTIVITY_LOADER_ID);
        //Check if a loader is already initialized then restart, if not then we initialize it.
        if (loader != null) {
            getSupportLoaderManager().restartLoader(DETAIL_ACTIVITY_LOADER_ID, null, this);
        }
    }

    /**
     * This method load the image that will shown in the detailActivity screen through an AsyncTask
     * loader. Obtains the url to get the image JSON of the movie selected.
     * @param id int of the id of selected movie.
     */
   /* private void loadPosterImage (int id){
        // Obtains the url to get the image JSON of the movie selected.
        URL posterUrl = NetworkUtils.buildBackdropImageUrl(id);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, posterUrl.toString());
        //Create the LoaderManager an check if is initialized or not.
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> moviesSearchLoader = loaderManager.getLoader(DETAIL_ACTIVITY_LOADER);

        if (moviesSearchLoader == null){
            loaderManager.initLoader(DETAIL_ACTIVITY_LOADER, queryBundle,this);
        }else{
            loaderManager.restartLoader(DETAIL_ACTIVITY_LOADER, queryBundle, this);
        }
    }*/

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
     * @param movie the movie object selected to show the details
     */
    private void setMovieDetails(Movie movie) {

        checkAndSetTex(movie.getOriginalTitle(),mTitleLabel);
        checkAndSetTex(movie.getOverview(),mSynopsisTv);
        checkAndSetTex(String.valueOf(movie.getVote_average()),mVoteAverage);
        checkAndSetTex(String.valueOf(movie.getReleaseDate()), mReleaseDate);

    }

    /**
     * This method creates a an alertDialog from NoConnectionDialogFragment class.
     */
    private void showDialog(){
        NoConnectionDialogFragment newFragment = new NoConnectionDialogFragment();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("jsonResponse",jsonResponse );
    }

    private List<Object> getObject(){
        objects.add(getReviews().get(0));
        objects.add(getVideos().get(0));
        return objects;
    }

    public static List<String> getReviews(){

        if (review.size() > 0) {
            return review;
        }else {
            review.add(mResources.getString(R.string.no_reviews_error));
            return review;
        }
    }

    public static List<String> getVideos(){
        if (video.size() > 0) {
            return video;
        }else {
            video.add(mResources.getString(R.string.no_videos_error));
            return video;
        }
    }



}