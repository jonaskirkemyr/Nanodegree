package nano.jonask.moviesapp.RestApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import nano.jonask.moviesapp.DataSettings;
import nano.jonask.moviesapp.Movie;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Jonas Kirkemyr on 20.07.2015.
 */
public class RestClient {

    private static MovieDbApi REST_CLIENT;

    public static MovieDbApi getInstance()
    {
       // if(REST_CLIENT==null)
            setupRestClient();
        return REST_CLIENT;
    }

    protected RestClient(){}


    private static void setupRestClient() {

        Gson gson=new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory("results"))
               // .registerTypeAdapter(Movie.class, new Deserializer<Movie>())
                .setDateFormat("yyyy-MM-dd")
                .create();


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(DataSettings.MOVIE_DB_BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(new OkHttpClient()))
                .setRequestInterceptor(new SessionRequestInterceptor())
                .build();

        REST_CLIENT = restAdapter.create(MovieDbApi.class);

    }
}
