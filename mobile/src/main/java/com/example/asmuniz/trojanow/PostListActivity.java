package com.example.asmuniz.trojanow;

import com.example.asmuniz.trojanow.obj.User;
import com.example.asmuniz.trojanow.util.DateUtil;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.asmuniz.trojanow.obj.Feed;
import com.example.asmuniz.trojanow.obj.Post;
import com.example.asmuniz.trojanow.util.GPSTracker;
import com.example.asmuniz.trojanow.util.PostListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by asmuniz on 4/12/15.
 */
public class PostListActivity extends ListActivity {

    private static final String TAG = "post_list";

    private List<Post> postList;
    private ArrayAdapter<Post> adapter;
    private ProgressDialog pd;

    private GPSTracker gps;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading posts");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);

        gps = new GPSTracker(PostListActivity.this);

        Log.d(TAG, "in post list onCreate");

        postList = new ArrayList<>();
        // use your custom layout
        adapter = new PostListAdapter(this, R.layout.post_row_layout, postList);
        setListAdapter(adapter);
        goGetData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "in onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_test_gps:
                showGpsData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showGpsData() {
        if (gps.canGetLocation()) {
            double lat = gps.getLatitude();
            double lon = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Lat: " + lat + ", Lon: " + lon, Toast.LENGTH_LONG).show();
        }
        else {
            gps.showSettingsAlert();
            gps.getLocation();
        }
    }

    private void logout() {
        User.setActiveUserId(0);
        Feed.setActiveFeedId(1);
        startActivity(new Intent(PostListActivity.this, MainActivity.class));
    }

    private void goGetData() {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/get_posts_by_feed?feed_id=" + Feed.getActiveFeedId());
            Log.d(TAG, url.toString());
            pd.show();
            new GetPostsTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private class GetPostsTask extends AsyncTask<URL, Void, ArrayList<Post>> {

        @Override
        protected ArrayList<Post> doInBackground(URL... url) {
            ArrayList<Post> posts = new ArrayList<>();
            HttpURLConnection httpConn = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String jsonStr = null;
            try {
                httpConn = (HttpURLConnection) url[0].openConnection();
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                InputStream inputStream = httpConn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        jsonStr = buffer.toString();
                        getPostDataFromJson(jsonStr, posts);
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            } finally {
                if (httpConn != null)
                    httpConn.disconnect();
            }
            Log.d(TAG, jsonStr);
            return posts;
        }

        private void getPostDataFromJson(String jsonStr, ArrayList<Post> posts) {
            // post information
            final String ID = "id";
            final String USER_ID = "user_id";
            final String USERNAME = "username";
            final String FEED_ID = "feed_id";
            final String MESSAGE = "message";
            final String LATITUDE = "latitude";
            final String LONGITUDE = "longitude";
            final String CREATED_TS = "created_ts";

            try {
                JSONArray userArray = new JSONArray(jsonStr);
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject user = userArray.getJSONObject(i);

                    int id = user.getInt(ID);
                    int userId = user.getInt(USER_ID);
                    String username = user.getString(USERNAME);
                    int feedId = user.getInt(FEED_ID);
                    String message = user.getString(MESSAGE);
                    double latitude = (!user.get(LATITUDE).toString().equals("null") ? user.getDouble(LATITUDE) : 0 );
                    double longitude = (!user.get(LONGITUDE).toString().equals("null") ? user.getDouble(LONGITUDE) : 0 );
                    Date ts = DateUtil.dateFromTimestamptz(user.getString(CREATED_TS));

                    Post postObj = new Post.Builder(id, userId, username, feedId, ts).message(message).latitude(latitude).longitude(longitude).build();
                    posts.add(postObj);
                }
                Log.d(TAG, userArray.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error ", e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            pd.dismiss();
            postList.clear();
            for (Post post : posts) {
                postList.add(post);
                Log.d(TAG, "Downloaded " + post);
            }
            adapter.notifyDataSetChanged();
        }

    }
}
