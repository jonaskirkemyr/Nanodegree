package nano.jonask.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import nano.jonask.moviesapp.RestApi.Response.Review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jonas on 29.08.2015.
 */
public class ReviewAdapter extends BaseAdapter
{
    private List<Review> reviews;
    private Context mContext;


    public ReviewAdapter(Context context)
    {
        mContext = context;
        reviews = new ArrayList<>();
    }


    public void setReviewList(List<Review> reviews)
    {
        if (this.reviews != null)
            this.reviews.clear();
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviews()
    {
        return (ArrayList<Review>) reviews;
    }    @Override
    public int getCount()
    {
        return reviews.size();
    }

    public static class ListItemViewHolder
    {
        public final TextView authorView;
        public final TextView contentView;

        public ListItemViewHolder(View view)
        {
            authorView = (TextView) view.findViewById(R.id.list_item_review_author);
            contentView = (TextView) view.findViewById(R.id.list_item_review_content);
        }
    }    @Override
    public Object getItem(int position)
    {
        if (position >= 0 && position < getCount())
            return reviews.get(position);
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
        ListItemViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_review, parent, false);
            holder = new ListItemViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
            holder = (ListItemViewHolder) convertView.getTag();

        Review review = reviews.get(position);

        holder.authorView.setText(review.getAuthor());
        holder.contentView.setText(review.getContent());

        return convertView;
    }





}
