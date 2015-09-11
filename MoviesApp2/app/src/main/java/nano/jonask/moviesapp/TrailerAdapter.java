package nano.jonask.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import nano.jonask.moviesapp.RestApi.Response.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 30.08.2015.
 */
public class TrailerAdapter extends BaseAdapter
{
    private List<Trailer> trailers;
    private Context mContext;


    public TrailerAdapter(Context context)
    {
        mContext = context;
        trailers = new ArrayList<Trailer>();
    }

    public void setTrailerList(List<Trailer> trailers)
    {
        if (this.trailers != null)
            this.trailers.clear();
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getTrailers()
    {
        return (ArrayList<Trailer>) trailers;
    }    @Override
    public int getCount()
    {
        return trailers.size();
    }

    public static class ListItemViewHolder
    {
        public final TextView trailerText;

        public ListItemViewHolder(View view)
        {
            trailerText = (TextView) view.findViewById(R.id.list_item_trailer_textview);
        }
    }    @Override
    public Object getItem(int position)
    {
        if (position >= 0 && position < getCount())
            return trailers.get(position);
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rootView;

        if (convertView == null)
        {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer, parent, false);
            ListItemViewHolder viewHolder = new ListItemViewHolder(rootView);
            rootView.setTag(viewHolder);
        }
        else
            rootView = convertView;

        Trailer trailer = trailers.get(position);

        ListItemViewHolder tempHolder = (ListItemViewHolder) rootView.getTag();

        tempHolder.trailerText.setText(trailer.getName() + " - " + trailer.getType() + " (" + trailer.getQuality() + ")");

        return rootView;
    }







}
