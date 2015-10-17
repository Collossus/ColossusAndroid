package com.colossus.mattslaptop.colossusandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{
    //list view for the devices in this account
    ListView deviceListView;

    //varrious buttons
    Button loginButton;
    Button logoutButton;

    //debug strings
    protected final String CLASS_NAME = "MainActivity";
    protected final String LOGIN_ERROR = "Failed to Login: ";

    //list of devices
    protected List<ParticleDevice> particleList;

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
        loginButton.setOnClickListener(loginBtnClickListener);
        logoutButton.setOnClickListener(logoutBtnClickListener);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, android.R.id.text1, values);
        return rootView;
    }

    //On click listeners
    View.OnClickListener loginBtnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //calling the Particle API method to login
            Async.executeAsync(ParticleCloud.get(v.getContext()), new Async.ApiWork<ParticleCloud, Integer>() {
                public Integer callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                    //logging in
                    particleCloud.logIn(getUserName(), getPassword());

                    //getting a list of devices and passing it into the fragment to inflate the list
                    particleList = particleCloud.getDevices();

                    //showing them in the list view


                    /**
                     mDevice = sparkCloud.getDevice("1f0034000747343232361234");
                     Integer variable;
                     try {
                     variable = mDevice.getVariable("analogvalue");
                     } catch (ParticleDevice.VariableDoesNotExistException e) {
                     Toaster.s(LoginActivity.this, "Error reading variable");
                     variable = -1;
                     }
                     return variable;
                     */
                    return 1;
                }

                //
                @Override
                public void onSuccess(Integer value) {
                    Toaster.l(getActivity(), "Logged in");
                }

                @Override
                public void onFailure(ParticleCloudException e) {
                    Log.e(CLASS_NAME, LOGIN_ERROR + e);
                    Toaster.l(getActivity(), "Wrong credentials or no internet connectivity, please try again");
                }
            });
        }
    };
    View.OnClickListener logoutBtnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Log out API call
            ParticleCloud.get(v.getContext()).logOut();
            //Toaster
            Toaster.l(getActivity(), "Logged out");
        }
    };

    //this method gets the login user name string, there is no input validation, it just grabs the string and returns it
    public String getUserName(){
        EditText loginText = (EditText)getActivity().findViewById(R.id.loginEmail);
        return loginText.getText().toString();
    }

    //this method gets the login user name string, there is no input validation, it just grabs the string and returns it
    public String getPassword(){
        EditText pswdText = (EditText)getActivity().findViewById(R.id.loginPassword);
        return pswdText.getText().toString();
    }

}

