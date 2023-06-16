package com.phoo.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.phoo.myapplication.Model.Movie;
import com.phoo.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context context;
    private List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.item_movie, movies);
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        }

        ImageView imageViewPoster = convertView.findViewById(R.id.imageViewPoster);
        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewReleaseYear = convertView.findViewById(R.id.textViewReleaseYear);

        Movie movie = movies.get(position);

        textViewTitle.setText(movie.getTitle());
        textViewReleaseYear.setText(movie.getReleaseYear());

        if (movie.getPosterPath() != null) {
            String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Picasso.get().load(imageUrl).into(imageViewPoster);
        } else {
            imageViewPoster.setImageResource(R.drawable.ic_launcher_background);
        }
        return convertView;
    }
}