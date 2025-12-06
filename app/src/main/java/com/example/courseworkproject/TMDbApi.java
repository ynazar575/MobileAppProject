package com.example.courseworkproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDbApi {

    @GET("trending/movie/week")
    Call<Movies.MovieResponse> getTrending(@Query("api_key") String apiKey);

    @GET("search/movie")
    Call<Movies.MovieResponse> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);
}