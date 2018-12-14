package com.example.jiajiehuang.hw9;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

public class RatingComparator implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject review1, JSONObject review2) {
        int result=0;
        try {
            result= review1.getInt("rating")-review2.getInt("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
