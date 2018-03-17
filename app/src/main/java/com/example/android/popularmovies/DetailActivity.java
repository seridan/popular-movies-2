package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.PicassoUtils;

public class DetailActivity extends AppCompatActivity {

    private ImageView posterIv;
    private TextView titleLabel;
    private TextView titleTv;
    private Movie mMovie;
    private String posterUrl;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        posterIv = findViewById(R.id.poster_iv);
        titleLabel = findViewById(R.id.title_label);
        titleTv = findViewById(R.id.title_tv);
        context = this;

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            mMovie = intentThatStartedThisActivity.getParcelableExtra("movieObject");
            if (mMovie != null) {
                posterUrl = mMovie.getPosterPath();
                PicassoUtils.getImageFromUrl(context, posterUrl, posterIv);
                titleTv.setText(mMovie.getOriginalTitle());

            }
        }
    }

}