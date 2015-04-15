package com.example.asmuniz.trojanow.util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.asmuniz.trojanow.obj.Feed;
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

/**
 * Created by asmuniz on 3/30/15.
 *
 * Contains common operations that will be perform regarding Users.
 */
public class UserCenter {

    private static final String TAG = "user_center";

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        return users;
    }

    public static List<User> getUsersByFeed(Feed feed) {
        // query for users by feed (UserFeed table)
        List<User> users = new ArrayList<>();
        return users;
    }

    public static void createUser(String firstName, String lastName, String username) {
        try {
            URL url = new URL("http://192.168.1.8/~asmuniz/users/adduser.php?first_name=" + firstName +
                    "&last_name=" + lastName +
                    "&username=" + username);
            Log.d(TAG, url.toString());
            new CreateUserTask().execute(url);
        } catch (MalformedURLException e) {

        }
    }

    private static class CreateUserTask extends AsyncTask<URL, Void, User> {

        @Override
        protected User doInBackground(URL... url) {
            User newUser = null;
            HttpURLConnection httpConn = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
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
                        forecastJsonStr = buffer.toString();
                        newUser = getUserDataFromJson(forecastJsonStr);
                    }
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            } finally {
                if (httpConn != null)
                    httpConn.disconnect();
            }
            Log.d(TAG, forecastJsonStr);
            return newUser;
        }

        private User getUserDataFromJson(String jsonStr) {
            User userObj = null;
            // user information
            final String ID = "id";
            final String FIRST_NAME = "first_name";
            final String LAST_NAME = "last_name";
            final String USERNAME = "username";

            try {
                JSONArray userArray = new JSONArray(jsonStr);
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject user = userArray.getJSONObject(i);

                    int id = user.getInt(ID);
                    String fname = user.getString(FIRST_NAME);
                    String lname = user.getString(LAST_NAME);
                    String uname = user.getString(USERNAME);

                    //userObj = new User(id, fname, lname, uname);
                }
                Log.d(TAG, userArray.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Error ", e);
            }
            return userObj;
        }

        @Override
        protected void onPostExecute(User result) {
            Log.d(TAG, "Created " + result);
        }

    }
}
