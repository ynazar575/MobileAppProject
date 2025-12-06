package com.example.courseworkproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView poster;
    private TextView title, overview, releaseDate;
    private Button btnFavorite;

    private FirebaseAuth auth;
    private CollectionReference favoritesRef;
    private Movies.MovieItem movie;

    private boolean isFavorited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        poster = findViewById(R.id.poster);
        title = findViewById(R.id.title);
        overview = findViewById(R.id.overview);
        releaseDate = findViewById(R.id.releaseDate);
        btnFavorite = findViewById(R.id.btnFavorite);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        favoritesRef = db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("favorites");

        // Get the movie object from intent
        movie = (Movies.MovieItem) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            bindMovie(movie);
            checkIfFavorited();
        }

        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void bindMovie(Movies.MovieItem movie) {
        title.setText(movie.getTitle());
        overview.setText(movie.getOverview());
        releaseDate.setText("Release Date: " + movie.getReleaseDate());

        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(poster);
    }

    private void checkIfFavorited() {
        favoritesRef.document(movie.getId())
                .get()
                .addOnSuccessListener(doc -> {
                    isFavorited = doc.exists();
                    updateButton();
                });
    }

    private void updateButton() {
        btnFavorite.setText(isFavorited ? "Unfavorite" : "Favorite");
    }

    private void toggleFavorite() {
        if (isFavorited) {
            favoritesRef.document(movie.getId())
                    .delete()
                    .addOnSuccessListener(a -> {
                        isFavorited = false;
                        updateButton();
                        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show());
        } else {
            favoritesRef.document(movie.getId())
                    .set(movie)
                    .addOnSuccessListener(a -> {
                        isFavorited = true;
                        updateButton();
                        Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show());
        }
    }
}