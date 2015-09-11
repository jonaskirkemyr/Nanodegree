package nano.jonask.moviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback
{

    private static final String DETAILTFRAGMENT_TAG = "DFTAG";
    private static boolean initPicasso = false;
    private boolean mTwoPane;

    //Uncomment to see details about image loading in Picasso
    //    private void initPicassoBuilder()
    //    {
    //        Picasso.Builder builder = new Picasso.Builder(this);
    //        Picasso built = builder.build();
    //        built.setIndicatorsEnabled(true);
    //        Picasso.setSingletonInstance(built);
    //    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar as defined in layout xml file
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
        {
            toolbar.setLogo(R.drawable.ic_logo);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (findViewById(R.id.movie_detail_container) != null)
        {
            mTwoPane = true;
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, new DetailFragment(), DETAILTFRAGMENT_TAG).commit();
        }
        else
        {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        Utility.setPosterSize(mTwoPane);


        //        if (!initPicasso)
        //        {
        //            initPicassoBuilder();
        //            initPicasso = true;
        //        }

    }

    @Override
    public void onItemSelected(Uri dataUri)
    {
        if (mTwoPane)
        {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dataUri);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment, DETAILTFRAGMENT_TAG).commit();
        }
        else
        {
            Intent intent = new Intent(this, DetailActivity.class).setData(dataUri);
            startActivity(intent);
        }
    }
}
