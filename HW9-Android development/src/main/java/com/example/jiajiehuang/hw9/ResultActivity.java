package com.example.jiajiehuang.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private ArrayList<PlaceItem> results=new ArrayList<PlaceItem>();
    private ArrayList<ArrayList<PlaceItem>> pages;
    private int currentPage;
    private int totalPage;
    private String backend="http://hw9-env-1.us-west-1.elasticbeanstalk.com/?";
    private RequestQueue requestQueue;
    private String nextPageToken;
    private RecyclerView mRecyclerView;
    private ResultListAdapter adapter;
    private ArrayList<PlaceItem> favourite;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        dialog=new ProgressDialog(this);
        dialog.setMessage("Fetching next page");
        pages=new ArrayList<ArrayList<PlaceItem>> (); ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        requestQueue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        nextPageToken=intent.getStringExtra("nextPageToken");
        String resultJSON=intent.getStringExtra("results");
        currentPage=0;
        totalPage=1;
            try {
                JSONArray array = (JSONArray) new JSONTokener(resultJSON).nextValue();
                int numRespones = array.length();
                for(int i=0; i<numRespones;i++){
                    JSONObject place=array.getJSONObject(i);
                    results.add(new PlaceItem(place.getString("name"),place.getString("vicinity"),place.getString("icon"),place.getString("place_id")));

                }
                pages.add((ArrayList<PlaceItem>) results.clone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
    @Override
    public void onStart(){
        super.onStart();
        getFavourite();
        if (nextPageToken==null)
        {
            Button next=findViewById(R.id.next);
            next.setEnabled(false);
        }
        if (results.size()==0)
        {
            findViewById(R.id.no_result).setVisibility(View.VISIBLE);
        }
        mRecyclerView=(RecyclerView) findViewById(R.id.result_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new ResultListAdapter(results,favourite,this);
        mRecyclerView.setAdapter(adapter);
    }
    private void getFavourite(){
        FileInputStream fis;
        try {
            fis = openFileInput("favourite");
            ObjectInputStream ois = new ObjectInputStream(fis);
            favourite = (ArrayList<PlaceItem>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        getFavourite();
        adapter=new ResultListAdapter(results,favourite,this);
        Log.v("favourite",favourite.size()+"");
        mRecyclerView.setAdapter(adapter);
    }
    @Override
    public void onPause(){
        super.onPause();
        try{
            favourite=(ArrayList<PlaceItem>) adapter.getFavourite();
            FileOutputStream fos = this.openFileOutput("favourite", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(favourite);
            oos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void previous(View view){
        currentPage--;
        if (currentPage==0)
        {
            Button previous=findViewById(R.id.previous);
            previous.setEnabled(false);
        }
        results=pages.get(currentPage);
        Log.v("pages",""+pages.size());
        adapter=new ResultListAdapter(results,favourite,this);
        mRecyclerView.setAdapter(adapter);
        Button next=findViewById(R.id.next);
        next.setEnabled(true);
    }
    public void nextPage(View view){
        if (currentPage<totalPage-1)
        {
            Button previous=findViewById(R.id.previous);
            previous.setEnabled(true);
            currentPage++;
            if (currentPage==totalPage-1)
            {
                Button next=findViewById(R.id.next);
                next.setEnabled(false);
            }
            results=pages.get(currentPage);
            adapter=new ResultListAdapter(results,favourite,this);
            mRecyclerView.setAdapter(adapter);
        }
        else{
            currentPage++;
            totalPage++;
            dialog.show();
            final String url = backend+"&next_page_token="+nextPageToken;
            Log.v("tag",url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("tag",response.toString());
                            results=new ArrayList<PlaceItem>();
                            try {
                                JSONArray array = response.getJSONArray("results");
                                //nextPageToken=response.getString("next_page_token");
                                int numRespones = array.length();
                                for(int i=0; i<numRespones;i++){
                                    JSONObject place=array.getJSONObject(i);
                                    results.add(new PlaceItem(place.getString("name"),place.getString("vicinity"),place.getString("icon"),place.getString("place_id")));
                                }
                                pages.add((ArrayList<PlaceItem>) results.clone());
                                //adapter=new ResultListAdapter(results,favourite,getParent());
                                adapter.changeData(results,favourite);
                                dialog.dismiss();
                                if (response.has("next+page_token"))
                                {
                                    nextPageToken=response.getString("next+page_token");
                                    Button next=findViewById(R.id.next);
                                    next.setEnabled(true);
                                }
                                else
                                {
                                    Button next=findViewById(R.id.next);
                                    next.setEnabled(false);
                                }
                                mRecyclerView.setAdapter(adapter);
                                //adapter.notifyDataSetChanged();
                                //adapter.swapImageRecords(results);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Button previous=findViewById(R.id.previous);
                            previous.setEnabled(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }
    }
}
