package nano.jonask.moviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import nano.jonask.moviesapp.RestApi.MovieDbApi;
import nano.jonask.moviesapp.RestApi.Response.MovieDiscover;
import nano.jonask.moviesapp.RestApi.Response.ReviewResponse;
import nano.jonask.moviesapp.RestApi.Response.TrailerResponse;
import nano.jonask.moviesapp.RestApi.RestClient;
import nano.jonask.moviesapp.data.MoviesContract;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;
import java.util.Vector;

/**
 * Created by Jonas on 27.08.2015.
 */
public class FetchMovieTask
{
    private final Context mContext;

    public FetchMovieTask(Context context)
    {
        mContext = context;
    }

    long addFavorite(int movieId)
    {
        long returnMovieId;
        Cursor favoriteCursor = mContext.getContentResolver().query(MoviesContract.FavoriteEntry.CONTENT_URI, new String[]{MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY}, MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY + " =?", new String[]{Integer.toString(movieId)}, null);

        if (favoriteCursor.moveToFirst())
        {
            int movieIdIndex = favoriteCursor.getColumnIndex(MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY);
            returnMovieId = favoriteCursor.getLong(movieIdIndex);

            mContext.getContentResolver().delete(MoviesContract.FavoriteEntry.CONTENT_URI, MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY + "=?", new String[]{Long.toString(returnMovieId)});
        }
        else
        {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY, movieId);
            movieValues.put(MoviesContract.FavoriteEntry.COLUMN_DATE_ADDED, System.currentTimeMillis());

            Uri insertedUri = mContext.getContentResolver().insert(MoviesContract.FavoriteEntry.CONTENT_URI, movieValues);

            returnMovieId = (int) ContentUris.parseId(insertedUri);
        }

        favoriteCursor.close();

        return returnMovieId;
    }

    public void fetch(final MoviesFragment.Discover type)
    {
        MovieDbApi movieApi = RestClient.getInstance();

        Progressor.show(mContext.getResources().getString(R.string.fetchMovies), mContext.getResources().getString(R.string.progress_loading) + "...", mContext);

        movieApi.getMovies(type + ".desc", new Callback<MovieDiscover>()
        {
            @Override
            public void success(MovieDiscover dMovies, Response response)
            {
                List<Movie> movies = dMovies.getMovies();
                Vector<ContentValues> cMovieVector = new Vector<ContentValues>(movies.size());
                for (int i = 0; i < movies.size(); ++i)
                {
                    Movie movie = movies.get(i);

                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MoviesContract.MovieEntry._ID, movie.getId());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_LANGUAGE, movie.getLanguage());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getPoster());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseTimeStamp());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_TYPE, type.toString());
                    movieValues.put(MoviesContract.MovieEntry.COLUMN_POSITION, i);

                    cMovieVector.add(movieValues);

                }

                int inserted = 0;

                if (cMovieVector.size() > 0)
                {
                    ContentValues[] cvArray = new ContentValues[cMovieVector.size()];
                    cMovieVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);
                }
                Progressor.dismiss();

            }

            @Override
            public void failure(RetrofitError error)
            {
                Progressor.dismiss();
            }

        });

        //  movieApi.getMovies(so);

    }

    public void fetchReviews(int movieId, final ReviewAdapter adapter)
    {
        MovieDbApi movieApi = RestClient.getInstance();

        Progressor.show(mContext.getResources().getString(R.string.fetchReviews), mContext.getResources().getString(R.string.progress_loading) + "...", mContext);
        movieApi.getMovieReviews(movieId, new Callback<ReviewResponse>()
        {
            @Override
            public void success(ReviewResponse reviews, Response response)
            {
                adapter.setReviewList(reviews.getReviews());
                Progressor.dismiss();
            }

            @Override
            public void failure(RetrofitError error)
            {
                Progressor.dismiss();
            }

        });

    }

    public void fetchTrailers(int movieId, final TrailerAdapter adapter)
    {
        MovieDbApi movieApi = RestClient.getInstance();

        Progressor.show(mContext.getResources().getString(R.string.fetchTrailers), mContext.getResources().getString(R.string.progress_loading) + "...", mContext);

        movieApi.getMovieTrailers(movieId, new Callback<TrailerResponse>()
        {
            @Override
            public void success(TrailerResponse trailerResponse, Response response)
            {
                adapter.setTrailerList(trailerResponse.getYoutubeTrailers());
                Progressor.dismiss();
            }

            @Override
            public void failure(RetrofitError error)
            {
                Progressor.dismiss();
            }
        });
    }

}
