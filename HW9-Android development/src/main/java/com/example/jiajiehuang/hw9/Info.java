package com.example.jiajiehuang.hw9;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Info extends Fragment {

    private TextView address;
    private TextView price;
    private TextView phone;
    private RatingBar rating;
    private TextView google_page;
    private TextView website;
    private String[] priceSymbol=new String[]{"","$","$$","$$$"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        String resultJSONString=getActivity().getIntent().getStringExtra("details");
        JSONObject resultJSON= null;
        try {
            resultJSON = (JSONObject) new JSONTokener(resultJSONString).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            address=(TextView) rootView.findViewById(R.id.address);
            if (resultJSON.has("formatted_address"))
            {address.setText(resultJSON.getString("formatted_address"));}
            phone=(TextView) rootView.findViewById(R.id.phone_number);
            if (resultJSON.has("formatted_phone_number"))
            {
                phone.setText(resultJSON.getString("formatted_phone_number"));
                final String number=resultJSON.getString("formatted_phone_number");
                final Intent  callIntent = new Intent(Intent.ACTION_DIAL);
                phone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                phone.setTextColor(getResources().getColor(R.color.colorAccent));
                phone.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callIntent.setData(Uri.parse("tel:"+number));
                                phone.getContext().startActivity(callIntent);

                            }
                        }
                );

            }
            price=(TextView) rootView.findViewById(R.id.price_level);
            if (resultJSON.has("price_level"))
            {price.setText(priceSymbol[resultJSON.getInt("price_level")]);}
            rating=(RatingBar) rootView.findViewById(R.id.MyRating);
            if (resultJSON.has("rating"))
            {rating.setRating((float)resultJSON.getDouble("rating"));}
            google_page=(TextView) rootView.findViewById(R.id.google_page);
            if (resultJSON.has("url"))
            {google_page.setText(
                    Html.fromHtml("<a href=\""+resultJSON.getString("url")+"\">"+resultJSON.getString("url")+"</a>")
            );}
            google_page.setMovementMethod(LinkMovementMethod.getInstance());
            website=(TextView) rootView.findViewById(R.id.website);
            if (resultJSON.has("website")) {
                website.setText(
                        Html.fromHtml("<a href=\"" + resultJSON.getString("website") + "\">" + resultJSON.getString("website") + "</a>")
                );
            }
            website.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }
}
