package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PopularMoviesContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class FavoriteMovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        //Table name -> movie;
        public static final String TABLE_NAME = "movies";

        //Column id movie;
        public static final String COLUMN_MOVIE_ID = "movieId";

        //Column tittle movie;
        public static final String COLUMN_TITTLE = "tittle";

        //Column synopsis;
        public static final String COLUMN_SYNOPSIS = "synopsis";

        //Column user rating;
        public static final String COLUMN_USER_RATING = "userRating";

        //Column release date;
        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        //Column backdrop path;
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";

        //Column backdrop path;
        public static final String COLUMN_POSTER_PATH = "posterPath";

        //Column reviews;
        public static final String COLUMN_REVIEWS = "movieReview";

    }
}
