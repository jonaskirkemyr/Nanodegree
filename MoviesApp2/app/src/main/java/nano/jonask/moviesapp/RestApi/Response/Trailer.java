package nano.jonask.moviesapp.RestApi.Response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jonas on 29.08.2015.
 */
public class Trailer implements Parcelable
{
    public static final String youtubeLink = "https://www.youtube.com/watch?v=";
    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>()
    {

        @Override
        public Trailer createFromParcel(Parcel source)
        {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size)
        {
            return new Trailer[size];
        }
    };
    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private String quality;
    @SerializedName("source")
    private String source;
    @SerializedName("type")
    private String type;

    public Trailer(Parcel in)
    {
        name = in.readString();
        quality = in.readString();
        source = in.readString();
        type = in.readString();
    }

    public String getName()
    {
        return name;
    }

    public String getQuality()
    {
        return quality;
    }

    public String getSource()
    {
        return source;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(quality);
        dest.writeString(source);
        dest.writeString(type);
    }
}
