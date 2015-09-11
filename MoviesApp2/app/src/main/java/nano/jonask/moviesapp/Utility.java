package nano.jonask.moviesapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Jonas on 28.08.2015.
 */
public class Utility
{

    public static PosterSize posterSize;

    public static boolean hasInternetConnection(Context context)
    {
        try
        {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
        }
        catch (NullPointerException e)
        {
        }
        return false;
    }

    /**
     * Set listview height, depending on how many items are present
     *
     * @param list
     *
     * @return
     *
     * @src http://blog.lovelyhq.com/setting-listview-height-depending-on-the-items/
     */
    public static boolean setListViewHeight(ListView list)
    {
        if (list == null)
            return false;
        ListAdapter adapter = list.getAdapter();

        if (adapter != null)
        {
            int itemsCount = adapter.getCount();

            int totalHeight = 0;

            for (int i = 0; i < itemsCount; ++i)
            {
                View view = adapter.getView(i, null, list);
                view.measure(0, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalHeight += view.getMeasuredHeight();
            }

            totalHeight += (list.getDividerHeight() * (itemsCount - 1));
            ViewGroup.LayoutParams params = list.getLayoutParams();

            params.height = totalHeight;
            list.setLayoutParams(params);
            list.requestLayout();

            return true;
        }
        return false;
    }

    public static void setPosterSize(boolean twoPane)
    {
        if (twoPane)//take too much memory to load high res pictures
            posterSize = PosterSize.W154;
        else
            posterSize = PosterSize.W500;
    }

}
