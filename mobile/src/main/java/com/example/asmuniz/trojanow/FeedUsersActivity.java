package com.example.asmuniz.trojanow;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

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
 * Created by asmuniz on 4/17/15.
 */
public class FeedUsersActivity extends ListActivity {

    private static final String TAG = "feed_users";
    private ArrayList<String> usernameList;
    private ArrayAdapter<String> adapter;
    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle("Users in feed (" + Feed.getActiveFeed().getName() + " )");
        pd = new ProgressDialog(this);
        pd.setMessage("Loading users");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        usernameList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.feed_row_layout, R.id.line, usernameList);
        setListAdapter(adapter);
        getFeedUsers();
    }

    private void getFeedUsers() {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/users_by_feed_id?feed_id=" + Feed.getActiveFeed().getId());
            Log.d(TAG, url.toString());
            pd.show();
            new GetUsersTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private class GetUsersTask extends AsyncTask<URL, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(URL... url) {
            ArrayList<String> usernames = new ArrayList<>();
            HttpURLConnection httpConn = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String result = null;
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
                        result = buffer.toString();
                        getPostDataFromJson(result, usernames);
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            } finally {
                if (httpConn != null)
                    httpConn.disconnect();
            }
            Log.d(TAG, result);
            return usernames;
        }

        private void getPostDataFromJson(String jsonStr, ArrayList<String> usernames) {
            final String USERNAME = "username";
            try {
                JSONArray userArray = new JSONArray(jsonStr);
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject user = userArray.getJSONObject(i);
                    usernames.add(user.getString(USERNAME));
                }
                Log.d(TAG, userArray.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error ", e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> usernames) {
            pd.dismiss();
            usernameList.clear();
            for (String str : usernames) {
                usernameList.add(str);
                Log.d(TAG, "Downloaded " + str);
            }
            adapter.notifyDataSetChanged();
        }

    }
}
