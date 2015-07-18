package nano.jonask.moviesapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Jonas Kirkemyr on 18.07.2015.
 */
public class MainDetail extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfragment);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetail())
                    .commit();
        }
    }
}
