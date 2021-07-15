package com.github.bleszerd.netflixclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.bleszerd.netflixclone.model.CategoryModel;
import com.github.bleszerd.netflixclone.model.MovieModel;
import com.github.bleszerd.netflixclone.util.CategoryTask;
import com.github.bleszerd.netflixclone.util.ImageTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {

    private MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.recycler_view_main);

        List<CategoryModel> categories = new ArrayList<>();
        List<MovieModel> movies = new ArrayList<>();

        for (int j = 0; j < 10; j++) {
            CategoryModel category = new CategoryModel();
            category.setName("Cat " + j);

            for (int i = 0; i < 30; i++) {
                MovieModel movie = new MovieModel();
//                movie.setCoverUrl(R.drawable.movie);
                movies.add(movie);
            }

            category.setMovies(movies);
            categories.add(category);
        }

        mainAdapter = new MainAdapter(categories);
        rv.setAdapter(mainAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoader(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");
    }

    @Override
    public void onResult(List<CategoryModel> categories) {
        mainAdapter.setCategories(categories);
    }

    private static class CategoryHolder extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        RecyclerView recyclerMovie;

        public CategoryHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            txtViewTitle = itemView.findViewById(R.id.text_view_title);
            recyclerMovie = itemView.findViewById(R.id.recycler_view_movie);
        }
    }

    private static class MovieHolder extends RecyclerView.ViewHolder {

        ImageView imgViewCover;

        public MovieHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            imgViewCover = itemView.findViewById(R.id.image_view_cover);
            itemView.setOnClickListener(v -> {
                onItemClickListener.onClick(getAdapterPosition());
            });
        }
    }

    private class MainAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private List<CategoryModel> categories;

        private MainAdapter(List<CategoryModel> categories) {
            this.categories = categories;
        }

        @NonNull
        @NotNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new CategoryHolder(getLayoutInflater().inflate(R.layout.category_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MainActivity.CategoryHolder holder, int position) {
            CategoryModel category = categories.get(position);
            holder.txtViewTitle.setText(category.getName());
            holder.recyclerMovie.setAdapter(new MovieAdapter(category.getMovies()));
            holder.recyclerMovie.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        void setCategories(List<CategoryModel> categories) {
            this.categories.clear();
            this.categories.addAll(categories);
            mainAdapter.notifyDataSetChanged();
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> implements OnItemClickListener{

        private final List<MovieModel> movies;

        private MovieAdapter(List<MovieModel> movies) {
            this.movies = movies;
        }

        @NonNull
        @NotNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.movie_item, parent, false);
            return new MovieHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MainActivity.MovieHolder holder, int position) {
            MovieModel movie = movies.get(position);
            new ImageTask(holder.imgViewCover).execute(movie.getCoverUrl());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        @Override
        public void onClick(int position) {
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra("id", movies.get(position).getId());
            startActivity(intent);
        }
    }

    interface  OnItemClickListener{
        void onClick(int position);
    }
}