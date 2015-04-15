package com.example.asmuniz.trojanow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.ProgressBar;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asmuniz on 4/12/15.
 */
public class SignUpActivity extends Activity {

    private static final String TAG = "authenticate";

    private EditText usernameEditText;
    private EditText passwdEditText;
    private EditText emailEditText;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usernameEditText = (EditText) findViewById(R.id.etUserName);
        passwdEditText = (EditText) findViewById(R.id.etPass);
        emailEditText = (EditText) findViewById(R.id.etEmail);
        pd = new ProgressDialog(this);
        pd.setMessage("Signing Up");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
    }

    public void signUp(View view) {
        try {
            URL url = new URL("https://nameless-escarpment-8579.herokuapp.com/create_user");
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
                List<NameValuePair> params = new ArrayList<>(3);
                params.add(new BasicNameValuePair("username", usernameEditText.getText().toString()));
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
            pd.hide();
            Log.d(TAG, "user created...");
            Log.e(TAG, returnedData);
            if (returnedData.startsWith("Error")) {
                usernameEditText.getText().clear();
                emailEditText.getText().clear();
                passwdEditText.getText().clear();

                Log.e(TAG, "signup login");

                Context context = getApplicationContext();
                CharSequence text = "";
                if (returnedData.contains("email"))
                    text = "email already in use, try again";
                else if (returnedData.contains("username"))
                    text = "username already in use, try again";

                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {
                final String ID = "id";
                final String USERNAME = "username";
                final String EMAIL = "email";
                try {
                    JSONArray idArray = new JSONArray(returnedData);
                    JSONObject uid = idArray.getJSONObject(0);
                    int id = uid.getInt(ID);
                    Log.e(TAG, "id = " + id);
                    String username = uid.getString(USERNAME);
                    String email = uid.getString(EMAIL);
                    User user = new User(id, username, email);
                    User.setActiveUser(user);
                    startActivity(new Intent(SignUpActivity.this, PostListActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
