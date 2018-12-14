package com.example.jiajiehuang.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    public  List<JSONObject> mDataSet;
    private String kind;
    private Context mContext;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView authorNameView;
        public final TextView timeView;
        public final RatingBar ratingBar;
        public final TextView reviewView;
        public final ImageView imageView;
        public View view;
        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            authorNameView = (TextView) v.findViewById(R.id.author_name);
            timeView= (TextView) v.findViewById(R.id.time);
            ratingBar=(RatingBar) v.findViewById(R.id.rating_bar);
            reviewView= (TextView) v.findViewById(R.id.review_text);
            imageView=(ImageView) v.findViewById(R.id.profile);
            view=v;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public ReviewAdapter(JSONArray dataSet,String kind,Context context) {
        mDataSet=new ArrayList<JSONObject>();
        mContext=context;
        this.kind=kind;
        for (int i=0;i<dataSet.length();i++)
        {
            try {
                mDataSet.add(dataSet.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_row, viewGroup, false);
        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        try {
            if (kind.equals("google"))
            {
                Timestamp timeStamp=new Timestamp(1000*(long)mDataSet.get(position).getInt("time"));
                Log.v(""+position,""+mDataSet.get(position).getInt("time"));
                String timeString=timeStamp.toString().split("\\.")[0];
                viewHolder.authorNameView.setText(mDataSet.get(position).getString("author_name"));
                viewHolder.reviewView.setText(mDataSet.get(position).getString("text"));
                viewHolder.timeView.setText(timeString);
                Picasso.get().load(mDataSet.get(position).getString("profile_photo_url")).into(viewHolder.imageView);
                viewHolder.ratingBar.setRating((float) mDataSet.get(position).getInt("rating"));
                viewHolder.view.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url= null;
                                try {
                                    url = mDataSet.get(position).getString("author_url");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                mContext.startActivity(intent);
                            }
                        }
                );
            }
            else
            {
                String time=mDataSet.get(position).getString("time_created");
                viewHolder.authorNameView.setText(mDataSet.get(position).getJSONObject("user").getString("name"));
                viewHolder.reviewView.setText(mDataSet.get(position).getString("text"));
                viewHolder.timeView.setText(time);
                Picasso.get().load(mDataSet.get(position).getJSONObject("user").getString("image_url")).into(viewHolder.imageView);
                viewHolder.ratingBar.setRating((float) mDataSet.get(position).getInt("rating"));
                viewHolder.view.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url= null;
                                try {
                                    url = mDataSet.get(position).getString("url");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                mContext.startActivity(intent);
                            }
                        }
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

