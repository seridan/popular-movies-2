package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.data.PopularMoviesDbHelper;
import com.example.android.popularmovies.data.PopularMoviesPreferences;
import com.example.android.popularmovies.model.Movie;

import com.example.android.popularmovies.utilities.CheckIsOnline;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.NoConnectionDialogFragment;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PopularMoviesAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<String>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String MOVIE_OBJECT = "movieObject";
    private static final int POPULAR_MOVIES_LOADER_ID = 0;
    private static final String SEARCH_QUERY_URL_EXTRA = "tmdbQueryExtra";
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private List<Movie> movieList;
    private ProgressBar mLoadingIndicator;
    private String queryUrl;
    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mRecyclerView = findViewById(R.id.recyclerview_images_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter(MainActivity.this, movieList, this);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);


        int loaderId = POPULAR_MOVIES_LOADER_ID;
        LoaderManager.LoaderCallbacks<String> callbacks = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
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
        /**
         * Check if is connected to internet, if not will shown an AlertDialog to report the issue to the user
         */
        if (CheckIsOnline.checkConnection(this)) {
            return new AsyncTaskLoader<String>(this) {

                String mPopularMoviesJson;

                @Override
                protected void onStartLoading() {

                    if (mPopularMoviesJson != null) {
                        deliverResult(mPopularMoviesJson);
                    } else {
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public String loadInBackground() {

                    String sortBy = PopularMoviesPreferences
                            .getPreferedSorted(MainActivity.this);

                    URL tmdbRequestUrl = NetworkUtils.buildSortedUrl(sortBy);

                    try {
                        String jsonTmdbResponse = NetworkUtils.getResponseFromHttpUrl(tmdbRequestUrl);
                        return jsonTmdbResponse;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String data) {
                    mPopularMoviesJson = data;
                    super.deliverResult(data);
                }
            };
        }else {
            showDialog();
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        queryUrl = data;
        mLoadingIndicator.setVisibility(View.GONE);

        try {
            movieList = JsonUtils.parseMovieList(queryUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPopularMoviesAdapter.setMovieList(movieList);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if a loader is not null.
        Loader loader = getSupportLoaderManager().getLoader(POPULAR_MOVIES_LOADER_ID);
        //Check if a loader is already initialized then restart, if not then we initialize it.
        if (loader != null) {
            if (PREFERENCES_HAVE_BEEN_UPDATED) {
                getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_ID, null, this);
                PREFERENCES_HAVE_BEEN_UPDATED = false;
            }else getSupportLoaderManager().initLoader(POPULAR_MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuOptionsThatWasSelected = item.getItemId();
        if (menuOptionsThatWasSelected == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY_URL_EXTRA, queryUrl);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    /**
     * This method creates a an alertDialog from NoConnectionDialogFragment class.
     */
    private void showDialog(){
        NoConnectionDialogFragment newFragment = new NoConnectionDialogFragment();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }
}


