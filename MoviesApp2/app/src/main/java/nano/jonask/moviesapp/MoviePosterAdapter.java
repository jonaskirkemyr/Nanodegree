package nano.jonask.moviesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Jonas on 26.08.2015.
 */
public class MoviePosterAdapter extends CursorAdapter
{

    public MoviePosterAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }


    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        final int id = cursor.getInt(MoviesFragment.COL_MOVIE_ID);
        final ImageView imgView = (ImageView) view;

        byte[] image = cursor.getBlob(MoviesFragment.COL_MOVIE_POSTER_IMAGE);

        if (image != null)
        {
            Bitmap imagePoster = BitmapFactory.decodeByteArray(image, 0, image.length);
            ((ImageView) view).setImageBitmap(imagePoster);
        }
        else
        {
            String url = DataSettings.POSTER_BASE_URL + Utility.posterSize + cursor.getString(MoviesFragment.COL_MOVIE_POSTER);

            // src: https://github.com/square/picasso/issues/609
            try
            {
                final ContentResolver contentResolver = context.getContentResolver();
                Picasso.with(context).load(url).into(imgView);
            }
            catch (IllegalArgumentException e)
            {
                imgView.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_delete));
            }
        }

    }
}
