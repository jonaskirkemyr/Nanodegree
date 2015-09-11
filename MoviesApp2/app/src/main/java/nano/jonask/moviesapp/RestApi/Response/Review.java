package nano.jonask.moviesapp.RestApi.Response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jonas on 29.08.2015.
 */
public class Review implements Parcelable
{
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>()
    {

        @Override
        public Review createFromParcel(Parcel source)
        {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size)
        {
            return new Review[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("author")
    private String author;
    @SerializedName("content")
    private String content;
    @SerializedName("url")
    private String url;

    public Review(Parcel in)
    {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public String getId()
    {
        return id;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getContent()
    {
        return content;
    }

    public String getUrl()
    {
        return url;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }
}
