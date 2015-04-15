package com.example.asmuniz.trojanow.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Provides authentication interface to the server.
 */
public class Authenticate {



    private Authenticate() {}

    /**
     * sign up a new user to the server
     * @param username
     * @param email
     * @param passwd
     * @return the newly created user's id
     */
    public static int signUp(String username, String passwd, String email) {
        return 0;
    }

    /**
     * logs user into the system
     * @param username
     * @param passwd
     * @return the logged in user's id
     */
    public static int login(String username, String passwd) {
        return 0;
    }


}
