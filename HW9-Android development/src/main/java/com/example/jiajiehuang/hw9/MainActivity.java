package com.example.jiajiehuang.hw9;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private LocationManager locationManager;
    private LocationListener listener;
    private Button b;
    private TextView t;
    private RequestQueue requestQueue;
    private String backend="http://hw9-env-1.us-west-1.elasticbeanstalk.com/?";
    private double lat;
    private double lon;
    private ArrayList<ArrayList<PlaceItem>> pages;
    private ArrayList<PlaceItem> results;
    private ArrayList<PlaceItem> favourite;
    private  ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        results=new ArrayList<PlaceItem>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        //createFavouriteFile();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        dialog=new ProgressDialog(this);
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));

        Button searchButton=(Button) findViewById(R.id.search);
        b = (Button) findViewById(R.id.clear);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               lat=location.getLatitude();
               lon=location.getLongitude();
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
    }
    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab2, null);
        TextView txt_title = (TextView) view.findViewById(R.id.textView);
        txt_title.setText(position==0?"search":"favourite");
        ImageView img_title = (ImageView) view.findViewById(R.id.imageView);
        img_title.setImageResource(position==0?R.drawable.search:R.drawable.heart_fill_white);
        return view;
    }
    /*
    @Override
    public void onStart()
    {
        super.onStart();
        //Log.v("start",""+favourite.size());
        FileInputStream fis;
        try {
            fis = openFileInput("favourite");
            ObjectInputStream ois = new ObjectInputStream(fis);
            favourite = (ArrayList<PlaceItem>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void createFavouriteFile()
    {
        try {
            FileInputStream fis;
            fis = openFileInput("favourite");
            ObjectInputStream ois = new ObjectInputStream(fis);
            favourite = (ArrayList<PlaceItem>) ois.readObject();
            Log.v("getFavourite","getFavourite");
            ois.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        /*try {
            FileInputStream fis;
            fis = openFileInput("favourite");
            ObjectInputStream ois = new ObjectInputStream(fis);
            favourite = (ArrayList<PlaceItem>) ois.readObject();
            Log.v("getFavourite","getFavourite");
            ois.close();
        } catch (FileNotFoundException e) {
            try {
                File file=new File("favourite");
                Log.v("createFile","createFile");
                FileOutputStream fos = this.openFileOutput("favourite", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                favourite=new ArrayList<>();
                oos.writeObject(favourite);
                oos.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void alertNoKeyword() {
        TextView alert=(TextView) findViewById(R.id.alert_keyword);
        alert.setVisibility(View.VISIBLE);
    }
    private void alertNoLocation() {
        TextView alert=(TextView) findViewById(R.id.alert_location);
        alert.setVisibility(View.VISIBLE);
    }
    public void seeResult(View view)
    {

        dialog.setMessage("Fetch Results");

        EditText edit = (EditText)findViewById(R.id.keyword);
        String keyword=edit.getText().toString();
        Spinner spinner = (Spinner)findViewById(R.id.category);
        String category=spinner.getSelectedItem().toString();
        edit = (EditText)findViewById(R.id.distance);
        String distance=edit.getText().toString();
        String from="";
        RadioGroup rg=(RadioGroup) findViewById(R.id.radioGroup);
        String otherLocation="";
        if(rg.getCheckedRadioButtonId()!=-1){
            int id= rg.getCheckedRadioButtonId();
            View radioButton = rg.findViewById(id);
            int radioId = rg.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) rg.getChildAt(radioId);
            from= (String) btn.getText();
            if (from.equals("current location"))
            {
                from="here";
            }
            else
            {
                from="other";
                edit = (EditText)findViewById(R.id.location);
                otherLocation=edit.getText().toString();
            }
        }
        boolean valid=true;
        Log.v("keyword",keyword);
        //Log.v("location",otherLocation);
        if (keyword.trim().equals("")) {
            alertNoKeyword();
            valid=false;
        }
        if (from.equals("other")&&otherLocation.trim().equals("")){
            alertNoLocation();
            valid=false;
        }
        if (!valid)
        {
            Toast toast=Toast.makeText(this,"please fix all fileds with errors.",Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        dialog.show();
        final String url = backend+"lat="+lat+"&lon="+lon+"&category="+category+"&keyword="+keyword+"&location="+otherLocation+"&distance="+distance+"&from="+from;
        Log.v("url",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //results=new ArrayList<PlaceItem>();
                        try {
                            JSONArray array = response.getJSONArray("results");
                            Log.v("array",array.toString());
                            Intent intent = new Intent(getApplicationContext() , ResultActivity.class);
                            intent.putExtra("results",array.toString());
                            if (response.has("next_page_token"))
                            {
                                intent.putExtra("nextPageToken",response.getString("next_page_token"));
                            }
                            startActivity(intent);
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error occurs",Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
       locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    @Override
    public void onRestart(){
        super.onRestart();
        FileInputStream fis;
        try {
            fis = this.openFileInput("favourite");
            ObjectInputStream ois = new ObjectInputStream(fis);
            favourite = (ArrayList<PlaceItem>) ois.readObject();
            RecyclerView recyclerView=findViewById(R.id.favourite_list);
            TextView noFavourite=(TextView)findViewById(R.id.no_favourite);
            if (favourite.size()>0)
            {noFavourite.setVisibility(View.INVISIBLE);}
            else
            {noFavourite.setVisibility(View.VISIBLE);}
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            FavouriteListAdapter adapter=new FavouriteListAdapter(favourite,this,noFavourite);
            recyclerView.setAdapter(adapter);
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    @Override
    public void onPause(){
        super.onPause();
        try{
            FileOutputStream fos = this.openFileOutput("favourite", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(favourite);
            Log.v("back",favourite.size()+"");
            oos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }*/
    public void clear(View view)
    {
        EditText edit = (EditText)findViewById(R.id.keyword);
        edit.setText("");
        Spinner spinner = (Spinner)findViewById(R.id.category);
        spinner.setSelection(0);
        edit = (EditText)findViewById(R.id.distance);
        edit.setText("");
        RadioButton current=(RadioButton) findViewById(R.id.curent_location);
        current.setChecked(true);
        AutoCompleteTextView location = (AutoCompleteTextView)findViewById(R.id.location);
        location.setText("");
        TextView alert=(TextView) findViewById(R.id.alert_keyword);
        //alert.getLayoutParams().height=getResources().getDimensionPixelSize(R.dimen.alert_height_invisible);
        alert.setVisibility(View.INVISIBLE);
        alert=(TextView) findViewById(R.id.alert_location);
        //alert.getLayoutParams().height=getResources().getDimensionPixelSize(R.dimen.alert_height_invisible);
        alert.setVisibility(View.INVISIBLE);

    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    Tab0search tab0=new Tab0search();
                    return new Tab0search();
                case 1:
                    Tab1favourite tab1=new Tab1favourite();
                    return new Tab1favourite();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

    }
}
