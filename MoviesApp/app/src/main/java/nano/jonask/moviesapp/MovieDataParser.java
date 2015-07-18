package nano.jonask.moviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonas Kirkemyr on 16.07.2015.
 */
public class MovieDataParser
{
    private static int PAGE=0;
    private static final String DATA="results";

    public static Movie[] getMoviesFromJson(String movieJsonStr,String picturePath) throws JSONException {

        final String TITLE="original_title";
        final String ID="id";
        final String OVERVIEW="overview";
        final String RELEASE_DATE="release_date";
        final String POSTER_PATH="poster_path";
        final String LANGUAGE="original_language";
        final String VOTE_AVG="vote_average";

        JSONObject moviesJson=new JSONObject(movieJsonStr);
        JSONArray resultArray=moviesJson.getJSONArray(DATA);

        Movie[] result=new Movie[resultArray.length()];
        for(int i=0;i<resultArray.length();++i)
        {
            JSONObject temp=resultArray.getJSONObject(i);

            result[i]=new Movie();
            result[i].setTitle(temp.getString(TITLE));
            result[i].setId(Integer.parseInt(temp.getString(ID)));
            result[i].setOverview(temp.getString(OVERVIEW));
            result[i].setReleaseDate(temp.getString(RELEASE_DATE));
            result[i].setPoster(picturePath+temp.getString(POSTER_PATH));
            result[i].setLanguage(temp.getString(LANGUAGE));
            result[i].setVoteAverage(temp.getString(VOTE_AVG));
        }


        return result;
    }
}
