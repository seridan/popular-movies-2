package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMoviesContract;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity
        implements PopularMoviesAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String MOVIE_OBJECT = "movieObject";
    private static final int POPULAR_MOVIES_LOADER_ID = 0;
    private static final int POPULAR_MOVIES_LOADER_CURSOR_ID = 100;
    private static final String SEARCH_QUERY_URL_EXTRA = "tmdbQueryExtra";
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;
    private List<Movie> movieList;
    private static List<Movie> dataBaseMovieList;
    private ProgressBar mLoadingIndicator;
    private String queryUrl;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteDatabase mDb;
    //private static String sortBy;
    private static final  boolean SORT_BY_FAVORITES = false;
    private static final boolean IS_FAVORITE_FLAG = false;


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

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);


        initLoaders();

    }

    private boolean checkIsFavorites() {
       String getPreferedSorted = PopularMoviesPreferences
                .getPreferedSorted(MainActivity.this);

        return getPreferedSorted.equals(getResources().getString(R.string.pref_sort_by_favorites_value));
    }

    @Override
    public void onListItemClick(Movie movie) {
        if (!CheckIsOnline.checkConnection(MainActivity.this) && !checkIsFavorites()) {
            Toast.makeText(getBaseContext(), "no tienes conexion", Toast.LENGTH_LONG).show();
        }else{
            Context context = this;
            Class destinationClass = DetailActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra(MOVIE_OBJECT, movie);
            startActivity(intentToStartDetailActivity);

        }


    }

    private void initLoaders(){

        if (CheckIsOnline.checkConnection(MainActivity.this) && !checkIsFavorites()) {
            getSupportLoaderManager().initLoader(POPULAR_MOVIES_LOADER_ID, null, new LoadNetworkData(MainActivity.this));
        }else if (checkIsFavorites()){
            Toast.makeText(getBaseContext(), "has seleccionado favoritos", Toast.LENGTH_LONG).show();
           getSupportLoaderManager().initLoader(POPULAR_MOVIES_LOADER_CURSOR_ID, null, new LoadCursor(MainActivity.this));
       }else {
            showDialog();
            Toast.makeText(getBaseContext(), "no tienes conexion", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all favorites movies
        if (CheckIsOnline.checkConnection(MainActivity.this) && !checkIsFavorites()) {
            getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_ID, null, new LoadNetworkData(MainActivity.this));
        }else if (checkIsFavorites()){
            Toast.makeText(getBaseContext(), "has seleccionado favoritos", Toast.LENGTH_LONG).show();
            getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_CURSOR_ID, null, new LoadCursor(MainActivity.this));
        }else {
            Toast.makeText(getBaseContext(), "no tienes conexion", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkIsFavorites()) {
            //Check if a loader is not null.
            Loader loader = getSupportLoaderManager().getLoader(POPULAR_MOVIES_LOADER_CURSOR_ID);
            //Check if a loader is already initialized then restart, if not then we initialize it.
            if (loader != null) {
                if (PREFERENCES_HAVE_BEEN_UPDATED) {
                    getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_CURSOR_ID, null, new LoadCursor(MainActivity.this));
                    PREFERENCES_HAVE_BEEN_UPDATED = false;
                } else
                    getSupportLoaderManager().initLoader(POPULAR_MOVIES_LOADER_CURSOR_ID, null, new LoadCursor(MainActivity.this));
            }
        } else {
            //Check if a loader is not null.
            Loader loader = getSupportLoaderManager().getLoader(POPULAR_MOVIES_LOADER_ID);
            //Check if a loader is already initialized then restart, if not then we initialize it.
            if (loader != null) {
                if (PREFERENCES_HAVE_BEEN_UPDATED) {
                    getSupportLoaderManager().restartLoader(POPULAR_MOVIES_LOADER_ID, null, new LoadNetworkData(MainActivity.this));
                    PREFERENCES_HAVE_BEEN_UPDATED = false;
                } else getSupportLoaderManager().initLoader(POPULAR_MOVIES_LOADER_ID, null, new LoadNetworkData(MainActivity.this));
            }
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


    private class LoadCursor implements LoaderManager.LoaderCallbacks<Cursor> {

        Context context;

        public LoadCursor(Context context) {
            this.context = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(context){
                Cursor mMovieCursor;

                @Override
                protected void onStartLoading() {
                    if (mMovieCursor != null) {
                        deliverResult(mMovieCursor);
                    }else {
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

                    }catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data from database." );
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

            List<Movie> dataBaseMovieList = new ArrayList<>();


            if (data == null || data.getCount() == 0 ) {
                mPopularMoviesAdapter.setMovieList(dataBaseMovieList);


            }else {
                while (data.moveToNext()) {

                    int idMovie = data.getInt(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID));
                    String originalTittle = data.getString(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_TITTLE));
                    String overView = data.getString(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS));
                    String posterPath = data.getString(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_POSTER_PATH));
                    String releaseDate = data.getString(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE));
                    Double voteAverage = data.getDouble(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_USER_RATING));
                    List<String> review = Collections.singletonList(data.getString(data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_REVIEWS)));

                    Movie mMovie = new Movie(idMovie, originalTittle, overView, posterPath, releaseDate, voteAverage, review);

                    dataBaseMovieList.add(mMovie);




                }
                mPopularMoviesAdapter.setMovieList(dataBaseMovieList);

            }



        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {




        }
    }

    private class LoadNetworkData implements LoaderManager.LoaderCallbacks<String> {

        Context context;

        public LoadNetworkData(Context context) {
            this.context = context;
        }

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<String>(context) {

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
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
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
        public void onLoaderReset(Loader<String> loader) {

        }
    }

}




