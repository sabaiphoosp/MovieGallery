package com.phoo.myapplication.Model;

public class Movie {
    public String id;
    public String title;
    private String overview;
    private String releaseYear;

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    private String posterPath;

    public Movie(String id, String title, String posterPath, String releaseYear) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseYear = releaseYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return getTitle(); // You can add anything else like maybe getDrinkType()
    }
}
