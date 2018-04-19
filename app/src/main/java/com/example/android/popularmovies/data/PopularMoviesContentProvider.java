package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import static com.example.android.popularmovies.data.PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME;

public class PopularMoviesContentProvider extends ContentProvider{

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // directory
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_MOVIES, MOVIES);
        // single item
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }


    private PopularMoviesDbHelper mPopularMoviesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mPopularMoviesDbHelper = new PopularMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mPopularMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

                default:
                    throw new UnsupportedOperationException("Unknow uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

       return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mPopularMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {

                    returnUri = ContentUris.withAppendedId(PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI, id);

                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

                default:
                    throw new UnsupportedOperationException("Unknow uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
