package com.example.asmuniz.trojanow;

import com.example.asmuniz.trojanow.obj.User;
import com.example.asmuniz.trojanow.util.DateUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.exceptions.PushyException;

/**
 * Created by asmuniz on 4/12/15.
 */
public class PostListActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "post_list";

    private static boolean isActive;

    private static Set<Integer> residentPosts;
    private static List<Post> postList;
    private static ArrayAdapter<Post> adapter;
    private ProgressDialog pd;

    private GPSTracker gps;

    private SwipeRefreshLayout swipeLayout;
    private ListView listView;

    public static boolean containsPost(int postId) {
        return residentPosts.contains(postId);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_post_feed_layout);

        int feedId = getIntent().getIntExtra("feedId", Feed.getActiveFeed().getId());
        if (feedId != Feed.getActiveFeed().getId()) {
            if (feedId == 1)
                Feed.setToPublicFeed();
            else
                Feed.setActiveFeed(new Feed(feedId, ""));
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listView = (ListView) findViewById(R.id.list);

        residentPosts = new HashSet<>();

        //------------------------------
        // Restart the socket service,
        // in case the user force-closed
        //------------------------------

        Pushy.listen(this);

        //------------------------------
        // Register up for push notifications
        // (will return existing token
        // if already registered before)
        //------------------------------

        new RegisterForPushNotifications().execute();

        if (Post.getRadiusInMiles() != 0)
            setTitle(" ( " + Feed.getActiveFeed().getName() + ", radius = " + Post.getRadiusInMiles() + "mi )");
        else
            setTitle(" ( " + Feed.getActiveFeed().getName() + " )");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading posts");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);

        gps = new GPSTracker(PostListActivity.this);

        Log.d(TAG, "in post list onCreate");

        postList = new ArrayList<>();
        // use your custom layout
        adapter = new PostListAdapter(this, R.layout.post_row_layout, postList);
        listView.setAdapter(adapter);
        goGetData();
    }

    public static boolean isActive() {
        return isActive;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "in onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        if (Feed.getActiveFeed().getId() == 1) {
            menu.removeItem(R.id.action_share_feed);
            menu.removeItem(R.id.action_feed_users);
        }

        if (!gps.canGetLocation())
            menu.removeItem(R.id.action_posts_near_me);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_new_post:
                startActivity(new Intent(PostListActivity.this, NewPostActivity.class));
                return true;
            case R.id.action_new_feed:
                startActivity(new Intent(PostListActivity.this, NewFeedActivity.class));
                return true;
            case R.id.action_change_feed:
                startActivity(new Intent(PostListActivity.this, SwitchFeedActivity.class));
                return true;
            case R.id.action_share_feed:
                startActivity(new Intent(PostListActivity.this, ShareFeedActivity.class));
                return true;
            case R.id.action_feed_users:
                startActivity(new Intent(PostListActivity.this, FeedUsersActivity.class));
                return true;
            case R.id.action_posts_near_me:
                startActivity(new Intent(PostListActivity.this, LocalPostsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        User.setActiveUser(null);
        Feed.setToPublicFeed();
        Post.setRadiusInMiles(0);
        startActivity(new Intent(PostListActivity.this, MainActivity.class));
        finish();
    }

    private void goGetData() {
        try {
            String req = "";
            if (Post.getRadiusInMiles() != 0 && gps.canGetLocation())
                req = "https://nameless-escarpment-8579.herokuapp.com/get_local_posts?feed_id=" + Feed.getActiveFeed().getId() + "&radius=" + Post.getRadiusInMiles() + "&lat=" + gps.getLatitude() + "&lon=" + gps.getLongitude();
            else
                req = "https://nameless-escarpment-8579.herokuapp.com/get_posts_by_feed?feed_id=" + Feed.getActiveFeed().getId();
            URL url = new URL(req);
            Log.d(TAG, url.toString());
            if (!swipeLayout.isRefreshing())
                pd.show();
            new GetPostsTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void onRefresh() {
       goGetData();
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
            residentPosts.clear();
            for (Post post : posts) {
                postList.add(post);
                residentPosts.add(post.getId());
                Log.d(TAG, "Downloaded " + post);
            }
            adapter.notifyDataSetChanged();
            if (swipeLayout.isRefreshing())
                swipeLayout.setRefreshing(false);
        }

    }

    private class RegisterForPushNotifications extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            //------------------------------
            // Temporary string containing
            // the registration ID result
            //------------------------------

            String result = "";

            //------------------------------
            // Get registration ID via Pushy
            //------------------------------

            try
            {
                result = Pushy.register(PostListActivity.this);
            }
            catch (PushyException exc)
            {
                //------------------------------
                // Show error instead
                //------------------------------

                result = exc.getMessage();
            }

            //------------------------------
            // Write to log
            //------------------------------

            Log.d("Pushy", "Registration result: " + result);

            //------------------------------
            // Return result
            //------------------------------

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            //------------------------------
            // Activity died?
            //------------------------------

            if ( isFinishing() )
            {
                return;
            }
        }
    }
}
