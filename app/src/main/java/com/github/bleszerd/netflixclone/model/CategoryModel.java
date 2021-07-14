package com.github.bleszerd.netflixclone.model;

import java.util.List;

/**
 * NetflixClone
 * 14/07/2021 - 09:58
 * Created by bleszerd.
 *
 * @author alive2k@programmer.net
 */
public class CategoryModel {
    private String name;
    private List<MovieModel> movies;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MovieModel> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
    }
}
