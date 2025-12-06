package com.example.courseworkproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onClick(Movies.MovieItem movie);
    }

    private Context context;
    private List<Movies.MovieItem> movies;
    private boolean isFavoritesTab; // true for favorites fragment
    private OnMovieClickListener clickListener;

    private FirebaseAuth auth;
    private CollectionReference favoritesRef;

    public MoviesAdapter(Context context, List<Movies.MovieItem> movies,
                         boolean isFavoritesTab, OnMovieClickListener clickListener) {
        this.context = context;
        this.movies = movies;
        this.isFavoritesTab = isFavoritesTab;
        this.clickListener = clickListener;

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        favoritesRef = db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("favorites");
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_saved, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movies.MovieItem movie = movies.get(position);

        holder.title.setText(movie.getTitle());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> clickListener.onClick(movie));

        if (isFavoritesTab) {
            holder.btnFavorite.setText("Unfavorite");
            holder.btnFavorite.setEnabled(true);
            holder.btnFavorite.setOnClickListener(v -> removeFavorite(movie));
        } else {
            if (favoriteIds.contains(movie.getId())) {
                holder.btnFavorite.setText("Favorited");
                holder.btnFavorite.setEnabled(false);
            } else {
                holder.btnFavorite.setText("Favorite");
                holder.btnFavorite.setEnabled(true);
                holder.btnFavorite.setOnClickListener(v -> addFavorite(movie));
            }
        }
    }

    private void addFavorite(Movies.MovieItem movie) {
        favoritesRef.document(movie.getId())
                .set(movie)
                .addOnSuccessListener(a -> Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to add favorite", Toast.LENGTH_SHORT).show());
    }

    private void removeFavorite(Movies.MovieItem movie) {
        favoritesRef.document(movie.getId())
                .delete()
                .addOnSuccessListener(a -> {
                    movies.remove(movie);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove favorite", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        Button btnFavorite;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
    private Set<String> favoriteIds = new HashSet<>();

    public void setFavoriteIds(Set<String> ids) {
        favoriteIds.clear();
        favoriteIds.addAll(ids);
        notifyDataSetChanged();
    }

}
