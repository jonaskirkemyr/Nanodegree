package nano.jonask.moviesapp.RestApi.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 29.08.2015.
 */
public class TrailerResponse
{
    @SerializedName("id")
    private int movieId;

    @SerializedName("quicktime")
    private List<Trailer> quickTimeTrailers = new ArrayList<Trailer>();

    @SerializedName("youtube")
    private List<Trailer> youtubeTrailers = new ArrayList<Trailer>();

    public int getMovieId()
    {
        return movieId;
    }

    public List<Trailer> getQuickTimeTrailers()
    {
        return quickTimeTrailers;
    }

    public List<Trailer> getYoutubeTrailers()
    {
        return youtubeTrailers;
    }
}
