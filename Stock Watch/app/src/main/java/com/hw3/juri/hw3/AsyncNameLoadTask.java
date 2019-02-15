package com.hw3.juri.hw3;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncNameLoadTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "AsyncNameLoadTask";
    private MainActivity mainActivity;
    private static final String STOCK_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    public AsyncNameLoadTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri dataUri = Uri.parse(STOCK_URL);
        String dataUrl = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(dataUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> symbolList = parseJSON(s);
        mainActivity.createStock(symbolList);
    }

    private HashMap<String, String> parseJSON(String s) {
        HashMap<String, String> symbolList = new HashMap<String, String>();
        try {
            JSONArray jArray = new JSONArray(s);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jStock = (JSONObject) jArray.get(i);
                String symbol = jStock.getString("symbol");
                String name = jStock.getString("name");

                symbolList.put(symbol, name);
            }
            return symbolList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
