package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by seridan on 04/03/2018.
 * These utilities will be used to communicate with the network.
 */


public class NetworkUtils {

    private final static String TAG = NetworkUtils.class.getSimpleName();

    private final static String MOVIE_DISCOVER_BASE_URL =
            "https://api.themoviedb.org/3/movie";

    private final static String API_KEY_PARAM = "api_key";
    private static String apiKey = BuildConfig.API_KEY; //***** Set the api key here******

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_PATH = "w342";

    private final static String MOVIE_BACKDROP_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    private static final String IMAGES_PATH = "images";
    private static final String INCLUDE_IMAGE_LANGUAGE = "include_image_language";
    private static final String includeImageLanguage = "include_image_language=en,null";

    private static final String APPEND_TO_RESPONSE_QUERY = "append_to_response_query";
    private static String appendToResponseParam = "videos,images,reviews";




    public static URL buildSortedUrl(String sortBy) {
        Uri builtUri = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL sortedUrl = null;
        try {
            sortedUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built sorted URI " + sortedUrl);
        return sortedUrl;
    }

    public static URL buildDetailsMovieUrl (int movieId){
        Uri builtUri = Uri.parse(MOVIE_BACKDROP_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendEncodedPath(IMAGES_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(APPEND_TO_RESPONSE_QUERY, appendToResponseParam)
                .build();

        URL imageUrl = null;
        try {
            imageUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built deatails Moview URI " + imageUrl);
        return imageUrl;
    }

    public static URL buildBackdropImageUrl (int movieId){
        Uri builtUri = Uri.parse(MOVIE_BACKDROP_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendEncodedPath(IMAGES_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(INCLUDE_IMAGE_LANGUAGE, includeImageLanguage)
                .build();

        URL imageUrl = null;
        try {
            imageUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "BuiltBackdrop URI " + imageUrl);
        return imageUrl;
    }

    public static URL buildImageUrl (String backDropPath){
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .appendEncodedPath(backDropPath)
                .build();

        URL imageUrl = null;
        try {
            imageUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built image URI " + imageUrl);
        return imageUrl;
    }


    public static String getResponseFromHttpUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
