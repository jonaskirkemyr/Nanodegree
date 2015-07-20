package nano.jonask.moviesapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)//if icon button in title bar clicked..
        {
            NavUtils.navigateUpFromSameTask(this);//.. go back to previous activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
