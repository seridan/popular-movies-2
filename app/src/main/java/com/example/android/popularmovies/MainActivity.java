package com.example.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

private RecyclerView mRecyclerView;

private PopularMoviesAdapter mPopularMoviesAdapter;

private Context mContext;

private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_images_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter = new PopularMoviesAdapter(MainActivity.this, movieList);
        mRecyclerView.setAdapter(mPopularMoviesAdapter);

        loadMoviesData();

    }

    private void loadMoviesData() {
        String sortBy = "popularity.desc";
        URL tmdbQueryUrl = NetworkUtils.buildSortedUrl(sortBy);
        new FetchMoviesTask().execute(tmdbQueryUrl);

    }

    public class FetchMoviesTask extends AsyncTask<URL, Void, List<Movie>> {

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
            mPopularMoviesAdapter.setMovieList(movieList);
        }
    }

}
