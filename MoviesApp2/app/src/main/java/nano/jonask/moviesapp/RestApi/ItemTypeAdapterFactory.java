package nano.jonask.moviesapp.RestApi;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Jonas Kirkemyr on 20.07.2015. <p/> Source http://blog.robinchutaux.com/blog/a-smart-way-to-use-retrofit/
 * #Decapsulate JSON webservices
 */
public class ItemTypeAdapterFactory implements TypeAdapterFactory
{

    private String elementName;

    public ItemTypeAdapterFactory(String elementName)
    {
        this.elementName = elementName;
    }


    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
    {

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>()
        {

            @Override
            public void write(JsonWriter out, T value) throws IOException
            {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException
            {
                JsonElement element = elementAdapter.read(in);

                if (element.isJsonObject())
                {
                    JsonObject obj = element.getAsJsonObject();

                    if (obj.has(elementName) && obj.get(elementName).isJsonArray())
                    {
                        element = obj.get(elementName);
                    }
                }

                if (element.isJsonPrimitive() && element.toString().equals("\"\""))
                { //error is thrown if string only consists of the string ("")
                    return null;
                }

                return delegate.fromJsonTree(element);
            }
        }.nullSafe();

    }
}
