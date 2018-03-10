package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

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

    final static String TAG = NetworkUtils.class.getSimpleName();

    final static String MOVIE_DISCOVER_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie";

    final static String API_KEY_PARAM = "api_key";
    final static String apiKey = "053875c05af844b2914df74028efb926";

    final static String PARAM_SORT = "sort_by";
    //final static String sortBy = "popularity.desc";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE_PATH = "w185/";


    public static URL buildSortedUrl(String sortBy) {
        Uri builtUri = Uri.parse(MOVIE_DISCOVER_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(PARAM_SORT, sortBy)
                .build();

        URL sortedUrl = null;
        try {
            sortedUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + sortedUrl);
        return sortedUrl;
    }

    public static URL buildImageUrl (String backDropPath){
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .appendPath(backDropPath)
                .build();

        URL imageUrl = null;
        try {
            imageUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + imageUrl);
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
