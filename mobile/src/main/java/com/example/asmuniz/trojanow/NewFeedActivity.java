package com.example.asmuniz.trojanow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asmuniz.trojanow.obj.Feed;
import com.example.asmuniz.trojanow.obj.User;
import com.example.asmuniz.trojanow.util.PostCenter;
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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asmuniz on 4/16/15.
 */
public class NewFeedActivity extends Activity {

    private static final String TAG = "new_feed";

    private TextView nameTv;
    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_new_feed);
        nameTv = (TextView) findViewById(R.id.editText2);
        pd = new ProgressDialog(this);
        pd.setMessage("Creating feed");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
    }

    public void createFeed(View view) {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/create_feed");
            Log.d(TAG, url.toString());
            pd.show();
            new CreateFeedTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    private class CreateFeedTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... url) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url[0].toString());
            String returnedData = "";
            try {
                List<NameValuePair> params = new ArrayList<>(2);
                params.add(new BasicNameValuePair("user_id", User.getActiveUser().getId()+""));
                params.add(new BasicNameValuePair("name", nameTv.getText().toString()));

                Log.d(TAG, "encode: " + new UrlEncodedFormEntity(params).toString());

                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = httpClient.execute(httpPost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                returnedData = reader.readLine();
            } catch (IOException ioe) {
                Log.e(TAG, "Error ", ioe);
            }
            return returnedData;
        }

        @Override
        protected void onPostExecute(String v) {
            pd.dismiss();
            Log.e(TAG, v);
            String ID = "id";
            try {
                if (!v.contains("id")) {

                    Log.e(TAG, "failed to create feed");

                    Context context = getApplicationContext();
                    CharSequence text = "Creation failed, try again";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    JSONObject uid = new JSONObject(v);
                    int id = uid.getInt(ID);
                    Log.e(TAG, "id = " + id);
                    Feed.setActiveFeed(new Feed(id, nameTv.getText().toString()));
                    startActivity(new Intent(NewFeedActivity.this, PostListActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
