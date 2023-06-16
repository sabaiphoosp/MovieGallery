package com.phoo.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.phoo.myapplication.Adapter.MovieAdapter;
import com.phoo.myapplication.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "08e9f2be0ee3f69a7b72c1b26e2a8a25";
    private static final String API_BASE_URL = "https://api.themoviedb.org/3";
    private static final String POPULAR_MOVIES_ENDPOINT = "/movie/popular";
    private static final String SEARCH_MOVIES_ENDPOINT = "/search/movie";

    private ListView movieListView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private ArrayAdapter<Movie> movieListAdapter;
    private ArrayList<Movie> movieList = new ArrayList<Movie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieListView = findViewById(R.id.movieListView);
        searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.progressBar);

        movieListAdapter = new MovieAdapter(this, movieList);
        movieListView.setAdapter(movieListAdapter);
        movieListView.setOnItemClickListener((parent, view, position, id) -> {
            String movieTitle = movieList.get(position).getTitle();
            Intent i = new Intent(MainActivity.this, MovieDetails.class);
            i.putExtra("movieId", movieList.get(position).getId());
            startActivity(i);
            Toast.makeText(MainActivity.this, "Movie details: " + movieTitle, Toast.LENGTH_SHORT).show();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovies(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        getPopularMovies();
    }

    private void getPopularMovies() {
        String url = API_BASE_URL + POPULAR_MOVIES_ENDPOINT + "?api_key=" + API_KEY;
        new FetchMoviesTask().execute(url);
    }

    private void searchMovies(String query) {
        String encodedQuery = query.replaceAll(" ", "%20");
        String url = API_BASE_URL + SEARCH_MOVIES_ENDPOINT + "?api_key=" + API_KEY + "&query=" + encodedQuery;
        new FetchMoviesTask().execute(url);
    }

    private void displayMovieList(JSONArray movies) {
        movieList.clear();
        try {
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movie = movies.getJSONObject(i);
                String title = movie.getString("title");
                String id = movie.getString("id");
                String release_date = movie.getString("release_date");
                String posterPath = movie.getString("poster_path");
                movieList.add(new Movie(id, title, posterPath,release_date));
            }
            movieListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getJSONArray("results");
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray movies) {
            progressBar.setVisibility(View.GONE);
            if (movies != null) {
                displayMovieList(movies);
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch movies.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
