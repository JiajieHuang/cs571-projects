package com.example.jiajiehuang.hw9;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Comparator;

public class TimeComparator implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject review1, JSONObject review2) {
        int result=0;
        try {
            if (review1.has("time"))
            {result= review1.getInt("time")-review2.getInt("time");}
            else if (review1.has("time_created"))
            {
                result= Timestamp.valueOf(review1.getString("time_created")).compareTo(Timestamp.valueOf(review2.getString("time_created")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
