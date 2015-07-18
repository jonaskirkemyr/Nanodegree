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
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jonas Kirkemyr on 15.07.2015.
 */
public class MoviesFragment extends Fragment
{

    private SharedPreferences preferences;
    private Toast toast;
    private MoviePosterAdapter movieAdapter;
    private ProgressDialog progress;

    private Context context=null;

    public MoviesFragment()
    {
        progress=null;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateMovies(true);
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.context=this.getActivity();
    }

    @Override
    public void onResume()
    {
        super.onResume();
       // dismissProgresser();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        dismissProgresser();
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.movies, menu);
    }


    private void updateMovies(boolean isPopular)
    {
        InternetConnection connection=new InternetConnection();
        initWaitInternetProgress();
        if(!connection.hasInternet())
        {
            dismissProgresser();
            return;
        }

        dismissProgresser();

        if(isPopular) {
            getActivity().setTitle("Most Popular");
            new FetchMovies().execute("popular");
        }
        else {
            getActivity().setTitle("Highest Rated");
            new FetchMovies().execute("rate");
        }
    }

    private void initProgresser(String msg)
    {
        if(progress==null)
        {
            progress = new ProgressDialog(context);
            progress.setTitle("Loading");

        }
        progress.setMessage(msg);
        progress.show();
    }

    private void initLoadDataProgress()
    {
        initProgresser("Wait while fetching data..");
    }

    private void initWaitInternetProgress()
    {
        initProgresser("Wait while checking internet connection..");
    }

    private void dismissProgresser()
    {
        if(progress!=null && progress.isShowing())
            progress.dismiss();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_sort)
        {

            updateMovies(true);
            return true;
        }
        else if(id==R.id.action_rate)
        {
            updateMovies(false);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group,Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.movie_posters,group,false);

        movieAdapter=new MoviePosterAdapter(getActivity());

        GridView gridView=(GridView) rootView.findViewById(R.id.movieGridView);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent detail=new Intent(parent.getContext(),MainDetail.class);
                detail.putExtra("movie", (Movie) movieAdapter.getItem(position));
                startActivity(detail);
            }
        });


        return rootView;
    }



    public class FetchMovies extends AsyncTask<String,Void,Movie[]>
    {
         @Override
        protected void onPreExecute()
        {
            initLoadDataProgress();
        }

        @Override
        protected Movie[] doInBackground(String... params) {


            if(params.length==0)
                return null;


            HttpURLConnection httpConnection=null;
            BufferedReader reader=null;

            String movieJsonStr=null;


            final String MOVIE_DB_BASE_URL="http://api.themoviedb.org/3/";
            final String POSTER_BASE_URL="http://image.tmdb.org/t/p/";

            final String DISCOVER="discover";
            final String MOVIE="movie";

            final String SORT_PARAM="sort_by";
            final String SORT_BY_POPULARITY="popularity.desc";
            final String SORT_BY_RATING="vote_average.desc";



            final String API_PARAM="api_key";
            final String API_KEY="18cf56a5776a773964835a3e41972c08";


            String sorter=(params[0].equals("rate"))?SORT_BY_RATING:SORT_BY_POPULARITY;


            try
            {
                Uri urlBuilder=Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(DISCOVER)
                        .appendPath(MOVIE)
                        .appendQueryParameter(SORT_PARAM, sorter)
                        .appendQueryParameter(API_PARAM,API_KEY)
                        .build();

                URL url=new URL(urlBuilder.toString());

                httpConnection=(HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                InputStream inputStream=httpConnection.getInputStream();
                StringBuffer buffer=new StringBuffer();

                if(inputStream==null)//if nothing returned...
                    return null;//.. nothing to do

                reader=new BufferedReader(new InputStreamReader(inputStream));

                String line=null;
                while((line=reader.readLine())!=null)
                    buffer.append(line+"\n");//for easier debugging, append new line

                if(buffer.length()==0)//check if anything in stream before parsing
                    return null;

                movieJsonStr=buffer.toString();


            }catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            finally{
                if(httpConnection!=null)
                    httpConnection.disconnect();//close connection before termination
                if(reader!=null)
                {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            try{
                return MovieDataParser.getMoviesFromJson(movieJsonStr,POSTER_BASE_URL+PosterSize.W500);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies)
        {
            if(movies!=null)
            {
                movieAdapter.setMovieList(movies);

                dismissProgresser();
            }
        }
    }



    public class InternetConnection
    {
        public boolean hasInternet()
        {
            try
            {
                NetworkInfo networkInfo=((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                return (networkInfo!=null && networkInfo.isAvailable() && networkInfo.isConnected());
            }
            catch(NullPointerException e)
            {}
            return false;
        }
    }




}
