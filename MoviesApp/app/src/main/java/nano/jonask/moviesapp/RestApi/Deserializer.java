package nano.jonask.moviesapp.RestApi;

import android.util.Log;
import com.google.gson.*;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Jonas Kirkemyr on 20.07.2015.
 */
public class Deserializer<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.d("hello","hello");

        return null;
      /*  JsonObject element=json.getAsJsonObject();

        JsonArray resultArray=element.getAsJsonArray("results");
        //.get("results");

        return new Gson().fromJson(resultArray,typeOfT);*/
    }
}
