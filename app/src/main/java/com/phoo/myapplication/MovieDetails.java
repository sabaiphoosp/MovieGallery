package com.phoo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoo.myapplication.Model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetails extends AppCompatActivity {

    private ImageView imageViewPoster;
    private TextView textViewTitle;
    private TextView textViewOverview;
    private TextView textViewReleaseYear;
    public String movieIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        imageViewPoster = findViewById(R.id.imageViewPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewReleaseYear = findViewById(R.id.textViewReleaseYear);

        Intent intent = getIntent();
        movieIdStr = intent.getStringExtra("movieId");

        FetchMovieDetailsTask task = new FetchMovieDetailsTask();
        task.execute();
    }

    private class FetchMovieDetailsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://api.themoviedb.org/3/movie/{MOVIE_ID}?api_key={API_KEY}";
            String movieId = movieIdStr;
            String apiKey = "08e9f2be0ee3f69a7b72c1b26e2a8a25";

            String result = "";

            try {
                URL url = new URL(apiUrl.replace("{MOVIE_ID}", movieId).replace("{API_KEY}", apiKey));
                Log.e("url", String.valueOf(url));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read the response
                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                StringBuilder stringBuilder = new StringBuilder();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    stringBuilder.append(new String(buffer, 0, bytesRead));
                }

                result = stringBuilder.toString();

                // Close the connections
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                // Extract the movie details
                String title = jsonObject.getString("title");
                String overview = jsonObject.getString("overview");
                String releaseDate = jsonObject.getString("release_date");
                String imageUrl = "https://image.tmdb.org/t/p/w500" + jsonObject.getString("poster_path");

                // Set the movie details to the TextViews

                textViewTitle.setText(title);
                textViewOverview.setText(overview);
                textViewReleaseYear.setText(releaseDate);
                Picasso.get().load(imageUrl).into(imageViewPoster);
                // Way 2 Load the movie image using an AsyncTask
               /* LoadMovieImageTask imageTask = new LoadMovieImageTask();
                imageTask.execute(imageUrl);
*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadMovieImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                // Read the image data
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

                // Close the connections
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                // Set the movie image to the ImageView
                imageViewPoster.setImageBitmap(bitmap);
            }
        }
    }
}
