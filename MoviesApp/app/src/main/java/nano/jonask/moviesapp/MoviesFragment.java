package nano.jonask.moviesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import nano.jonask.moviesapp.RestApi.MovieDbApi;
import nano.jonask.moviesapp.RestApi.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas Kirkemyr on 15.07.2015.
 */
public class MoviesFragment extends Fragment {

    private MoviePosterAdapter movieAdapter;
    private ProgressDialog progress;

    private Context context = null;
    private Discover currentDiscover = null;
    private boolean isAsc = false;//not used for now. can be used at a later stage however :)


    public MoviesFragment() {
        progress = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        this.context = this.getActivity();

        movieAdapter = new MoviePosterAdapter(this.context);

        if (savedInstanceState != null) {
            movieAdapter.setMovieList(savedInstanceState.getParcelableArrayList("movies"));
        } else
            updateMovies(Discover.POPULAR);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

        dismissProgresser();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieAdapter.getMovies());
        super.onSaveInstanceState(outState);
    }


    /**
     * check if internet connection is enabled
     *
     * @return boolean
     */
    private boolean checkHasInternet() {
        InternetConnection connection = new InternetConnection();

        return connection.hasInternet();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies, menu);
    }


    /**
     * Update movie list to show
     * Starts a progressdialog when starting to fetch new movies
     *
     * @param discover
     */
    private void updateMovies(Discover discover) {

        initWaitInternetProgress();
        if (!checkHasInternet()) {//if no internet connection..
            dismissProgresser();//.. cannot retrieve movies
            return;//.. therefore, return from method
        }

        dismissProgresser();

        if (discover == Discover.POPULAR) {//fetch popular movies
            getActivity().setTitle("Most Popular");

        } else if (discover == Discover.VOTE) {//fetch high rated movies
            getActivity().setTitle("Highest Rated");
        }

        new FetchMovies().execute(discover);//fetch movies according to chosen type
    }

    private void initProgresser(String msg) {
        if (progress == null) {
            progress = new ProgressDialog(context);
            progress.setTitle("Loading");

        }
        progress.setMessage(msg);
        progress.show();
    }

    private void initLoadDataProgress() {
        initProgresser("Wait while fetching data..");
    }

    private void initWaitInternetProgress() {
        initProgresser("Wait while checking internet connection..");
    }

    private void dismissProgresser() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            updateMovies(Discover.POPULAR);
            return true;
        } else if (id == R.id.action_rate) {
            updateMovies(Discover.VOTE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_posters, group, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movieGridView);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//on item click..
                Intent detail = new Intent(parent.getContext(), MainDetail.class);//.. get new intent to launch
                detail.putExtra("movie", (Movie) movieAdapter.getItem(position));
                startActivity(detail);//.. and start the new activity
            }
        });

        return rootView;
    }


    public class FetchMovies extends AsyncTask<Discover, Void, Integer> {
        @Override
        protected void onPreExecute() {
            initLoadDataProgress();
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (status == -1)
                dismissProgresser();
        }

        @Override
        protected Integer doInBackground(Discover... params) {

            if (params.length == 0 || currentDiscover == params[0])//executing fetching of movies requires input to choose which type of movies to return..
                return -1;//.. return if no input params given

            String sortBy = (isAsc) ? ".asc" : ".desc";//whether to sort ascending or descending
            String sorter = params[0].toString() + sortBy;

            MovieDbApi movieApi = RestClient.getInstance();
            currentDiscover = params[0];

            movieApi.getMovies(sorter, new Callback<List<Movie>>() {
                @Override
                public void success(List<Movie> movies, Response response) {
                    for (int i = 0; i < movies.size(); ++i)//set poster url to retrieve poster from
                        movies.get(i).setPoster(DataSettings.POSTER_BASE_URL + PosterSize.W500 + movies.get(i).getPoster());
                    movieAdapter.setMovieList((ArrayList) movies);

                    dismissProgresser();
                }

                @Override
                public void failure(RetrofitError error) {
                    dismissProgresser();
                }

            });


            return 0;
        }

    }


    public class InternetConnection {
        public boolean hasInternet() {
            try {
                NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
            } catch (NullPointerException e) {
            }
            return false;
        }
    }


    private enum Discover {
        POPULAR {
            @Override
            public String toString() {
                return "popularity";
            }
        },
        VOTE {
            @Override
            public String toString() {
                return "vote_average";
            }
        }
    }


}
