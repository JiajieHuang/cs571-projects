package com.example.jiajiehuang.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

    public  List<PlaceItem> mDataSet;
    private String backend;
    private Context mContext;
    private RequestQueue requestQueue;
    public List<PlaceItem> favourite;
    private ProgressDialog dialog;
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
        public View view;

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
    public ResultListAdapter(ArrayList<PlaceItem> dataSet,ArrayList<PlaceItem> favourite,Context context) {
        mContext=context;
        mDataSet=dataSet;
        requestQueue = Volley.newRequestQueue(context);
        this.favourite=favourite;
        backend=context.getString(R.string.backend);
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
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.name.setText(mDataSet.get(position).getName());
        viewHolder.address.setText(mDataSet.get(position).getAddress());
        Picasso.get().load(mDataSet.get(position).getIcon()).into(viewHolder.icon);
        if (!favourite.contains(mDataSet.get(position)))
        {viewHolder.heart.setImageResource(R.drawable.heart_outline_black);}
        else
        {viewHolder.heart.setImageResource(R.drawable.heart_fill_red);}
        viewHolder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favourite.contains(mDataSet.get(position)))
                {
                    favourite.remove(mDataSet.get(position));
                    ((ImageView) v).setImageResource(R.drawable.heart_outline_black);
                    Toast toast = Toast.makeText(mContext, mDataSet.get(position).getName()+"was removed to favourite list", Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    favourite.add(mDataSet.get(position));
                    Toast toast = Toast.makeText(mContext, mDataSet.get(position).getName()+"was added to favourite list", Toast.LENGTH_LONG);
                    toast.show();
                    ((ImageView) v).setImageResource(R.drawable.heart_fill_red);
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.middle.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     dialog=new ProgressDialog(mContext);
                     dialog.setMessage("Fetching Detail");
                     dialog.show();
                     String placeId=mDataSet.get(position).getPlaceId();
                     String url=backend+"place_id="+placeId;
                     Log.v("url",url);
                     JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                             (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                 @Override
                                 public void onResponse(JSONObject response) {
                                     dialog.dismiss();
                                     try {
                                         Intent intent = new Intent(mContext , DetailActivity.class);
                                         intent.putExtra("details",response.getJSONObject("result").toString());
                                         intent.putExtra("name",response.getJSONObject("result").getString("name"));
                                         mContext.startActivity(intent);
                                         //dialog.dismiss();
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }, new Response.ErrorListener() {

                                 @Override
                                 public void onErrorResponse(VolleyError error) {
                                     dialog.dismiss();
                                     Toast.makeText(mContext,"Error occurs",Toast.LENGTH_LONG).show();
                                 }
                             });
                     requestQueue.add(jsonObjectRequest);
                 }
             }
        );
  }
  public void changeData(ArrayList<PlaceItem> results,ArrayList<PlaceItem> favourite)
  {
      this.mDataSet=results;
      this.favourite=favourite;
  }
  public List<PlaceItem> getFavourite(){
        return favourite;
  }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

