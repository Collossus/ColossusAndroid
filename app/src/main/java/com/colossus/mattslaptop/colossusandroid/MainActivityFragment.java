package com.colossus.mattslaptop.colossusandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{
    //list view for the devices in this account
    ListView deviceListView;
    //varrious buttons
    Button loginButton;
    Button logoutButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //finding the device ListView
        deviceListView = (ListView) rootView.findViewById(R.id.deviceListView);

        //finding the buttons
        loginButton = (Button) rootView.findViewById(R.id.loginBtn);
        logoutButton = (Button) rootView.findViewById(R.id.logoutBtn);

        //setting up their onClick listener handlers
        loginButton.setOnClickListener(myhandler1);
        logoutButton.setOnClickListener(myhandler2);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        return rootView;
    }


}

