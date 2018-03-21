package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PopularMoviesAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final String MOVIE_OBJECT = "movieObject";
    private RecyclerView mRecyclerView;

    private PopularMoviesAdapter mPopularMoviesAdapter;

    private List<Movie> movieList;

    private ProgressBar mLoadingIndicator;

    private static String sortBy;

    private static final int POPULAR_MOVIES_LOADER = 22;

    private static final String SEARCH_QUERY_URL_EXTRA = "tmdbQueryExtra";
    private String queryUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_images_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter(MainActivity.this, movieList, this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        checkConnectionAndExecute();
    }

    /**
     * This method gets the list of movies to set in the recycler view through an AsyncTask
     * loader.
     * @param sortBy
     */
    private void loadMoviesData(String sortBy) {

            URL tmdbUrl = NetworkUtils.buildSortedUrl(sortBy);

            Bundle queryBundle = new Bundle();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, tmdbUrl.toString());

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<Object> moviesSearchLoader = loaderManager.getLoader(POPULAR_MOVIES_LOADER);
            if (moviesSearchLoader == null){
                loaderManager.initLoader(POPULAR_MOVIES_LOADER, queryBundle,this);
            }else{
                loaderManager.restartLoader(POPULAR_MOVIES_LOADER, queryBundle, this);
            }
    }

    @Override
    public void onListItemClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(MOVIE_OBJECT, movie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {

                if (bundle == null) {
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String searchQueryStringUrl = bundle.getString(SEARCH_QUERY_URL_EXTRA);
                if (searchQueryStringUrl == null || TextUtils.isEmpty(searchQueryStringUrl)) {
                    return null;
                }
                try {
                    URL tmdbUrl = new URL(searchQueryStringUrl);
                    return NetworkUtils.getResponseFromHttpUrl(tmdbUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        queryUrl = data;
        try {
            movieList = JsonUtils.parseMovieList(queryUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mPopularMoviesAdapter.setMovieList(movieList);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }

    /**
     * Check if is connected to internet, if not will shown an AlertDialog to report the issue to the user
     */
    private void checkConnectionAndExecute(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && activeNetwork.isAvailable()) {
            loadMoviesData(sortBy);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_connection_message)
                    .setTitle(R.string.error_connection_tittle);
            builder.setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    recreate();
                }
            });

            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuOptionsThatWasSelected = item.getItemId();
            if (menuOptionsThatWasSelected == R.id.action_sort_popularity){
                sortBy = "popularity.desc";
                mPopularMoviesAdapter.setMovieList(null);
                loadMoviesData(sortBy);
        }else if (menuOptionsThatWasSelected == R.id.action_sort_rated) {
                sortBy = "vote_average.desc";
                mPopularMoviesAdapter.setMovieList(null);
                loadMoviesData(sortBy);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);
    }

}
