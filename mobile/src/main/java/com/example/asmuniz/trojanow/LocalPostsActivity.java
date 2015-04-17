package com.example.asmuniz.trojanow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.example.asmuniz.trojanow.obj.Post;
import com.example.asmuniz.trojanow.util.GPSTracker;

/**
 * Created by asmuniz on 4/17/15.
 */
public class LocalPostsActivity extends Activity {

    private static final String TAG = "local_posts";
    private NumberPicker np;
    private ProgressDialog pd;
    private GPSTracker gps;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_local_posts);

        pd = new ProgressDialog(this);
        pd.setMessage("Retrieving posts");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);

        gps = new GPSTracker(LocalPostsActivity.this);

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(10);
        np.setWrapSelectorWheel(true);
    }

    public void setRadius(View view) {
        Post.setRadiusInMiles(np.getValue());
        loadPosts();
    }

    public void clearRadius(View view) {
        Post.setRadiusInMiles(0);
        loadPosts();
    }

    private void loadPosts() {
        startActivity(new Intent(LocalPostsActivity.this, PostListActivity.class));
        finish();
    }
}
