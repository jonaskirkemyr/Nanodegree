package nano.jonask.moviesapp.RestApi.Response;

import com.google.gson.annotations.SerializedName;
import nano.jonask.moviesapp.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 29.08.2015.
 */
public class MovieDiscover
{
    @SerializedName("page")
    private int page;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("results")
    private List<Movie> movies = new ArrayList<Movie>();

    public int getPage()
    {
        return page;
    }

    public int getTotalPages()
    {
        return totalPages;
    }

    public int getTotalResults()
    {
        return totalResults;
    }

    public List<Movie> getMovies()
    {
        return movies;
    }
}
