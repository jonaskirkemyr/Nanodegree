package nano.jonask.moviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Jonas on 26.08.2015.
 */
public class MovieProvider extends ContentProvider
{
    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int MOVIE_WITH_TYPE = 102;
    public static final int FAVORITE = 200;
    public static final int FAVORITE_WITH_ID = 201;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sFavoriteByMovieQueryBuilder;
    private static final String sMovieSettingId = MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID + "=?";
    private static final String sFavoriteSettingId = MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY + "=?";
    private static final String sMovieSettingType = MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_TYPE + "=?";

    static
    {
        sFavoriteByMovieQueryBuilder = new SQLiteQueryBuilder();

        sFavoriteByMovieQueryBuilder.setTables(MoviesContract.FavoriteEntry.TABLE_NAME + " INNER JOIN " +
                MoviesContract.MovieEntry.TABLE_NAME + " ON " +
                MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY + "=" +
                MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID);
    }

    private MoviesDbHelper mHelper;

    public static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/get/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIE_WITH_TYPE);

        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, FAVORITE);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate()
    {
        mHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor cursor;

        switch (sUriMatcher.match(uri))
        {
            case MOVIE:
                cursor = mHelper.getReadableDatabase().query(MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                cursor = getMovieById(uri, projection, sortOrder);
                break;
            case MOVIE_WITH_TYPE:
                cursor = getMovieByType(uri, projection, sortOrder);
                break;

            case FAVORITE:
                cursor = sFavoriteByMovieQueryBuilder.query(mHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_WITH_ID:
                cursor = getFavoriteById(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown url: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_TYPE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;

            case FAVORITE:
                return MoviesContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MoviesContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case MOVIE:
            {
                long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);

                if (id > 0)
                    returnUri = MoviesContract.MovieEntry.buildMovieUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case FAVORITE:
            {
                long id = db.insert(MoviesContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = MoviesContract.FavoriteEntry.buildMovieUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);//notify of change
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match)
        {
            case MOVIE:
                db.beginTransaction();

                try
                {
                    for (ContentValues value : values)
                    {
                        long id = db.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
                break;

            case FAVORITE:
                db.beginTransaction();

                try
                {
                    for (ContentValues value : values)
                    {
                        long id = db.insert(MoviesContract.FavoriteEntry.TABLE_NAME, null, value);
                        if (id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null)//delete all if no selection is made
            selection = "1";

        switch (match)
        {
            case MOVIE:
                rowsDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case FAVORITE:
                rowsDeleted = db.delete(MoviesContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)//notify of change
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match)
        {
            case MOVIE:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case FAVORITE:
                rowsUpdated = db.update(MoviesContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0)//notify of change
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    //cursors
    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder)
    {
        int id = MoviesContract.MovieEntry.getIdFromUri(uri);

        String[] selectionArgs = null;
        String selection = null;

        if (id != 0)
        {
            selection = sMovieSettingId;
            selectionArgs = new String[]{Integer.toString(id)};
        }

        return mHelper.getReadableDatabase().query(MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getMovieByType(Uri uri, String[] projection, String sortOrder)
    {
        String type = MoviesContract.MovieEntry.getTypeFromUri(uri);

        String[] selectionArgs = null;
        String selection = null;

        if (type != null)
        {
            selection = sMovieSettingType;
            selectionArgs = new String[]{type};
        }

        return mHelper.getReadableDatabase().query(MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getFavoriteById(Uri uri, String[] projection, String sortOrder)
    {
        int id = MoviesContract.FavoriteEntry.getIdFromUri(uri);
        String[] selectionArgs = null;
        String selection = null;

        if (id != 0)
        {
            selection = sFavoriteSettingId;
            selectionArgs = new String[]{Integer.toString(id)};
        }

        return sFavoriteByMovieQueryBuilder.query(mHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }


}
