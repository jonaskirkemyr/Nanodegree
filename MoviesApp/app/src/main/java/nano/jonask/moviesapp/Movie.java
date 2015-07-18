package nano.jonask.moviesapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jonas Kirkemyr on 16.07.2015.
 */
public class Movie implements Serializable
{
    /**
     * id of movie
     */
    private int id;

    /**
     * title of movie
     */
    private String title;

    /** overview of movie*/
    private String overview;

    /**
     * which language movie is in
     */
    private String language;

    /**
     * poster path
     */
    private String poster;

    /**
     * when the movies was released
     */
    private Date releaseDate;

    private String voteAverage;


    /**
     *
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     */
    public String getPoster() {
        return poster;
    }

    /**
     *
     * @param poster
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     *
     * @return
     */
    public String getReleaseDate(String format) {

        if(this.releaseDate!=null)
        {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(this.releaseDate);
        }
        return null;
    }

    /**
     *
     * @param releaseDate
     * @return boolean wheter date was accepted or not as a valid date
     */
    public boolean setReleaseDate(String releaseDate) {
        DateFormat temp=new SimpleDateFormat("yyyy-MM-d", Locale.ENGLISH);

        try {
            this.releaseDate=temp.parse(releaseDate);
        } catch (ParseException e) {

            return false;
        }

        return true;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
}
