package nano.jonask.moviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.Picasso;
import nano.jonask.moviesapp.RestApi.Response.Review;
import nano.jonask.moviesapp.RestApi.Response.Trailer;
import nano.jonask.moviesapp.data.MoviesContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jonas on 27.08.2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String DETAIL_URI = "URI";

    private static final String MOVIE_SHARE_TEXT = "Watch this awesome trailer! ";
    private static final String MOVIE_SHARE_HASHTAG = " #MoviesApp";

    private static final String SAVED_TRAILERS = "trailers";
    private static final String SAVED_REVIEWS = "reviews";

    private static final int DETAILS_LOADER = 0;
    private static final int FAVORITE_LOADER = 1;


    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_LANGUAGE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    private static final String[] FAVORITE_COLUMNS = {
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_DATE_ADDED
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_LANGUAGE = 3;
    private static final int COL_MOVIE_POSTER = 4;
    private static final int COL_MOVIE_RELEASE_DATE = 5;
    private static final int COL_MOVIE_VOTE_AVERAGE = 6;

    private static final int COL_MOVIE_FAVORITE_ADDED = 1;


    private ShareActionProvider mShareActionProvider;


    private Uri mUri;//movie uri
    private Uri fUri;//favorite uri

    private TextView vTitle;
    private ImageView vPoster;
    private TextView vReleaseYear;
    private TextView vDuration;
    private TextView vRating;
    private TextView vOverview;

    private Button favorite;

    private ListView trailers;
    private ListView reviews;

    private MenuItem shareMenuItem;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private FetchMovieTask movieTask;


    public DetailFragment()
    {
        movieTask = null;
    }

    /**
     * Sharing url of first trailer
     *
     * @param vCode
     *
     * @return
     */
    private Intent createTrailerShareIntent(String vCode)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, MOVIE_SHARE_TEXT + Trailer.youtubeLink + vCode + MOVIE_SHARE_HASHTAG);
        return intent;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        reviewAdapter = new ReviewAdapter(getActivity());
        reviewAdapter.registerDataSetObserver(new ObserveReviewChange());
        trailerAdapter = new TrailerAdapter(getActivity());
        trailerAdapter.registerDataSetObserver(new ObserveTrailerChange());
        movieTask = new FetchMovieTask(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        vTitle = (TextView) rootView.findViewById(R.id.title);
        vReleaseYear = (TextView) rootView.findViewById(R.id.releaseYear);
        vDuration = (TextView) rootView.findViewById(R.id.duration);
        vRating = (TextView) rootView.findViewById(R.id.rating);
        vOverview = (TextView) rootView.findViewById(R.id.description);
        vPoster = (ImageView) rootView.findViewById(R.id.poster);

        favorite = (Button) rootView.findViewById(R.id.favorite);

        trailers = (ListView) rootView.findViewById(R.id.listViewTrailers);
        reviews = (ListView) rootView.findViewById(R.id.listViewReviews);

        trailers.setAdapter(trailerAdapter);
        reviews.setAdapter(reviewAdapter);


        trailers.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Trailer selectedTrailer = (Trailer) trailerAdapter.getItem(position);

                if (selectedTrailer != null)
                    startActivity(createPlayTrailerIntent(selectedTrailer.getSource()));

            }
        });


        Bundle arguments = getArguments();
        if (arguments != null)//get arguments sent to fragment
        {
            mUri = arguments.getParcelable(DETAIL_URI);
            fUri = MoviesContract.FavoriteEntry.buildMovieUri(MoviesContract.MovieEntry.getIdFromUri(mUri));
        }

        if (savedInstanceState != null)//if trailers and reviews are already retrieved..
        {//..fetch from saved instance
            ArrayList<Trailer> storedTrailers = savedInstanceState.getParcelableArrayList(SAVED_TRAILERS);
            ArrayList<Review> storedReviews = savedInstanceState.getParcelableArrayList(SAVED_REVIEWS);
            trailerAdapter.setTrailerList(storedTrailers);
            reviewAdapter.setReviewList(storedReviews);
        }
        else if (mUri != null)
            fetchMovieDetails();

        return rootView;
    }

    /**
     * Init intent for showing trailer in browser or youtube app if available
     *
     * @param vCode
     *
     * @return
     */
    private Intent createPlayTrailerIntent(String vCode)
    {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(Trailer.youtubeLink + vCode));
    }

    private void fetchMovieDetails()
    {
        if (Utility.hasInternetConnection(getActivity()))
        {
            movieTask.fetchReviews(MoviesContract.MovieEntry.getIdFromUri(mUri), reviewAdapter);
            movieTask.fetchTrailers(MoviesContract.MovieEntry.getIdFromUri(mUri), trailerAdapter);
        }
        else
        {
            Toast toast = Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        //initialize loaders for retrieving movie details, and whether movie is stored as a favorite
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(SAVED_TRAILERS, trailerAdapter.getTrailers());
        outState.putParcelableArrayList(SAVED_REVIEWS, reviewAdapter.getReviews());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_details, menu);

        shareMenuItem = menu.findItem(R.id.action_share);
        shareMenuItem.setVisible(false);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if (mUri != null && id == DETAILS_LOADER)
            return new CursorLoader(getActivity(), mUri, MOVIE_COLUMNS, null, null, null);
        else if (fUri != null && id == FAVORITE_LOADER)
            return new CursorLoader(getActivity(), fUri, FAVORITE_COLUMNS, null, null, null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (data != null && data.moveToFirst())
        {
            if (loader.getId() == DETAILS_LOADER)
            {
                String titleText = data.getString(COL_MOVIE_TITLE);
                vTitle.setText(titleText);


                long releaseDate = data.getLong(COL_MOVIE_RELEASE_DATE);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

                String dateString = formatter.format(new Date(releaseDate));

                vReleaseYear.setText(dateString);
                //String durationText=data.getString(COL_MOVIE_)

                String overviewText = data.getString(COL_MOVIE_OVERVIEW);
                vOverview.setText(overviewText);
                //String languageText = data.getString(COL_MOVIE_LANGUAGE);

                float voteNumb = data.getFloat(COL_MOVIE_VOTE_AVERAGE);
                vRating.setText(Float.toString(voteNumb));

                String posterUrlText = data.getString(COL_MOVIE_POSTER);
                Picasso.with(getActivity()).load(DataSettings.POSTER_BASE_URL + Utility.posterSize + posterUrlText).into(vPoster);

                favorite.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        movieTask.addFavorite(MoviesContract.MovieEntry.getIdFromUri(mUri));

                    }
                });

            }
            else if (loader.getId() == FAVORITE_LOADER)
            {
                favorite.setText(getActivity().getResources().getString(R.string.button_favorite_remove));
            }
        }
        else if (loader.getId() == FAVORITE_LOADER)
            favorite.setText(getActivity().getResources().getString(R.string.button_favorite));

        favorite.setVisibility(View.VISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    private class ObserveTrailerChange extends DataSetObserver
    {
        @Override
        public void onChanged()
        {
            Utility.setListViewHeight(trailers);
            if (mShareActionProvider != null && trailerAdapter != null && trailerAdapter.getCount() > 1)
            {
                shareMenuItem.setVisible(true);
                mShareActionProvider.setShareIntent(createTrailerShareIntent(((Trailer) trailerAdapter.getItem(0)).getSource()));
            }
            else if (shareMenuItem != null)
                shareMenuItem.setVisible(false);

        }
    }

    private class ObserveReviewChange extends DataSetObserver
    {
        @Override
        public void onChanged()
        {
            Utility.setListViewHeight(reviews);
        }
    }
}
