package nano.jonask.moviesapp.RestApi;

import nano.jonask.moviesapp.RestApi.Response.MovieDiscover;
import nano.jonask.moviesapp.RestApi.Response.ReviewResponse;
import nano.jonask.moviesapp.RestApi.Response.TrailerResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Jonas Kirkemyr on 20.07.2015.
 */
public interface MovieDbApi
{

    @GET("/discover/movie")
        //void getMovies(@Query("sort_by") String sortBy, Callback<List<Movie>> callback);
    void getMovies(@Query("sort_by") String sortBy, Callback<MovieDiscover> callback);

    @GET("/movie/{id}/reviews")
    void getMovieReviews(@Path("id") int movieId, Callback<ReviewResponse> callback);

    @GET("/movie/{id}/trailers")
    void getMovieTrailers(@Path("id") int movieId, Callback<TrailerResponse> callback);
}
