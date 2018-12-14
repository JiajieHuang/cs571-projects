package com.example.jiajiehuang.hw9;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {
    private GoogleApiClient mGoogleApiClient;
    private int mHeight;

    private int mWidth;

    public PhotoTask(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient =mGoogleApiClient;
    }

    /**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     */
    @Override
    protected AttributedPhoto doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        Log.v("start","startdoInBackground");
        final String placeId = params[0];
        AttributedPhoto attributedPhoto = null;
        List<Bitmap> list=new ArrayList<Bitmap>();
        //Log.v("placeID",mGoogleApiClient.);
        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();
        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                for (int i=0;i<photoMetadataBuffer.getCount();i++)
                {
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(i);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    int width=photo.getMaxWidth();
                    int height=photo.getMaxHeight();
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient,width,height).await()
                            .getBitmap();
                    list.add(image);
                }
                attributedPhoto=new AttributedPhoto(list);
            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();
        }
        Log.v("end","enddoInBackground");
        return attributedPhoto;
    }

    /**
     * Holder for an image and its attribution.
     */
    class AttributedPhoto {

        public final List<Bitmap> bitmap;

        public AttributedPhoto(List<Bitmap> bitmap) {
            this.bitmap = bitmap;
        }
    }
}