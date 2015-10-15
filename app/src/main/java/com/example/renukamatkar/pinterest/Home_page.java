package com.example.renukamatkar.pinterest;

import android.app.ActionBar;
import android.content.Intent;// to call another activity page
import android.view.Menu;
import android.view.MenuItem;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import android.view.View;
import android.widget.Button;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;



public class Home_page extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
    }

    //this is onclick function for login pages
    public void loginclick(View view) {
        Intent loginpage = new Intent(this, Login_page.class);

        startActivity(loginpage);


    }

    //this is onclick function for registration page
    public void signup(View view) {
        Intent signup1 = new Intent(this, RegistrationPage.class);
        startActivity(signup1);
    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    // @Override
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
}
  /*  @Override

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreateView(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.FacebookHomeButton);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(mCallbackManager, mCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, requestCode);

    }
}
*/




