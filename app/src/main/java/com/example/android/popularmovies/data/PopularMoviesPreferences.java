package com.example.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.popularmovies.R;

/**
 * Created by seridan on 22/03/2018.
 */

public class PopularMoviesPreferences {

    public static boolean isShortedByPopularity(Context context) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForSortBy = context.getString(R.string.pref_sort_by_key);
        String defaultSortBy = context.getString(R.string.pref_sort_by_top_rated_value);
        String preferredSortBy = prefs.getString(keyForSortBy, defaultSortBy);
        String sortByTopRated = context.getString(R.string.pref_sort_by_top_rated_value);
        boolean userPrefersSortByTopRated;
        if (sortByTopRated.equals(preferredSortBy)) {
            userPrefersSortByTopRated = true;
        } else {
            userPrefersSortByTopRated = false;
        }
        return userPrefersSortByTopRated;
    }

    public static String getPreferdSorted (Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForSortBy = context.getString(R.string.pref_sort_by_key);
        String defaultSortBy = context.getString(R.string.pref_sort_by_top_rated_value);
        return preferences.getString(keyForSortBy, defaultSortBy);
    }
}