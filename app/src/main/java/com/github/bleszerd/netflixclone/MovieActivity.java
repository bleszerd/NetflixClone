package com.github.bleszerd.netflixclone;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.bleszerd.netflixclone.model.MovieDetailModel;
import com.github.bleszerd.netflixclone.model.MovieModel;
import com.github.bleszerd.netflixclone.util.ImageTask;
import com.github.bleszerd.netflixclone.util.MovieDetailTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {

    private TextView txtTitle;
    private TextView txtDesc;
    private TextView txtCast;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        txtTitle = findViewById(R.id.text_view_title);
        txtDesc = findViewById(R.id.text_view_desc);
        txtCast = findViewById(R.id.text_view_cast);
        recyclerView = findViewById(R.id.recycler_view_similar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
            getSupportActionBar().setTitle(null);
        }

        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.shadows);
        if (drawable != null) {
            Drawable movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4);
            drawable.setDrawableByLayerId(R.id.cover_drawable, movieCover);
//            ((ImageView) findViewById(R.id.image_view_cover)).setImageDrawable(drawable);
        }

        List<MovieModel> movies = new ArrayList<>();

        movieAdapter = new MovieAdapter(movies);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(movieAdapter);

        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey("id")) {
            int id = extras.getInt("id");
            if (id > 3 || id < 1){
                id = 3;
                Toast.makeText(this, "Devido a falta de dados no servidor o filme com o id 03 está sendo exibido", Toast.LENGTH_LONG).show();
            }

            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDetailModel movieDetail) {
        txtTitle.setText(movieDetail.getMovie().getTitle());
        txtDesc.setText(movieDetail.getMovie().getDesc());
        txtCast.setText(movieDetail.getMovie().getCast());
        imgCover = findViewById(R.id.image_view_cover);


        ImageTask imageTask = new ImageTask(imgCover);
        imageTask.setShadowEnabled(true);
        imageTask.execute(movieDetail.getMovie().getCoverUrl());

        movieAdapter.setMovies(movieDetail.getMoviesSimiler());
    }

    private static class MovieHolder extends RecyclerView.ViewHolder {

        ImageView imgViewCover;

        public MovieHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            imgViewCover = itemView.findViewById(R.id.image_view_cover);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private List<MovieModel> movies;

        private MovieAdapter(List<MovieModel> movies) {
            this.movies = movies;
        }

        public void setMovies(List<MovieModel> movies) {
            this.movies.clear();
            this.movies.addAll(movies);
            this.notifyDataSetChanged();
        }

        @NonNull
        @NotNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MovieHolder holder, int position) {
            MovieModel movie = movies.get(position);
            new ImageTask(holder.imgViewCover).execute(movie.getCoverUrl());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }
}