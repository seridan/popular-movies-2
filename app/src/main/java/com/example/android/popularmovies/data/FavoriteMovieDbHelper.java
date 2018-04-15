package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.constraint.ConstraintLayout;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    //data base file name;
    public static final String DATABASE_NAME = "favoriteMovie.db";

    //database version;
    public static final int DATABASE_VERSION = 1;

    //constructor
    public FavoriteMovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //String query that will create our table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITTLE + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_USER_RATING + " DOUBLE NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_REVIEWS + " TEXT NOT NULL" +
                ");";

        //Execute the query passing the string query.
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
