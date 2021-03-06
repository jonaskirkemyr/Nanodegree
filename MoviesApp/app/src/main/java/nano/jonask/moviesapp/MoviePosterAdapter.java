package nano.jonask.moviesapp;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jonas Kirkemyr on 15.07.2015.
 */
public class MoviePosterAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> movies;

    public MoviePosterAdapter(Context c) {
        this.context = c;
        this.movies = new ArrayList<Movie>();
    }

    /**
     * set movie list do display in the grid
     *
     * @param movies
     */
    public void setMovieList(Movie[] movies) {
        if (this.movies != null) {
            this.movies.clear();
            Collections.addAll(this.movies, movies);
            notifyDataSetChanged();//notify of data change
        }
    }

    public void setMovieList(ArrayList movies) {
        if (this.movies != null) {
            this.movies.clear();
            this.movies = movies;
            notifyDataSetChanged();
        }
    }

    public int getCount() {
        return movies.size();
    }


    public Object getItem(int position) {
        if (position >= 0 & position < getCount())
            return movies.get(position);
        return null;
    }


    public long getItemId(int position) {
        if (position >= 0 & position < getCount())
            return movies.get(position).getId();
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else
            imageView = (ImageView) convertView;

        Movie movieTemp = movies.get(position);
        // src: https://github.com/square/picasso/issues/609
        try {
            Picasso.with(context).load(movieTemp.getPoster()).into(imageView);
        } catch (IllegalArgumentException e) {
            movieTemp.setPoster(null);
            imageView.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_delete));
        }


        return imageView;
    }


    public void clearMovies() {
        movies.clear();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public ArrayList<Movie> getMovies() {
        return (ArrayList<Movie>) movies;
    }
}
