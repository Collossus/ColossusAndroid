package com.colossus.mattslaptop.colossusandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class MainActivity extends AppCompatActivity {

    protected final String CLASS_NAME = "MainActivity";
    protected final String LOGIN_ERROR = "Failed to Login: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    //this is the button click listener method for the log in button.  It calls
    public void onClickLoginBtn(View view){
        //calling the Particle API method to login
        Async.executeAsync(ParticleCloud.get(view.getContext()), new Async.ApiWork<ParticleCloud, Integer>() {
            public Integer callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(getUserName(), getPassword());

                return 1;
            }

            //
            @Override
            public void onSuccess(Integer value) {
                Toaster.l(MainActivity.this, "Logged in");
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                Log.e(CLASS_NAME, LOGIN_ERROR + e);
                Toaster.l(MainActivity.this, "Wrong credentials or no internet connectivity, please try again");
            }
        });
    }

    //this method gets the login user name string, there is no input validation, it just grabs the string and returns it
    public String getUserName(){
        EditText loginText = (EditText)findViewById(R.id.loginEmail);
        return loginText.getText().toString();
    }

    //this method gets the login user name string, there is no input validation, it just grabs the string and returns it
    public String getPassword(){
        EditText pswdText = (EditText)findViewById(R.id.loginPassword);
        return pswdText.getText().toString();
    }

    public void onClickLogoutBtn(View view){
        //Log out API call
        ParticleCloud.get(view.getContext()).logOut();
        //Toaster
        Toaster.l(MainActivity.this, "Logged out");
    }
}
