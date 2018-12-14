package com.example.jiajiehuang.hw9;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class Photos extends Fragment {
    private String placeId;
    private List<Bitmap> listPhotos;
    private RecyclerView mRecyclerView;
    private PhotoAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private View rootView;
    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            listPhotos.add(placePhotoResult.getBitmap());
            Log.v("length",listPhotos.size()+"");
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        if (mGoogleApiClient==null)
        {
            mGoogleApiClient= new GoogleApiClient
                    .Builder(this.getContext())
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this.getActivity(),new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            // your code here
                        }
                    })
                    .build();
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPhotos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        placeId=getPlaceID();
        this.rootView=rootView;
        getPhotos();
        return rootView;
    }
    private String getPlaceID()
    {
        String resultJSONString=getActivity().getIntent().getStringExtra("details");
        JSONObject resultJSON;
        String placeID="";
        try {
            resultJSON = (JSONObject) new JSONTokener(resultJSONString).nextValue();
            placeID=resultJSON.getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placeID;
    }

    private void getPhotos(){
        Log.v("id",placeId);
       new PhotoTask(mGoogleApiClient) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                Log.v("id","null");
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    Log.v("id",""+attributedPhoto.bitmap.size());
                    adapter=new PhotoAdapter(attributedPhoto.bitmap);
                    mRecyclerView.setAdapter(adapter);
                    if (attributedPhoto.bitmap.size()>0)
                    {
                        rootView.findViewById(R.id.noPhoto).setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        rootView.findViewById(R.id.noPhoto).setVisibility(View.VISIBLE);
                    }
                }
                else
                {rootView.findViewById(R.id.noPhoto).setVisibility(View.VISIBLE);}
            }
        } .execute(placeId);
        /*Log.v("id","end");
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            for (int i=0;i<photoMetadataBuffer.getCount();i++)
                            {
                                photoMetadataBuffer.get(i).getPhoto(mGoogleApiClient).setResultCallback(mDisplayPhotoResultCallback);
                            }
                            adapter=new PhotoAdapter(listPhotos);
                            mRecyclerView.setAdapter(adapter);
                        }
                        photoMetadataBuffer.release();
                    }
                });*/
    }
}
