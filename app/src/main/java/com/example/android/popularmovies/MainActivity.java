package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrlDisplayTextView = (TextView) findViewById(R.id.textViewUrl);
        mSearchResultsTextView = (TextView) findViewById(R.id.textViewResults);
        initQuery();
    }

   private void initQuery (){
       String sortBy = "popularity.desc";
       URL tmdbQueryUrl = NetworkUtils.buildUrl(sortBy);
       //mUrlDisplayTextView.setText(tmdbQueryUrl.toString());
       new TmdbQueryTask().execute(tmdbQueryUrl);
   }

    public class TmdbQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String tmdbQueryResults = null;
            try {
                tmdbQueryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmdbQueryResults;
        }

        @Override
        protected void onPostExecute(String tmdbQueryResults) {
            if (tmdbQueryResults != null && !tmdbQueryResults.equals("")) {
                mSearchResultsTextView.setText(tmdbQueryResults);
                try {
                    List<Movie> json = JsonUtils.parseMovieList(tmdbQueryResults);
                    mUrlDisplayTextView.setText(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
