package com.github.bleszerd.netflixclone.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.github.bleszerd.netflixclone.model.MovieDetailModel;
import com.github.bleszerd.netflixclone.model.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * NetflixClone
 * 15/07/2021 - 10:24
 * Created by bleszerd.
 *
 * @author alive2k@programmer.net
 */
public class MovieDetailTask extends AsyncTask<String, Void, MovieDetailModel> {
    private final WeakReference<Context> contextWeak;
    private Dialog dialog;
    private MovieDetailLoader movieDetailLoader;

    public MovieDetailTask(Context context) {
        this.contextWeak = new WeakReference<>(context);
    }

    public void setMovieDetailLoader(MovieDetailLoader movieDetailLoader) {
        this.movieDetailLoader = movieDetailLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (contextWeak != null)
            dialog = ProgressDialog.show(contextWeak.get(), "Loading", "", true);
    }

    @Override
    protected MovieDetailModel doInBackground(String... params) {
        String url = params[0];

        try {
            URL requestUrl = new URL(url);

            HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int response = urlConnection.getResponseCode();
            if (response >= 400) {
                throw new IOException("Connection error");
            }

//            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String jsonAsString = toString(in);

            MovieDetailModel movieDetail = getMovieDetail(new JSONObject(jsonAsString));
            in.close();
            return movieDetail;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private MovieDetailModel getMovieDetail(JSONObject json) throws JSONException {
        int id = json.getInt("id");
        String title = json.getString("title");
        String desc = json.getString("desc");
        String cast = json.getString("cast");
        String coverUrl = json.getString("cover_url");

        List<MovieModel> movies = new ArrayList<>();
        JSONArray movieArray = json.getJSONArray("movie");
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObj = movieArray.getJSONObject(i);
            String coverSimilar = movieObj.getString("cover_url");
            int idSimilar = movieObj.getInt("id");

            MovieModel similar = new MovieModel();
            similar.setId(idSimilar);
            similar.setCoverUrl(coverSimilar);

            movies.add(similar);
        }

        MovieModel movie = new MovieModel();
        movie.setId(id);
        movie.setCoverUrl(coverUrl);
        movie.setTitle(title);
        movie.setDesc(desc);
        movie.setCast(cast);

        return new MovieDetailModel(movie, movies);
    }

    public interface MovieDetailLoader {
        void onResult(MovieDetailModel movieDetail);
    }

    @Override
    protected void onPostExecute(MovieDetailModel movieDetailModel) {
        super.onPostExecute(movieDetailModel);
        dialog.dismiss();

        if (movieDetailLoader != null)
            movieDetailLoader.onResult(movieDetailModel);
    }

    private String toString(InputStream is) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        while ((read = is.read(bytes)) > 0) {
            baos.write(bytes, 0, read);
        }

        return new String(baos.toByteArray());
    }
}
