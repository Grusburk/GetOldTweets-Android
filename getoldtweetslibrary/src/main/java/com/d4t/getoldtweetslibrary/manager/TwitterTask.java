package com.d4t.getoldtweetslibrary.manager;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mattias on 04/05/16.
 */
public class TwitterTask extends AsyncTask<String, Void, String> {

    private final String TAG = TwitterTask.class.getSimpleName();

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        StringBuilder html = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html.toString();
    }
}

