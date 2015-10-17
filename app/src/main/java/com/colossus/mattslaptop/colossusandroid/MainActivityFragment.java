package com.colossus.mattslaptop.colossusandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{
    //list view for the devices in this account
    ListView deviceListView;

    //textviews
    EditText torchMsgText;

    //varrious buttons
    Button loginButton;
    Button logoutButton;
    Button displayDevicesButton;
    Button sendTextButton;

    //debug strings
    protected final String CLASS_NAME = "MainActivityFragement";
    protected final String LOGIN_ERROR = "Failed to Login: ";
    protected final String TEXT_WRITE_ERROR = "Failed to write text";

    //list of devices
    protected List<ParticleDevice> particleList;

    //user selected device
    ParticleDevice userSelectedDevice;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //finding the device ListView
        deviceListView = (ListView) rootView.findViewById(R.id.deviceListView);

        //finding the text view
        torchMsgText = (EditText) rootView.findViewById(R.id.torchMessage);

        //finding the buttons
        loginButton = (Button) rootView.findViewById(R.id.loginBtn);
        logoutButton = (Button) rootView.findViewById(R.id.logoutBtn);
        displayDevicesButton = (Button) rootView.findViewById(R.id.displayDevicesButton);
        sendTextButton = (Button) rootView.findViewById(R.id.sendTextBtn);

        //setting up their onClick listener handlers
        loginButton.setOnClickListener(loginBtnClickListener);
        logoutButton.setOnClickListener(logoutBtnClickListener);
        displayDevicesButton.setOnClickListener(displayDevicesBtnClickListener);
        sendTextButton.setOnClickListener(sendTextButtonClickListener);

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
            Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Integer>() {
                public Integer callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                    //logging in
                    particleCloud.logIn(getUserName(), getPassword());

                    //getting a list of devices and passing it into the fragment to inflate the list
                    particleList = particleCloud.getDevices();
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
            ParticleCloudSDK.getCloud().logOut();
            //Toaster
            Toaster.l(getActivity(), "Logged out");
        }
    };

    View.OnClickListener displayDevicesBtnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //technically all of the devices were gotten during the login, but they're
            //populated in the device list so we'll just show them in this method

            //temp particle device param
            ParticleDevice tempDevice;

            // Defined Array values to show in ListView
            String[] values = new String[particleList.size()];
            for(int i = 0; i < particleList.size(); i++) {
                tempDevice = particleList.get(i);
                values[i] = tempDevice.getName();
            }
            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            deviceListView.setAdapter(adapter);

            //setting up the onclick listeners
            deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //based on which one was clicked we can select it and perform some actions
                    userSelectedDevice = particleList.get(position);

                    //Log.e the services
                    Set<String> services = userSelectedDevice.getFunctions();
                    if (services.size() == 0) {
                        Toaster.l(getActivity(), "No services for this core!");
                    }
                }
            });
        }
    };

    View.OnClickListener sendTextButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //getting the text from the text field
            torchMsgText.getText();

            Async.executeAsync(userSelectedDevice, new Async.ApiWork<ParticleDevice, Integer>() {

                public Integer callApi(ParticleDevice particleDevice) throws ParticleCloudException, IOException {
                    //creating a list of Strings
                    List<String> outputText = new ArrayList<String>();
                    outputText.add(torchMsgText.getText().toString());
                    try {
                        return particleDevice.callFunction("message", outputText);
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                        return 2;
                    }
                }

                @Override
                public void onSuccess(Integer returnValue) {
                    Toaster.s(getActivity(), "Write Successful");
                }

                @Override
                public void onFailure(ParticleCloudException e) {
                    Log.e(CLASS_NAME, TEXT_WRITE_ERROR);
                    Toaster.s(getActivity(), "Write Failed");
                }
            });

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

