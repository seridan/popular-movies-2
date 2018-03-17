package com.example.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.ListItemClickListener {

    private static final String MOVIE_OBJECT = "movieObject";
    private RecyclerView mRecyclerView;

private PopularMoviesAdapter mPopularMoviesAdapter;

private List<Movie> movieList;

private ProgressBar mLoadingIndicator;

private static String sortBy;

    Toast toast;


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
        sortBy = "popularity.desc";

        checkConnectionAndExecute();

    }

    private void loadMoviesData(String sortBy) {


            URL tmdbQueryUrl = NetworkUtils.buildSortedUrl(sortBy);
            new FetchMoviesTask().execute(tmdbQueryUrl);

    }

    @Override
    public void onListItemClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(MOVIE_OBJECT, movie);
        startActivity(intentToStartDetailActivity);
       /* if(toast != null){
            toast.cancel();
        }
        String toastMessage = "Tittle: " + movie.getOriginalTitle();
        toast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        toast.show();*/
    }

    public class FetchMoviesTask extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(URL... urls) {

            URL searchUrl = urls[0];
            String tmdbQueryResult;
            try {
                tmdbQueryResult = NetworkUtils.getResponseFromHttpUrl(searchUrl);

                movieList = JsonUtils.parseMovieList(tmdbQueryResult);

                return movieList;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mPopularMoviesAdapter.setMovieList(movieList);

        }
    }

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
}
