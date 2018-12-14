package com.example.jiajiehuang.hw9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity{

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
    private String backend="http://hw9-env-1.us-west-1.elasticbeanstalk.com/?";
    private String name;
    private Intent intent;
    private JSONObject details;
    private List<PlaceItem> favourite;
    private PlaceItem place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name=getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_detail);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_detail);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
        tabLayout.getTabAt(1).setIcon(R.drawable.photos);
        tabLayout.getTabAt(2).setIcon(R.drawable.maps);
        tabLayout.getTabAt(3).setIcon(R.drawable.review);

        getFavourite();
        try {
            details=(JSONObject) new JSONTokener(getIntent().getStringExtra("details")).nextValue();
            Log.v("details",details.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getPlace();
    }
    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.textView);
        ImageView img_title = (ImageView) view.findViewById(R.id.imageView);
        switch(position)
        {
            case 0:
                txt_title.setText("Info");
                img_title.setImageResource(R.drawable.info_outline);
                break;
            case 1:
                txt_title.setText("Photo");
                img_title.setImageResource(R.drawable.photos);
                break;
            case 2:
                txt_title.setText("Map");
                img_title.setImageResource(R.drawable.maps);
                break;
            case 3:
                txt_title.setText("Review");
                img_title.setImageResource(R.drawable.review);
                break;
        }

        return view;
    }
    private void getPlace(){
        try {
            String place_id = details.getString("place_id");
            String name = details.getString("name");
            String address=details.getString("formatted_address");
            String icon=details.getString("icon");
            place=new PlaceItem(name,address,icon,place_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    public void onClick(View view){

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        if (favourite.contains(place))
        {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.heart_fill_white));
        }
        return true;
    }
    private void share() {
        try {
            String name = details.getString("name");
            String address=details.getString("formatted_address");
            String website=details.getString("website");
            String text= "Check out "+name+" locate at "+address+" website:"+website;
            String url="https://twitter.com/intent/tweet?text="+text+"&url="+website+"&hashtags=TravelAndEntertainmentSearch";
            Log.v("twitter",url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void addToFavourite(MenuItem item){
            favourite.add(place);
            item.setIcon(getResources().getDrawable(R.drawable.heart_fill_white));
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
            Toast toast=Toast.makeText(this,name+"was added to your favourite list",Toast.LENGTH_LONG);
            toast.show();
    }
    private void removeFromFavourite(MenuItem item){
        favourite.remove(place);
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
        item.setIcon(getResources().getDrawable(R.drawable.heart_outline_white));
        Toast toast=Toast.makeText(this,name+"was removed from your favourite list",Toast.LENGTH_LONG);
        toast.show();
    }
    @Override
    public void onStop(){
        super.onStop();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.share_menu:
                share();
                return true;
            case R.id.favourite_menu:
                if (!favourite.contains(place))
                {addToFavourite(item);}
                else
                {removeFromFavourite(item);}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
     }
     /**
      * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
      * one of the sections/tabs/pages.
      */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return new Info();
                case 1:
                    return new Photos();
                case 2:
                    return new Map();
                case 3:
                    return new Review();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}
