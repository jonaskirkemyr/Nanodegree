package nano.jonask.moviesapp.RestApi;

import nano.jonask.moviesapp.Movie;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

/**
 * Created by Jonas Kirkemyr on 20.07.2015.
 */
public interface MovieDbApi {

    @GET("/discover/movie")
    void getMovies(@Query("sort_by") String sortBy, Callback<List<Movie>> callback);
}
