package com.example.asmuniz.trojanow;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by asmuniz on 4/12/15.
 */
public class HomePageActivity extends Activity {

    private static final String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }
}
