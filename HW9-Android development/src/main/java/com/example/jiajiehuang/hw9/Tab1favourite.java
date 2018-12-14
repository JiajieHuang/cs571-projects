package com.example.jiajiehuang.hw9;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import 	android.content.Context;

public class Tab1favourite extends Fragment {

    private ArrayList<PlaceItem> favouriteList;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1favourite, container, false);

        this.rootView=rootView;
        initialFavouriteList();
        bindData(favouriteList);
        return rootView;
    }
    private void bindData(ArrayList<PlaceItem> favouriteList){
        RecyclerView recyclerView=rootView.findViewById(R.id.favourite_list);
        TextView noFavourite=rootView.findViewById(R.id.no_favourite);
        if (favouriteList.size()>0)
        {noFavourite.setVisibility(View.INVISIBLE);}
        else
        {noFavourite.setVisibility(View.VISIBLE);}
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        FavouriteListAdapter adapter=new FavouriteListAdapter(favouriteList,this.getActivity(),noFavourite);
        recyclerView.setAdapter(adapter);
        Log.v("bind",favouriteList.size()+"");
    }
    private void initialFavouriteList(){
        FileInputStream fis;
        try {
            fis = getActivity().openFileInput("favourite");
            ObjectInputStream ois = new ObjectInputStream(fis);
            favouriteList = (ArrayList<PlaceItem>) ois.readObject();
            if (favouriteList.size()>0)
            {
                TextView noFavourite=rootView.findViewById(R.id.no_favourite);
                noFavourite.setVisibility(View.INVISIBLE);
            }
            Log.v("count",favouriteList.size()+"");
            ois.close();
        } catch (Exception e) {
            favouriteList=new ArrayList<PlaceItem>();
            try {
                FileOutputStream fos = this.getActivity().openFileOutput("favourite", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(favouriteList);
                oos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
