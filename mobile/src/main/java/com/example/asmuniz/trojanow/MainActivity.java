package com.example.asmuniz.trojanow;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import com.example.asmuniz.trojanow.obj.User;

public class MainActivity extends Activity {

    private static final String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "active user = " + User.getActiveUser());
        if (User.getActiveUser() != null)
            startActivity(new Intent(this, PostListActivity.class));
    }

    public void launchSignUp(View view) {
        Log.d(TAG, "in launch sign up...");
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void launchSignIn(View view) {
        Log.d(TAG, "in launch sign in...");
        startActivity(new Intent(this, SignInActivity.class));
    }
}
