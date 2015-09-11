package nano.jonask.moviesapp.RestApi;

import nano.jonask.moviesapp.DataSettings;
import retrofit.RequestInterceptor;

/**
 * Created by Jonas Kirkemyr on 20.07.2015.
 */
public class SessionRequestInterceptor implements RequestInterceptor
{
    @Override
    public void intercept(RequestFacade request)
    {
        request.addQueryParam("api_key", DataSettings.API_KEY);
    }
}
