package nano.jonask.moviesapp;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Jonas on 28.08.2015.
 */
public class Progressor
{
    private static ProgressDialog mProgress;

    public Progressor()
    {
        mProgress = null;
    }

    public static void show(String msg, String title, Context context)
    {
        if (mProgress == null || !mProgress.isShowing())
        {
            mProgress = new ProgressDialog(context);
            mProgress.setTitle(title);
        }

        mProgress.setMessage(msg);
        mProgress.show();
    }

    public static void dismiss()
    {
        if (mProgress != null && mProgress.isShowing())
        {
            mProgress.cancel();
            mProgress = null;
        }

    }


}
