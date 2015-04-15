package com.example.asmuniz.trojanow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asmuniz.trojanow.obj.Feed;
import com.example.asmuniz.trojanow.obj.User;
import com.example.asmuniz.trojanow.util.DateUtil;
import com.example.asmuniz.trojanow.util.GPSTracker;
import com.example.asmuniz.trojanow.util.PostCenter;
import com.example.asmuniz.trojanow.obj.Post;
import com.example.asmuniz.trojanow.util.SensorCenter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

/**
 * Created by asmuniz on 4/14/15.
 */
public class NewPostActivity extends Activity {

    private static final String TAG = "new_post";

    private CheckBox anonymous;
    private EditText message;
    private TextView feedName;

    private ProgressDialog pd;
    private GPSTracker gps;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_new_post);
        message = (EditText) findViewById(R.id.editText);
        anonymous = (CheckBox) findViewById(R.id.checkBox);
        feedName = (TextView) findViewById(R.id.textView2);
        feedName.setText(feedName.getText() + Feed.getActiveFeed().getName());

        pd = new ProgressDialog(this);
        pd.setMessage("Creating post");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);

        gps = new GPSTracker(NewPostActivity.this);
    }

    public void createPost(View view) {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/create_post");
            Log.d(TAG, "sData = " + SensorCenter.getInstance().getData(this, SensorCenter.Data.PRESSURE));
            Log.d(TAG, url.toString());
            pd.show();
            new CreatePostTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private class CreatePostTask extends AsyncTask<URL, Void, String> {

        String returnedData;
        @Override
        protected String doInBackground(URL... url) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url[0].toString());
            try {
                List<NameValuePair> params = new ArrayList<>(6);
                params.add(new BasicNameValuePair("user_id", User.getActiveUser().getId()+""));

                if (anonymous.isChecked())
                    params.add(new BasicNameValuePair("username", "anonymous"));
                else
                    params.add(new BasicNameValuePair("username", User.getActiveUser().getUsername()));

                params.add(new BasicNameValuePair("message", message.getText().toString()));
                params.add(new BasicNameValuePair("feed_id", Feed.getActiveFeed().getId()+""));

                if (gps.canGetLocation()) {
                    params.add(new BasicNameValuePair("latitude", gps.getLatitude()+""));
                    params.add(new BasicNameValuePair("longitude", gps.getLongitude()+""));
                }
                else {
                    params.add(new BasicNameValuePair("latitude", ""));
                    params.add(new BasicNameValuePair("longitude", ""));
                }

                Log.d(TAG, "encode: " + new UrlEncodedFormEntity(params).toString());

                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                returnedData = reader.readLine();
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String v) {
            pd.dismiss();
            Log.e(TAG, returnedData);
            String ID = "id";
            try {
                JSONArray idArray = new JSONArray(returnedData);
                if (idArray.length() == 0) {

                    Log.e(TAG, "failed to create post");

                    Context context = getApplicationContext();
                    CharSequence text = "Creation failed, try again";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    JSONObject uid = idArray.getJSONObject(0);
                    int id = uid.getInt(ID);
                    Log.e(TAG, "id = " + id);
                    startActivity(new Intent(NewPostActivity.this, PostListActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
