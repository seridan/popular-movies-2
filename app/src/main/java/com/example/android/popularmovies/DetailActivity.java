package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView posterIv;
    private TextView titleLabel;
    private TextView titleTv;
    private Movie mMovie;
    private URL posterUrl;
    private int mMovieId;
    Context context;
    String backdropPath;
    final static String TAG = DetailActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        posterIv = findViewById(R.id.poster_iv);
        titleLabel = findViewById(R.id.title_label);
        titleTv = findViewById(R.id.title_tv);
        context = this;
        backdropPath = null;


        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            mMovie = intentThatStartedThisActivity.getParcelableExtra("movieObject");
            if (mMovie != null) {
                int mMovieId = mMovie.getId();
                loadPosterImage(mMovieId);

            }


            /*if (mMovie != null) {
                int mMovieId = mMovie.getId();
                posterUrl = NetworkUtils.buildBackdropImageUrl(mMovieId);
                new FetchMoviesTask().execute(posterUrl);
                Log.v(TAG, "poster Url " + posterUrl);
                PicassoUtils.getImageFromUrl(context, backdropPath, posterIv);
                Log.v(TAG, "backdropPath " + backdropPath);
                titleTv.setText(mMovie.getOriginalTitle());
            }*/
                /*posterUrl = mMovie.getPosterPath();
                PicassoUtils.getImageFromUrl(context, posterUrl, posterIv);
                titleTv.setText(mMovie.getOriginalTitle());*/


        }
    }


    public class FetchPosterMovieTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String tmdbQueryResult;
            try {
                tmdbQueryResult = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                backdropPath = JsonUtils.getDetailImage(tmdbQueryResult);
                return backdropPath;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PicassoUtils.getImageFromUrl(context, backdropPath, posterIv);
            Log.v(TAG, "backdropPath " + backdropPath);

        }
    }

    private void loadPosterImage (int id){
        posterUrl = NetworkUtils.buildBackdropImageUrl(id);
        new FetchPosterMovieTask().execute(posterUrl);


    }

}