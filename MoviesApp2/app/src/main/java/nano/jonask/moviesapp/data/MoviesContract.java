package nano.jonask.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jonas on 26.08.2015.
 */
public class MoviesContract
{
    public static final String CONTENT_AUTHORITY = "nano.jonask.moviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVORITES = "favorites";


    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        //table name of db-table
        public static final String TABLE_NAME = "movies";

        //table columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_LANGUAGE = "original_language";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_POSTER_IMAGE = "poster_image";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TYPE = "movie_type";
        public static final String COLUMN_POSITION = "movie_position";

        public static Uri buildMovieUri(long id)
        {
            return CONTENT_URI.buildUpon().appendPath("get").appendPath(Long.toString(id)).build();
        }

        public static Uri buildMovieType(String type)
        {
            return CONTENT_URI.buildUpon().appendPath(type).build();
        }

        /**
         * Retrieves id from given Uri, as defined in buildUriMather
         *
         * @param uri current request
         *
         * @return int
         *
         * @see this.buildUriMatcher
         */
        public static int getIdFromUri(Uri uri)
        {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static String getTypeFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class FavoriteEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        //table name of db-table
        public static final String TABLE_NAME = "favorites";

        //table columns
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_DATE_ADDED = "date_created";
        //public static final String COLUMN_POSTER_IMAGE="poster_image";

        public static Uri buildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getIdFromUri(Uri uri)
        {
            return (int) ContentUris.parseId(uri);
            //return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

}
