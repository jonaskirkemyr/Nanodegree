package nano.jonask.moviesapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by Jonas Kirkemyr on 18.07.2015.
 */
public class MovieDetail extends Fragment
{
    private ShareActionProvider shareActionProvider;
    private Movie movie;

    public MovieDetail(){}


    private void initDetails(View view)
    {
        ((TextView)view.findViewById(R.id.title)).setText(movie.getTitle());
        ((TextView)view.findViewById(R.id.releaseYear)).setText(movie.getReleaseDate("yyyy-MM-dd"));
        //((TextView)view.findViewById(R.id.duration)).setText(movie.getTitle());
        ((TextView)view.findViewById(R.id.duration)).setText("");
        ((TextView)view.findViewById(R.id.rating)).setText(movie.getVoteAverage());
        Picasso.with(getActivity()).load(movie.getPoster()).into((ImageView)view.findViewById(R.id.poster));

        ((TextView)view.findViewById(R.id.description)).setText(movie.getOverview());
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

            ActionBar bar=null;
            if((bar=getActivity().getActionBar())!=null)
                bar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getActivity().getIntent();
        movie=(Movie)intent.getExtras().getParcelable("movie");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.movie_details, container, false);
        initDetails(rootView);


        return rootView;
    }



}
