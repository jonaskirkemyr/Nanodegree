package nano.jonask.moviesapp.RestApi.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 29.08.2015.
 */
public class ReviewResponse
{
    @SerializedName("id")
    private int movieId;

    @SerializedName("results")
    private List<Review> reviews = new ArrayList<Review>();

    public int getMovieId()
    {
        return movieId;
    }

    public List<Review> getReviews()
    {
        return reviews;
    }
}
