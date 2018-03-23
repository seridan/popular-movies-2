package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.PicassoUtils;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private ImageView mPosterIv;
    private TextView mTitleLabel;
    private TextView mSynopsisTv;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private Movie mMovie;

    Context context;

    final static String TAG = DetailActivity.class.getSimpleName();
    final static int DETAIL_ACTIVITY_LOADER = 22;
    final static String SEARCH_QUERY_URL_EXTRA = "searchQueryUrlExtra";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterIv = findViewById(R.id.poster_iv);
        mTitleLabel = findViewById(R.id.title_movie);
        mSynopsisTv = findViewById(R.id.synopsis_tv);
        mVoteAverage = findViewById(R.id.vote_average_tv);
        mReleaseDate = findViewById(R.id.release_date_tv);

        context = this;

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            mMovie = intentThatStartedThisActivity.getParcelableExtra("movieObject");
            if (mMovie != null) {
                int mMovieId = mMovie.getId();
                loadPosterImage(mMovieId);
            }
        }

        setMovieDetails();
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                forceLoad();
                //mLoadingIndicator iria aqui
            }

            @Override
            public String loadInBackground() {
                String searchQueryUrlString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryUrlString == null || TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }
                try {
                    URL detailUrl = new URL(searchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(detailUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        //Check data is null, if it is them get the path of the main movie image and pass to PicassoUtils
        if (data == null) {
            data = mMovie.getPosterPath();
            PicassoUtils.getImageFromUrl(context, data, mPosterIv);
            Log.v(TAG, "data if null " + data);
        //If not null, get the data to pass the JsonUtils and obtain the backdrop image
        }else {
            try {
                String backDropPath = JsonUtils.getDetailImage(data);
                PicassoUtils.getImageFromUrl(context, backDropPath, mPosterIv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    /**
     * This method load the image that will shown in the detailActivity screen through an AsyncTask
     * loader. Obtains the url to get the image JSON of the movie selected.
     * @param id int of the id of selected movie.
     */
    private void loadPosterImage (int id){
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
    }

    private void setMovieDetails() {
        mTitleLabel.setText(mMovie.getOriginalTitle());
        mSynopsisTv.setText(mMovie.getOverview());
        mVoteAverage.setText(String.valueOf(mMovie.getVote_average()));
        mReleaseDate.setText(String.valueOf(mMovie.getReleaseDate()));

    }
}