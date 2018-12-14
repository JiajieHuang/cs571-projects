package com.example.jiajiehuang.hw9;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class Map extends Fragment implements OnMapReadyCallback{

    private MapView mapView;
    private Double lat;
    private Double lon;
    private View rootView;
    private RequestQueue requestQueue;
    private GoogleMap map;
    private String origin="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Spinner mySpinner= (Spinner) rootView.findViewById(R.id.spinner);
        requestQueue = Volley.newRequestQueue(this.getContext());
        String resultJSONString=getActivity().getIntent().getStringExtra("details");
        JSONObject resultJSON= null;
        try {
            resultJSON = (JSONObject) new JSONTokener(resultJSONString).nextValue();
            lat=resultJSON.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            lon=resultJSON.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.methods, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mySpinner.setAdapter(adapter);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        this.rootView=rootView;
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String originAddress="";
                if (view!=null)
                {AutoCompleteTextView origin=(AutoCompleteTextView) view.getRootView().findViewById(R.id.origin);
                    originAddress=origin.getText().toString();}
                String travelMode="driving";
                switch(position)
                {
                    case 0:
                        travelMode="driving";
                        break;
                    case 1:
                        travelMode="walking";
                        break;
                    case 2:
                        travelMode="bicycling";
                        break;
                    case 3:
                        travelMode="transit";
                        break;
                     default:
                        break;
                }
                String url =
                        "https://maps.googleapis.com/maps/api/directions/json" +
                                "?origin=" + originAddress.replace(" ", "+")
                                .replace(",", "") +
                                "&destination=" +lat+","+lon+
                                "&mode="+travelMode+"&key=AIzaSyA50ahRJ-Sq7545T81lWHwYXR2fGRvtTBA";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                generateRoute(response);
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error

                            }
                        });
                requestQueue.add(jsonObjectRequest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        initialAutoComplete();
        return rootView;
    }
    private void initialAutoComplete(){
        AutoCompleteTextView origin=(AutoCompleteTextView) rootView.findViewById(R.id.origin);
        AdapterView.OnItemClickListener onItemClickListener =
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String originAddress=((Address)adapterView.
                                getItemAtPosition(i)).getPlaceText();
                        String travelMode=((Spinner)rootView.findViewById(R.id.spinner)).getSelectedItem().toString();
                        String url =
                                "https://maps.googleapis.com/maps/api/directions/json" +
                                        "?origin=" + originAddress.replace(" ", "+")
                                        .replace(",", "") +
                                        "&destination=" +lat+","+lon+
                                        "&mode="+travelMode+"&key=AIzaSyA50ahRJ-Sq7545T81lWHwYXR2fGRvtTBA";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        generateRoute(response);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO: Handle error
                                    }
                                });
                        requestQueue.add(jsonObjectRequest);
                    }
                };
        origin.setOnItemClickListener(onItemClickListener);
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(this.getContext());
        origin.setAdapter(adapter);
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }
    private void generateRoute(JSONObject response){
        try {
            map.clear();
            LatLng coordinates = new LatLng(lat, lon);
            map.addMarker(new MarkerOptions().position(coordinates));
            Log.v("response",response.toString());
            Double originLat=response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lat");
            Double originLon=response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lng");
            PolylineOptions lineOptions = new PolylineOptions();
            JSONArray steps=response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            int numSteps=steps.length();
            for (int i=0;i<numSteps;i++)
            {
               List<LatLng> decodePoints=decodePoly(steps.getJSONObject(i).getJSONObject("polyline").getString("points"));
               for (LatLng latLng:decodePoints) {
                   Log.v("lat",latLng.toString());
                   lineOptions.add(latLng);
               }
            }
            lineOptions.color(Color.BLUE);
            Polyline polyline = map.addPolyline(lineOptions);
            map.addMarker(new MarkerOptions().position(new LatLng(originLat, originLon)));
            mapView.onResume();
            JSONObject JSONbound=response.getJSONArray("routes").getJSONObject(0).getJSONObject("bounds");
            LatLng northeast=new LatLng(JSONbound.getJSONObject("northeast").getDouble("lat"),JSONbound.getJSONObject("northeast").getDouble("lng"));
            LatLng southwest=new LatLng(JSONbound.getJSONObject("southwest").getDouble("lat"),JSONbound.getJSONObject("southwest").getDouble("lng"));
            LatLngBounds bounds=new LatLngBounds(southwest,northeast);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void onMapReady(GoogleMap map) {
        this.map=map;
        LatLng coordinates = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(coordinates));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
        mapView.onResume();
    }
}
