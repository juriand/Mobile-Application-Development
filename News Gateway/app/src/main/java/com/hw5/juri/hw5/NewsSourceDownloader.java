package com.hw5.juri.hw5;

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
import java.util.ArrayList;
import java.util.HashMap;

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NewsSourceDownloader";
    private static final String APP_KEY = "921d1b3eaa45465aa73fee80f429e79a";
    private MainActivity mainActivity;
    public NewsSourceDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        String SOURCE_URL = "";
        if(strings[0].equals("all")){
            SOURCE_URL = "https://newsapi.org/v2/sources?language=en&country=us&apiKey=" + APP_KEY;
        }else{
            SOURCE_URL = "https://newsapi.org/v2/sources?language=en&country=us&category=" + strings[0] + "&apiKey=" + APP_KEY;
        }

        Uri dataUri = Uri.parse(SOURCE_URL);
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
        ArrayList<NewsSource> sourceList = parseJSON(s);
        ArrayList<String> categoryList;

        HashMap<String,NewsSource> categoryMap = new HashMap<String,NewsSource>();
        for(NewsSource ns:sourceList){
            if(!categoryMap.containsKey(ns.getCategory())){
                categoryMap.put(ns.getCategory(),ns);
            }
        }
        categoryList = new ArrayList<String>(categoryMap.keySet());
        mainActivity.setSourceList(sourceList, categoryList);
    }

    private ArrayList<NewsSource> parseJSON(String s) {
        try {
            ArrayList<NewsSource> sourceList = new ArrayList<NewsSource>();

            JSONObject jObj = new JSONObject(s);
            JSONArray jSource = jObj.getJSONArray("sources");
            for(int i=0;i<jSource.length();i++){
                String sourceId = ((JSONObject)jSource.get(i)).getString("id");
                String sourceName = ((JSONObject)jSource.get(i)).getString("name");
                String sourceCategory = ((JSONObject)jSource.get(i)).getString("category");

                NewsSource source = new NewsSource(sourceId, sourceName, sourceCategory);
                sourceList.add(source);
            }
            return sourceList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
