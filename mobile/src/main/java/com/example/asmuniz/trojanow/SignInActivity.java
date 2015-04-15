package com.example.asmuniz.trojanow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Context;
import android.widget.Toast;

import com.example.asmuniz.trojanow.obj.User;

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by asmuniz on 4/12/15.
 */
public class SignInActivity extends Activity {

    private static final String TAG = "login";

    private EditText passwdEditText;
    private EditText emailEditText;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        passwdEditText = (EditText) findViewById(R.id.etPass);
        emailEditText = (EditText) findViewById(R.id.etUserName);
        pd = new ProgressDialog(this);
        pd.setMessage("Signing In");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
    }

    public void signIn(View view) {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/login");
            Log.d(TAG, url.toString());
            pd.show();
            new NewUserTask().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    String returnedData;
    private class NewUserTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... url) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url[0].toString());
            try {
                List<NameValuePair> params = new ArrayList<>(2);
                params.add(new BasicNameValuePair("passwd", passwdEditText.getText().toString()));
                params.add(new BasicNameValuePair("email", emailEditText.getText().toString()));

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
            final String ID = "id";
            try {
                JSONArray idArray = new JSONArray(returnedData);
                if (idArray.length() == 0) {
                    passwdEditText.getText().clear();

                    Log.e(TAG, "failed login");

                    Context context = getApplicationContext();
                    CharSequence text = "Login failed, try again";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    JSONObject uid = idArray.getJSONObject(0);
                    int id = uid.getInt(ID);
                    Log.e(TAG, "id = " + id);
                    User.setActiveUserId(id);
                    startActivity(new Intent(SignInActivity.this, PostListActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
