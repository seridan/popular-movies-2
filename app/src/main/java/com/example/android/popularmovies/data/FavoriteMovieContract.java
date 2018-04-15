package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

public class FavoriteMovieContract {

    public static final class FavoriteMovieEntry implements BaseColumns{

        //Table name -> movie;
        public static final String TABLE_NAME = "movie";

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

        //Column reviews;
        public static final String COLUMN_REVIEWS = "movieReview";


    }
}
