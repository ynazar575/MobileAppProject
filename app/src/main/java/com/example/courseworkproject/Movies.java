package com.example.courseworkproject;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Movies {

    // Response from TMDb
    public class MovieResponse {
        @SerializedName("results")
        private List<MovieItem> results;
        public List<MovieItem> getResults() { return results; }
    }

    // Individual movie item
    public class MovieItem implements Serializable {
        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("overview")
        private String overview;
        @SerializedName("poster_path")
        private String posterPath;
        @SerializedName("release_date")
        private String releaseDate;

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getOverview() { return overview; }
        public String getPosterPath() { return posterPath; }
        public String getReleaseDate() { return releaseDate; }
    }
}
