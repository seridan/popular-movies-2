package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.popularmovies.DetailActivity;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    final static String TAG = PopularMoviesDbHelper.class.getSimpleName();

    //data base file name;
    public static final String DATABASE_NAME = "favoriteMovie.db";

    //database version;
    public static final int DATABASE_VERSION = 2;

    //constructor
    public PopularMoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //String query that will create our table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                PopularMoviesContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_TITTLE + " TEXT NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_USER_RATING + " DOUBLE NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                PopularMoviesContract.FavoriteMovieEntry.COLUMN_REVIEWS + " TEXT NOT NULL);";

        //Execute the query passing the string query.
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
