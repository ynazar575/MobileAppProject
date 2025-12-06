package com.example.courseworkproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private ArrayList<Movies.MovieItem> favoriteMovies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteMovies = new ArrayList<>();
        adapter = new MoviesAdapter(getContext(), favoriteMovies, true, movie -> {
            Intent i = new Intent(getContext(), MovieDetailsActivity.class);
            i.putExtra("movie", movie);
            startActivity(i);
        });
        recyclerView.setAdapter(adapter);

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    favoriteMovies.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshot.getDocuments()) {
                        Movies.MovieItem movie = doc.toObject(Movies.MovieItem.class);
                        if (movie != null) favoriteMovies.add(movie);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}