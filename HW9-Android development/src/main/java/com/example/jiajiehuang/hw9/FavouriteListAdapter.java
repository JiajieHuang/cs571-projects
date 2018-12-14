package com.example.jiajiehuang.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.ViewHolder> {

    public  List<PlaceItem> mDataSet;
    private String backend;
    private Context mContext;
    private RequestQueue requestQueue;
    private TextView noFavourite;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView address;
        public  ImageView heart;
        public LinearLayout middle;
        public ViewHolder(View v) {
            super(v);
            icon=(ImageView) v.findViewById(R.id.icon);
            name=(TextView) v.findViewById(R.id.place_name);
            address=(TextView)v.findViewById(R.id.place_address);
            heart=(ImageView) v.findViewById(R.id.heart);
            middle=(LinearLayout) v.findViewById(R.id.middle);
            // Define click listener for the ViewHolder's View.
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FavouriteListAdapter(ArrayList<PlaceItem> dataSet,Context context,TextView noFavourite) {
        mContext=context;
        mDataSet=dataSet;
        requestQueue = Volley.newRequestQueue(context);
        backend=context.getString(R.string.backend);
        this.noFavourite=noFavourite;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result_row, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.name.setText(mDataSet.get(position).getName());
        viewHolder.address.setText(mDataSet.get(position).getAddress());
        Picasso.get().load(mDataSet.get(position).getIcon()).into(viewHolder.icon);
        viewHolder.heart.setImageResource(R.drawable.heart_fill_red);
        viewHolder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyDataSetChanged();
                Toast toast=Toast.makeText(mContext,mDataSet.get(position).getName()+"was removed from favourite list.",Toast.LENGTH_LONG);
                mDataSet.remove(position);
                toast.show();
                try {
                    FileOutputStream fos = mContext.openFileOutput("favourite", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(mDataSet);
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mDataSet.size()==0)
                {noFavourite.setVisibility(View.VISIBLE);}
            }
        });
        viewHolder.middle.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     final ProgressDialog dialog=new ProgressDialog(mContext);
                     dialog.setMessage("Fetching Detail");
                     dialog.show();
                     String placeId=mDataSet.get(position).getPlaceId();
                     String url=backend+"place_id="+placeId;
                     Log.v("url",url);
                     JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                             (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                 @Override
                                 public void onResponse(JSONObject response) {
                                     try {
                                         Intent intent = new Intent(mContext , DetailActivity.class);
                                         intent.putExtra("details",response.getJSONObject("result").toString());
                                         intent.putExtra("name",response.getJSONObject("result").getString("name"));
                                         mContext.startActivity(intent);
                                         dialog.dismiss();
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }, new Response.ErrorListener() {

                                 @Override
                                 public void onErrorResponse(VolleyError error) {
                                     // TODO: Handle error
                                     dialog.dismiss();
                                     Toast.makeText(mContext,"Error occurs",Toast.LENGTH_LONG).show();
                                 }
                             });
                     requestQueue.add(jsonObjectRequest);
                 }
             }
        );
  }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

