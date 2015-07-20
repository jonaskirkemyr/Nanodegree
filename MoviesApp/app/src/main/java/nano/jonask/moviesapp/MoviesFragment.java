package nano.jonask.moviesapp;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.squareup.okhttp.OkHttpClient;
import nano.jonask.moviesapp.RestApi.DiscoverResult;
import nano.jonask.moviesapp.RestApi.MovieDbApi;
import nano.jonask.moviesapp.RestApi.RestClient;
import nano.jonask.moviesapp.RestApi.SessionRequestInterceptor;
import org.json.JSONException;
import retrofit.RestAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Jonas Kirkemyr on 15.07.2015.
 */
public class MoviesFragment extends Fragment {

    private MoviePosterAdapter movieAdapter;
    private ProgressDialog progress;

    private Context context = null;

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
            updateMovies(true);
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
     * @param isPopular
     */
    private void updateMovies(boolean isPopular) {

        initWaitInternetProgress();
        if (!checkHasInternet()) {//if no internet connection..
            dismissProgresser();//.. cannot retrieve movies
            return;//.. therefore, return from method
        }

        dismissProgresser();

        String type = null;
        if (isPopular) {//fetch popular movies
            getActivity().setTitle("Most Popular");
            type = "popular";
        } else {//fetch high rated movies
            getActivity().setTitle("Highest Rated");
            type = "rate";
        }

        new FetchMovies().execute(type);//fetch movies according to chosen type
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

            updateMovies(true);
            return true;
        } else if (id == R.id.action_rate) {
            updateMovies(false);
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


    public class FetchMovies extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            initLoadDataProgress();
        }

        @Override
        protected Void doInBackground(String... params) {

            if (params.length == 0)//executing fetching of movies requires input to choose which type of movies to return..
                return null;//.. return if no input params given






            HttpURLConnection httpConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;


            final String DISCOVER = "discover";
            final String MOVIE = "movie";

            final String SORT_PARAM = "sort_by";
            final String SORT_BY_POPULARITY = "popularity.desc";
            final String SORT_BY_RATING = "vote_average.desc";


            final String API_PARAM = "api_key";
            final String API_KEY = DataSettings.API_KEY;//insert api key here

            String sorter = (params[0].equals("rate")) ? SORT_BY_RATING : SORT_BY_POPULARITY;
            //String sorter=SORT_BY_RATING;

            MovieDbApi movieApi= RestClient.getInstance();

            Movie[] result=null;

            movieApi.getMovies(sorter,new Callback<List<Movie>>(){
                @Override
                public void success(List<Movie> movies, Response response) {
                        Log.d("json", "Success");

                    for(int i=0;i<movies.size();++i)
                        movies.get(i).setPoster(DataSettings.POSTER_BASE_URL + PosterSize.W500 + movies.get(i).getPoster());
                    movieAdapter.setMovieList((ArrayList)movies);

                    dismissProgresser();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("json","Failure");
                    dismissProgresser();
                }

            });



/*
            try {
                Uri urlBuilder = Uri.parse(DataSettings.POSTER_BASE_URL).buildUpon()
                        .appendPath(DISCOVER)
                        .appendPath(MOVIE)
                        .appendQueryParameter(SORT_PARAM, sorter)
                        .appendQueryParameter(API_PARAM, API_KEY)
                        .build();

                URL url = new URL(urlBuilder.toString());

                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                InputStream inputStream = httpConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null)//if nothing returned...
                    return null;//.. nothing to do

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = null;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");//for easier debugging, append new line

                if (buffer.length() == 0)//check if anything in stream before parsing
                    return null;

                movieJsonStr = buffer.toString();


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (httpConnection != null)
                    httpConnection.disconnect();//close connection before termination
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            try {
                return MovieDataParser.getMoviesFromJson(movieJsonStr, DataSettings.POSTER_BASE_URL + PosterSize.W500);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

          //  return null;

            return null;
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


}
