package nano.jonask.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonas on 26.08.2015.
 */
public class MoviesDbHelper extends SQLiteOpenHelper
{
    public static final String DATABSE_NAME = "moviesapp.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context)
    {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteEntry.TABLE_NAME);
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + "(" +
                MoviesContract.MovieEntry._ID + " INTEGER NOT NULL PRIMARY KEY," +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL DEFAULT ''," +
                MoviesContract.MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL DEFAULT ''," +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT," +
                MoviesContract.MovieEntry.COLUMN_POSTER_IMAGE + " BLOB," +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL," +//timestamp
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL DEFAULT 0," +
                MoviesContract.MovieEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_POSITION + " INTEGER NOT NULL" +
                ");";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MoviesContract.FavoriteEntry.TABLE_NAME + "(" +
                MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL PRIMARY KEY," +
                MoviesContract.FavoriteEntry.COLUMN_DATE_ADDED + " INTEGER NOT NULL," +//timestamp

                "FOREIGN KEY(" + MoviesContract.FavoriteEntry.COLUMN_MOVIE_KEY + ") REFERENCES " + MoviesContract.MovieEntry.TABLE_NAME + "(" + MoviesContract.MovieEntry._ID + "));";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteEntry.TABLE_NAME);

        onCreate(db);
    }
}
