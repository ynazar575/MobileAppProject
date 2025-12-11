package com.example.courseworkproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private EditText searchInput;
    private Button searchBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<Movies.MovieItem> movieList;
    private MoviesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchBtn = view.findViewById(R.id.searchBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        movieList = new ArrayList<>();

        adapter = new MoviesAdapter(requireContext(), movieList,false, movie -> {
            // Click to open movie details
            if (getContext() != null) {
                Intent i = new Intent(getContext(), MovieDetailsActivity.class);
                i.putExtra("movie", movie);
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                searchMovies(query);
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Enter a movie name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void searchMovies(String query) {
        progressBar.setVisibility(View.VISIBLE);
        TMDbApi api = retrofit.getInstance().create(TMDbApi.class);
        String apiKey = BuildConfig.TMDB_API_KEY;
        api.searchMovies(apiKey, query).enqueue(new Callback<Movies.MovieResponse>() {
            @Override
            public void onResponse(Call<Movies.MovieResponse> call, Response<Movies.MovieResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "No movies found", Toast.LENGTH_SHORT).show();
                }
            }
            }

            @Override
            public void onFailure(Call<Movies.MovieResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (getContext() != null) {
                    String errorMsg = t.getMessage() != null && t.getMessage().contains("Unable to resolve host") 
                        ? "No internet connection" 
                        : "Failed to fetch movies";
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}