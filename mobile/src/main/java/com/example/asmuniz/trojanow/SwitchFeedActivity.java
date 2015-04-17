package com.example.asmuniz.trojanow;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;

import com.example.asmuniz.trojanow.obj.User;
import com.example.asmuniz.trojanow.obj.Feed;

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

/**
 * Created by asmuniz on 4/16/15.
 */
public class SwitchFeedActivity extends ListActivity {

    private ArrayList<Feed> feedList;
    private ArrayAdapter adapter;
    private ProgressDialog pd;

    private static final String TAG = "switch_feed";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        feedList = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading feeds");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        adapter = new ArrayAdapter<>(this, R.layout.feed_row_layout, R.id.line, feedList);
        setListAdapter(adapter);
        goGetData();
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int pos, long id) {
        Log.d(TAG, "switch to feed " + feedList.get(pos));
        Feed.setActiveFeed(feedList.get(pos));
        startActivity(new Intent(SwitchFeedActivity.this, PostListActivity.class));
        finish();
    }

    private void goGetData() {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/feeds_by_user_id?user_id=" + User.getActiveUser().getId());
            Log.d(TAG, url.toString());
            pd.show();
            new GetFeedsTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private class GetFeedsTask extends AsyncTask<URL, Void, ArrayList<Feed>> {

        @Override
        protected ArrayList<Feed> doInBackground(URL... url) {
            ArrayList<Feed> feeds = new ArrayList<>();
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
                        getPostDataFromJson(jsonStr, feeds);
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            } finally {
                if (httpConn != null)
                    httpConn.disconnect();
            }
            Log.d(TAG, jsonStr);
            return feeds;
        }

        private void getPostDataFromJson(String jsonStr, ArrayList<Feed> feeds) {
            // feed information
            final String ID = "feed_id";
            final String NAME = "name";

            try {
                JSONArray userArray = new JSONArray(jsonStr);
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject user = userArray.getJSONObject(i);

                    int id = user.getInt(ID);
                    String name = user.getString(NAME);

                    Feed feedObj = new Feed(id, name);
                    feeds.add(feedObj);
                }
                Log.d(TAG, userArray.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error ", e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Feed> feeds) {
            pd.dismiss();
            feedList.clear();
            for (Feed feed : feeds) {
                feedList.add(feed);
                Log.d(TAG, "Downloaded " + feed);
            }
            adapter.notifyDataSetChanged();
        }

    }
}
