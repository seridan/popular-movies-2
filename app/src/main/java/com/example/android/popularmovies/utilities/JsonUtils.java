package com.example.android.popularmovies.utilities;

import android.util.Log;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seridan on 06/03/2018.
 */

public class JsonUtils {

    private static final String ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String RESULTS = "results";
    private static final String BACKDROPS = "backdrops";
    private static final String FILE_PATH = "file_path";
    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final String IMAGES_PATH = "images";
    private static final String CONTENT_PATH = "content";
    private static final String AUTHOR_PATH = "author";
    private static final String KEY = "key";

    private static final String TAG = JsonUtils.class.toString();

    /**
     * Once we have the String with the JSON of the Movie Discover witch contains a list of the movies
     * and their data we have to parse with this method and then return an ArrayList of the movies.
     *
     * @param json String that contains the JSON.
     * @return ArrayList of the movies.
     * @throws JSONException
     */
    public static List<Movie> parseMovieList(String json) throws JSONException {

        JSONObject parentObject = new JSONObject(json);
        JSONArray resultsArray = parentObject.getJSONArray(RESULTS);
        List<Movie> parseMovieList = new ArrayList<>();

        if (resultsArray != null) {
            for (int i = 0; i < resultsArray.length(); i++) {
                Movie parseMovie = new Movie();
                JSONObject movieObject = resultsArray.getJSONObject(i);
                parseMovie.setId(movieObject.optInt(ID));
                parseMovie.setOriginalTitle(movieObject.optString(ORIGINAL_TITLE));
                parseMovie.setPosterPath(movieObject.optString(POSTER_PATH));
                parseMovie.setOverview(movieObject.optString(OVERVIEW));
                parseMovie.setVote_average(movieObject.optDouble(VOTE_AVERAGE));
                parseMovie.setReleaseDate(movieObject.optString(RELEASE_DATE));

                parseMovieList.add(parseMovie);
            }
        }
        Log.v(TAG, "list movies " + parseMovieList.toString());
        return parseMovieList;
    }



    /**
     * This method parse the JSON of the image that we'll use in the detail activity.
     *
     * @param json String that contains the JSON from the URL.
     * @return String with the path to the image taking from the JSON.
     * @throws JSONException
     */

    public static String getDetailImage (String json) throws JSONException {
        String filePath;
        JSONObject parentObject = new JSONObject(json);
        JSONArray backDropArray = parentObject.getJSONArray(BACKDROPS);

        if (backDropArray != null && backDropArray.length() > 0) {

            JSONObject backDroPObject = backDropArray.getJSONObject(0);
            if (backDroPObject != null){
                filePath = backDroPObject.getString(FILE_PATH);
                Log.v(TAG, "parse filepath " + filePath);
                return filePath;
            }
        }
        return null;
    }

   
    public static List<String> getVideosMovie(String movieJson) throws JSONException {
        JSONObject parentObject = new JSONObject(movieJson);
        JSONArray videosArray = parentObject.getJSONArray(RESULTS);
        List<String> stringList = new ArrayList<>();

        if (videosArray != null) {

            for (int i = 0; i < videosArray.length(); i++) {
                JSONObject videoId = videosArray.getJSONObject(i);
                stringList.add((String) videoId.get(KEY));
            }
            Log.v(TAG, "key video array " + stringList.toString());
            return stringList;
        }

        return null;
    }

    public static List<String> getReviewsMovie(String movieJson) throws JSONException {
        JSONObject parentObject = new JSONObject(movieJson);
        JSONArray reviewsArray = parentObject.getJSONArray(RESULTS);
        List<String> stringList = new ArrayList<>();

        if (reviewsArray != null) {
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject reviews = reviewsArray.getJSONObject(i++);
                String author = reviews.getString(AUTHOR_PATH);
                String content = reviews.getString(CONTENT_PATH);
                stringList.add("Author: " + author + "\n" + content + "\n");
            }
            Log.v(TAG, "reviews array " + stringList.toString());
            return stringList;
        }

        return null;
    }
    }

