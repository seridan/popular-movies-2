package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seridan on 06/03/2018.
 */

public class Movie implements Parcelable {

    private int id;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String releaseDate;
    private double vote_average;
    private List<String> videos;
    private List<String> reviews;

    public Movie() {
    }

    public Movie(int id, String originalTitle, String overview, String posterPath, String releaseDate,
    double vote_average, List<String> trailers, List<String> reviews){
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.vote_average = vote_average;
        this.videos = trailers;
        this.reviews = reviews;
    }

    public Movie(int id, String originalTitle, String overview, String posterPath, String releaseDate, double vote_average, List<String> reviews) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.vote_average = vote_average;
        this.reviews = reviews;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        vote_average = in.readDouble();
        videos = in.createStringArrayList();
        reviews = in.createStringArrayList();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideo(List<String> videos) {
        this.videos = videos;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        parcel.writeString(overview);
        parcel.writeString(posterPath);
        parcel.writeString(releaseDate);
        parcel.writeDouble(vote_average);
        parcel.writeStringList(videos);
        parcel.writeStringList(reviews);
    }
}
