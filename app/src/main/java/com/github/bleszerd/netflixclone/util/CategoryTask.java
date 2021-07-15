package com.github.bleszerd.netflixclone.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.github.bleszerd.netflixclone.model.CategoryModel;
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
 * 14/07/2021 - 19:38
 * Created by bleszerd.
 *
 * @author alive2k@programmer.net
 */
public class CategoryTask extends AsyncTask<String, Void, List<CategoryModel>> {

    private final WeakReference<Context> contextWeak;
    private ProgressDialog dialog;
    private CategoryLoader categoryLoader;

    public CategoryTask(Context context) {
        this.contextWeak = new WeakReference<>(context);
    }

    public void setCategoryLoader(CategoryLoader categoryLoader) {
        this.categoryLoader = categoryLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (contextWeak != null)
            dialog = ProgressDialog.show(contextWeak.get(), "Loading", "", true);
    }

    @Override
    protected List<CategoryModel> doInBackground(String... params) {
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

            List<CategoryModel> categories = getCategories(new JSONObject(jsonAsString));
            in.close();
            return categories;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<CategoryModel> getCategories(JSONObject jsonObject) throws JSONException {
        List<CategoryModel> categories = new ArrayList<>();

        JSONArray categoryArray = jsonObject.getJSONArray("category");

        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            String title = category.getString("title");

            List<MovieModel> movies = new ArrayList<>();
            JSONArray movieArray = category.getJSONArray("movie");
            for (int j = 0; j < movieArray.length(); j++) {
                JSONObject movie = movieArray.getJSONObject(j);

                String coverUrl = movie.getString("cover_url");
                int id = movie.getInt("id");

                MovieModel movieObj = new MovieModel();
                movieObj.setCoverUrl(coverUrl);
                movieObj.setId(id);

                movies.add(movieObj);
            }

            CategoryModel categoryObj = new CategoryModel();
            categoryObj.setName(title);
            categoryObj.setMovies(movies);

            categories.add(categoryObj);
        }

        return categories;
    }

    @Override
    protected void onPostExecute(List<CategoryModel> categoryModels) {
        super.onPostExecute(categoryModels);
        dialog.dismiss();

        if (categoryLoader != null) {
            categoryLoader.onResult(categoryModels);
        }
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

    public interface CategoryLoader {
        void onResult(List<CategoryModel> categories);
    }
}
