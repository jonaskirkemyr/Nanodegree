package nano.jonask.moviesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import nano.jonask.moviesapp.data.MoviesContract;


/**
 * Created by Jonas Kirkemyr on 15.07.2015.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_POSTER = 1;
    public static final int COL_MOVIE_POSTER_IMAGE = 2;
    private static final int MOVIE_LOADER = 0;
    //specify which columns are needed
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_POSTER_IMAGE
    };
    private Discover mSelectedMovieSort = Discover.POPULAR;
    private MoviePosterAdapter mMovieAdapter;

    private Toast toast;

    private int currentSelectedMovie=-1;

    private static final String STORE_CURRENT_POSITION="curpos";

    public MoviesFragment()
    {

    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        updateTitle();

        if(savedInstanceState!=null)
            currentSelectedMovie=savedInstanceState.getInt(STORE_CURRENT_POSITION);
    }

    private void updateTitle()
    {
        if (mSelectedMovieSort == Discover.FAVORITE)
            getActivity().setTitle(R.string.title_favorites);
        else if (mSelectedMovieSort == Discover.VOTE)
            getActivity().setTitle(R.string.title_rate);
        else
            getActivity().setTitle(R.string.title_popular);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
    {
        mMovieAdapter = new MoviePosterAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.movie_posters, group, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movieGridView);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {//on item click..
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null && currentSelectedMovie != position)
                {
                    currentSelectedMovie = position;
                    int movieId = cursor.getInt(COL_MOVIE_ID);
                    ((Callback) getActivity()).onItemSelected(MoviesContract.MovieEntry.buildMovieUri(movieId));

                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(STORE_CURRENT_POSITION,currentSelectedMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        super.onStop();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_movies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_sort)
        {

            if (mSelectedMovieSort != Discover.POPULAR)
            {
                mSelectedMovieSort = Discover.POPULAR;
                updateTitle();
                restartLoader();
            }
            return true;
        }
        else if (id == R.id.action_rate)
        {
            if (mSelectedMovieSort != Discover.VOTE)
            {
                mSelectedMovieSort = Discover.VOTE;
                updateTitle();
                restartLoader();
            }
            return true;
        }
        else if (id == R.id.action_favorite)
        {
            if (mSelectedMovieSort != Discover.FAVORITE)
            {
                mSelectedMovieSort = Discover.FAVORITE;
                updateTitle();
                restartLoader();
            }
            return true;
        }
        else if (id == R.id.action_refresh && mSelectedMovieSort != Discover.FAVORITE)
        {
            updateMovies();
            updateTitle();
            restartLoader();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void restartLoader()
    {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    /**
     * Fetches movies from web api
     */
    private void updateMovies()
    {
        if (Utility.hasInternetConnection(getActivity()))
        {
            FetchMovieTask movieTask = new FetchMovieTask(getActivity());
            movieTask.fetch(mSelectedMovieSort);
        }
        else
        {
            if (toast != null && toast.getView().isShown())
                toast.cancel();
            toast = Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String sortOrder = null;
        Uri MovieTypeUri = null;

        if (mSelectedMovieSort == Discover.POPULAR)
        {
            MovieTypeUri = MoviesContract.MovieEntry.buildMovieType(mSelectedMovieSort.toString());
            sortOrder = MoviesContract.MovieEntry.COLUMN_POSITION + " ASC";
        }
        else if (mSelectedMovieSort == Discover.VOTE)
        {
            MovieTypeUri = MoviesContract.MovieEntry.buildMovieType(mSelectedMovieSort.toString());
            sortOrder = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        else if (mSelectedMovieSort == Discover.FAVORITE)
        {
            MovieTypeUri = MoviesContract.FavoriteEntry.CONTENT_URI;
            sortOrder = MoviesContract.FavoriteEntry.COLUMN_DATE_ADDED + " DESC";
        }

        return new CursorLoader(getActivity(), MovieTypeUri, MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        mMovieAdapter.swapCursor(data);

        if (data.getCount() == 0)//fetch movies if no data returned from db
            updateMovies();


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mMovieAdapter.swapCursor(null);
    }

    public enum Discover
    {
        POPULAR
                {
                    @Override
                    public String toString()
                    {
                        return "popularity";
                    }
                },
        VOTE
                {
                    @Override
                    public String toString()
                    {
                        return "vote_average";
                    }
                },
        FAVORITE
                {
                    @Override
                    public String toString()
                    {
                        return "favorite";
                    }
                }
    }


    public interface Callback
    {
        public void onItemSelected(Uri dataUri);
    }


}
