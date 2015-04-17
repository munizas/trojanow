package com.example.asmuniz.trojanow;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.asmuniz.trojanow.obj.Feed;
import com.example.asmuniz.trojanow.util.UserSelectionModel;
import com.example.asmuniz.trojanow.util.InteractiveArrayAdapter;
import com.example.asmuniz.trojanow.obj.User;

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
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by asmuniz on 4/16/15.
 */
public class ShareFeedActivity extends ListActivity {

    private static final String TAG = "share_feed";

    private ProgressDialog pd;
    private ArrayList<UserSelectionModel> userSelectionModels;
    private ArrayAdapter<UserSelectionModel> adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setTitle("Add Users to Feed ( " + Feed.getActiveFeed().getName() + " )");
        setContentView(R.layout.activity_share_feed);

        Log.d(TAG, "in share feed list onCreate");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading users");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);

        userSelectionModels = new ArrayList<>();
        adapter = new InteractiveArrayAdapter(this, userSelectionModels);
        setListAdapter(adapter);
        goGetUsers();
    }

    public void shareFeed(View view) {
        try {
            StringBuilder urlString = new StringBuilder("https://nameless-escarpment-8579.herokuapp.com/share_feed_with_multiple?feed_id=" + Feed.getActiveFeed().getId());
            for (UserSelectionModel usm : userSelectionModels) {
                if (usm.isSelected()) {
                    urlString.append("&user_ids=" + usm.getUser().getId());
                }
            }
            URL url = new URL(urlString.toString());
            Log.d(TAG, url.toString());
            pd.show();
            new ShareFeedTask().execute(url);

        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private void goGetUsers() {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/users");
            Log.d(TAG, url.toString());
            pd.setMessage("Sharing feed with selected users");
            pd.show();
            new GetUsersTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private class GetUsersTask extends AsyncTask<URL, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(URL... url) {
            ArrayList<User> users = new ArrayList<>();
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
                        getPostDataFromJson(jsonStr, users);
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            } finally {
                if (httpConn != null)
                    httpConn.disconnect();
            }
            Log.d(TAG, jsonStr);
            return users;
        }

        private void getPostDataFromJson(String jsonStr, ArrayList<User> users) {
            // feed information
            final String ID = "id";
            final String USERNAME = "username";
            final String EMAIL = "email";

            try {
                JSONArray userArray = new JSONArray(jsonStr);
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject user = userArray.getJSONObject(i);

                    int id = user.getInt(ID);
                    String username = user.getString(USERNAME);
                    String email = user.getString(EMAIL);

                    User userObj = new User(id, username, email);
                    users.add(userObj);
                }
                Log.d(TAG, userArray.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error ", e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            pd.dismiss();
            userSelectionModels.clear();
            for (User user : users)
                userSelectionModels.add(new UserSelectionModel(user));
            adapter.notifyDataSetChanged();
        }

    }

    private class ShareFeedTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... url) {
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
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            } finally {
                if (httpConn != null)
                    httpConn.disconnect();
            }
            Log.d(TAG, result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            if (result.contains("done")) {
                startActivity(new Intent(ShareFeedActivity.this, PostListActivity.class));
                finish();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Sharing failed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}
