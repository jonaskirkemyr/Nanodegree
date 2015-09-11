package nano.jonask.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jonas Kirkemyr on 16.07.2015.
 */
public class Movie implements Parcelable
{
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {

        @Override
        public Movie createFromParcel(Parcel source)
        {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };
    /**
     * id of movie
     */
    @SerializedName("id")
    private int id;
    /**
     * title of movie
     */
    @SerializedName("title")
    private String title;
    /**
     * overview of movie
     */
    @SerializedName("overview")
    private String overview;
    /**
     * which language movie is in
     */
    @SerializedName("original_language")
    private String language;
    /**
     * poster path
     */
    @SerializedName("poster_path")
    private String poster;
    /**
     * when the movies was released
     */
    @SerializedName("release_date")
    private Date releaseDate;
    @SerializedName("vote_average")
    private String voteAverage;
    @SerializedName("imdb_id")
    private String imdbId;
    @SerializedName("runtime")
    private int runTime;
    @SerializedName("trailers")
    private ArrayList<String> trailers;

    public Movie(Parcel in)
    {
        trailers = new ArrayList<>();

        id = in.readInt();
        title = in.readString();
        overview = in.readString();
        language = in.readString();
        poster = in.readString();
        setReleaseDate(in.readString());
        voteAverage = in.readString();

        imdbId = in.readString();
        runTime = in.readInt();

        //in.readStringList(trailers);//todo: fix reading array list!
    }

    /**
     * @param releaseDate
     *
     * @return boolean wheter date was accepted or not as a valid date
     */
    public boolean setReleaseDate(String releaseDate)
    {
        DateFormat temp = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try
        {
            this.releaseDate = temp.parse(releaseDate);
        }
        catch (ParseException e)
        {

            return false;
        }

        return true;
    }

    public Movie()
    {
    }

    /**
     * @return
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * @param language
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }

    /**
     * @return
     */
    public String getPoster()
    {
        return poster;
    }

    /**
     * @param poster
     */
    public void setPoster(String poster)
    {
        this.poster = poster;
    }

    public long getReleaseTimeStamp()
    {
        if (this.releaseDate != null)
            return releaseDate.getTime();
        return 0;
    }

    public String getOverview()
    {
        return overview;
    }

    public void setOverview(String overview)
    {
        this.overview = overview;
    }

    public String getVoteAverage()
    {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage)
    {
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(language);
        dest.writeString(poster);
        dest.writeString(getReleaseDate("yyyy-MM-dd"));
        dest.writeString(voteAverage);

        dest.writeString(imdbId);
        dest.writeInt(runTime);
    }

    /**
     * @return
     */
    public String getReleaseDate(String format)
    {

        if (this.releaseDate != null)
        {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(this.releaseDate);
        }
        return null;
    }

    public String toString()
    {
        return getTitle() + " " + getId();
    }

    /**
     * @return
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @return int
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
}
