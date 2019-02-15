package com.hw3.juri.hw3;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncStockLoadTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "AsyncStockLoadTask";
    private MainActivity mainActivity;

    public AsyncStockLoadTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        String SYMBOL_URL = "https://api.iextrading.com/1.0/stock/"+strings[0]+"/quote?displayPercent=true";
        Uri dataUri = Uri.parse(SYMBOL_URL);
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
        Stock stock = parseJSON(s);
        mainActivity.updateStock(stock);
    }

    private Stock parseJSON(String s) {
        try {
            JSONObject jStock = new JSONObject(s);
            String symbol = jStock.getString("symbol");
            String company = jStock.getString("companyName");
            Double price = Double.parseDouble(jStock.getString("latestPrice"));
            Double change = Double.parseDouble(jStock.getString("change"));
            Double changePercent = Double.parseDouble(jStock.getString("changePercent"));

            Stock stock = new Stock(symbol,company,price,change,changePercent);
            return stock;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
