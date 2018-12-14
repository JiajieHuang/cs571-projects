package com.example.jiajiehuang.hw9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class Tab0search extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab0search, container, false);
        Spinner mySpinner= (Spinner) rootView.findViewById(R.id.category);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.types, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mySpinner.setAdapter(adapter);
        initialAutoComplete(rootView);
        return rootView;
    }
    private void initialAutoComplete(View rootView){
        AutoCompleteTextView location=(AutoCompleteTextView) rootView.findViewById(R.id.location);
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(this.getContext());
        location.setAdapter(adapter);
    }

}
