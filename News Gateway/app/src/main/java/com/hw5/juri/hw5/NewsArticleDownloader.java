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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NewsArticleDownloader";
    private String APP_KEY = "921d1b3eaa45465aa73fee80f429e79a";
    private NewsService newsService;
    public NewsArticleDownloader(NewsService ns) {
        newsService = ns;
    }

    @Override
    protected String doInBackground(String... strings) {
        String ARTICLE_URL = "https://newsapi.org/v2/top-headlines?sources=" + strings[0] + "&apiKey=" + APP_KEY;
        Uri dataUri = Uri.parse(ARTICLE_URL);
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
        ArrayList<News> newsList = parseJSON(s);
        newsService.setArticles(newsList);
    }

    private ArrayList<News> parseJSON(String s) {
        try {
            SimpleDateFormat fm1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat fm2 = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

            ArrayList<News> newsList = new ArrayList<News>();
            JSONObject jObj = new JSONObject(s);
            JSONArray jArticle = jObj.getJSONArray("articles");
            for(int i=0;i<jArticle.length();i++){
                String author = ((JSONObject)jArticle.get(i)).getString("author");
                String title = ((JSONObject)jArticle.get(i)).getString("title");
                String description = ((JSONObject)jArticle.get(i)).getString("description");
                String url = ((JSONObject)jArticle.get(i)).getString("url");
                String urlToImage = ((JSONObject)jArticle.get(i)).getString("urlToImage");
                String publishedAt = ((JSONObject)jArticle.get(i)).getString("publishedAt");
                StringTokenizer st1 = new StringTokenizer(publishedAt,"T");
                Date date = fm1.parse(st1.nextToken());

                StringTokenizer st2 = new StringTokenizer(st1.nextToken(),":");
                String time = st2.nextToken()+":"+st2.nextToken();
                String publishTime = fm2.format(date) + " " + time;

                News news = new News(author, title, description, url, urlToImage, publishTime);
                newsList.add(news);
            }
            return newsList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
