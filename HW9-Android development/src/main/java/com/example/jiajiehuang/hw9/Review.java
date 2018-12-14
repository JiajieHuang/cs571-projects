package com.example.jiajiehuang.hw9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Collections;
import java.util.Comparator;

public class Review extends Fragment {

    private JSONArray googleReview;
    private JSONArray YelpReview;
    private RecyclerView mRecyclerView;
    private ReviewAdapter adapterGoogle;
    private ReviewAdapter adapterYelp;
    private TimeComparator timeComparator;
    private RatingComparator ratingComparator;
    private RequestQueue requestQueue;
    private String backend="http://hw9-env-1.us-west-1.elasticbeanstalk.com/?";
    private Comparator currentComparator;
    private JSONArray currentReview;
    private String currentKind="google";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        requestQueue = Volley.newRequestQueue(this.getContext());
        getReviewData();
        initialSpinner(rootView);
        initialComparator();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviewRecyclerView);
        adapterGoogle=new ReviewAdapter(googleReview,currentKind,getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(adapterGoogle);
        if (googleReview.length()==0)
        {rootView.findViewById(R.id.no_review).setVisibility(View.VISIBLE);}
        Spinner orderSpinner=(Spinner) rootView.findViewById(R.id.review_order_spinner);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (view == null)
                {return;}
                switch (position) {
                    case 0:
                        getReviewData();
                        adapterGoogle=new ReviewAdapter(googleReview,"google",getActivity());
                        if (currentKind.equals("google"))
                        {
                            mRecyclerView.setAdapter(adapterGoogle);
                            if (googleReview.length()==0)
                            {view.getRootView().findViewById(R.id.no_review).setVisibility(View.VISIBLE);}
                            else
                            {view.getRootView().findViewById(R.id.no_review).setVisibility(View.INVISIBLE);}
                        }
                        else
                        {
                            mRecyclerView.setAdapter(adapterYelp);
                            if (YelpReview.length()==0)
                            {view.getRootView().findViewById(R.id.no_review).setVisibility(View.VISIBLE);}
                            else
                            {view.getRootView().findViewById(R.id.no_review).setVisibility(View.INVISIBLE);}
                        }
                        break;
                    case 1:
                        Collections.sort(adapterGoogle.mDataSet,timeComparator);
                        Collections.sort(adapterYelp.mDataSet,timeComparator);
                        adapterGoogle.notifyDataSetChanged();
                        adapterYelp.notifyDataSetChanged();
                        break;
                    case 2:
                        Collections.sort(adapterGoogle.mDataSet,Collections.reverseOrder(timeComparator));
                        Collections.sort(adapterYelp.mDataSet,Collections.reverseOrder(timeComparator));
                        adapterGoogle.notifyDataSetChanged();
                        adapterYelp.notifyDataSetChanged();
                        break;
                    case 3:
                        Collections.sort(adapterGoogle.mDataSet, ratingComparator);
                        Collections.sort(adapterYelp.mDataSet, ratingComparator);
                        adapterGoogle.notifyDataSetChanged();
                        adapterYelp.notifyDataSetChanged();
                        break;
                    case 4:
                        Collections.sort(adapterGoogle.mDataSet, Collections.reverseOrder(ratingComparator));
                        Collections.sort(adapterYelp.mDataSet, Collections.reverseOrder(ratingComparator));
                        adapterGoogle.notifyDataSetChanged();
                        adapterYelp.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner typeSpinner=(Spinner) rootView.findViewById(R.id.review_type_spinner);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (view==null)
                {return;}
                switch (position) {
                    case 0:
                        mRecyclerView.setAdapter(adapterGoogle);
                        currentKind="google";
                        if (googleReview.length()==0)
                        {view.getRootView().findViewById(R.id.no_review).setVisibility(View.VISIBLE);}
                        else
                        {view.getRootView().findViewById(R.id.no_review).setVisibility(View.INVISIBLE);}
                        break;
                    case 1:
                        mRecyclerView.setAdapter(adapterYelp);
                        currentKind="yelp";
                        if (YelpReview==null||YelpReview.length()==0)
                        {view.getRootView().findViewById(R.id.no_review).setVisibility(View.VISIBLE);}
                        else
                        {view.getRootView().findViewById(R.id.no_review).setVisibility(View.INVISIBLE);}
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }
    private void getYelpReview(JSONObject detail) throws JSONException {
        JSONArray addressComponent=detail.getJSONArray("address_components");
        int numComponents=addressComponent.length();
        String name=detail.getString("name").replace(" ","+");
        String address=detail.getString("formatted_address").split(",")[0].replace(" ","+").replace("#","");
        String city="";
        String state="";
        for (int i=0;i<numComponents;i++)
        {
            if(addressComponent.getJSONObject(i).getJSONArray("types").getString(0).equals("locality"))
            {city=addressComponent.getJSONObject(i).getString("long_name").replace(" ","+");}
            if(addressComponent.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_1"))
            {state=addressComponent.getJSONObject(i).getString("short_name");}
        }
        String url = backend+"kind=Yelp&name="+name+"&address="+address+"&city="+city+"&state="+state+"&country=US";
        Log.v("url",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            YelpReview=response.getJSONArray("reviews");
                            adapterYelp=new ReviewAdapter(YelpReview,"yelp",getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.v("tag","error");
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
    private void initialComparator(){
        timeComparator=new TimeComparator();
        ratingComparator=new RatingComparator();
    }
    private void getReviewData(){
        //currentKind="google";
        String detailString=getActivity().getIntent().getStringExtra("details");
        try {
            JSONObject detail=(JSONObject) new JSONTokener(detailString).nextValue();
            if(detail.has("reviews"))
            {googleReview=detail.getJSONArray("reviews");}
            else
            {googleReview=new JSONArray();}
            getYelpReview(detail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initialSpinner(View rootView){
        ArrayAdapter<CharSequence> adapter_type = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.review_type, android.R.layout.simple_spinner_item);
        adapter_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner reviewKindSpinner= (Spinner) rootView.findViewById(R.id.review_type_spinner);
        reviewKindSpinner.setAdapter(adapter_type);
        ArrayAdapter<CharSequence> adapter_order = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.review_order, android.R.layout.simple_spinner_item);
        adapter_order.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner reviewOrderSpinner= (Spinner) rootView.findViewById(R.id.review_order_spinner);
        reviewOrderSpinner.setAdapter(adapter_order);
    }

}
