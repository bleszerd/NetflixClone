package com.github.bleszerd.netflixclone.model;

import java.util.List;

/**
 * NetflixClone
 * 15/07/2021 - 10:22
 * Created by bleszerd.
 *
 * @author alive2k@programmer.net
 */
public class MovieDetailModel {
    private final MovieModel movie;
    private final List<MovieModel> moviesSimiler;

    public MovieDetailModel(MovieModel movie, List<MovieModel> moviesSimiler) {
        this.movie = movie;
        this.moviesSimiler = moviesSimiler;
    }

    public MovieModel getMovie() {
        return movie;
    }

    public List<MovieModel> getMoviesSimiler() {
        return moviesSimiler;
    }
}
