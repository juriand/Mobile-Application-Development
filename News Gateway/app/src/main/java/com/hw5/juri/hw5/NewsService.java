package com.hw5.juri.hw5;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String SOURCE = "SOURCE";
    private boolean running = true;

    private ServiceReceiver serviceReceiver;

    private ArrayList<News> newsList = new ArrayList<News>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(newsList.size() != 0){
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.ACTION_NEWS_STORY);
                        intent.putExtra(MainActivity.NEWS_LIST, new ArrayList<News>(newsList));
                        sendBroadcast(intent);
                        newsList.clear();
                    }
                }
                Log.d(TAG, "run: Ending loop");
            }
        }).start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<News> list){
        newsList.clear();
        newsList.addAll(list);
    }

    public void download(Intent intent){
        String tmpSource = "";
        if (intent.hasExtra(SOURCE)){
            NewsSource ns = (NewsSource) intent.getSerializableExtra(SOURCE);
            tmpSource = ns.getSourceId();
            new NewsArticleDownloader(NewsService.this).execute(tmpSource);
        }
    }

    //-----------------------------------Receiver-----------------
    class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case ACTION_MSG_TO_SERVICE:
                    download(intent);
                    break;
                default:
                    Log.d(TAG, "onReceive: Unkown broadcast received");
            }
        }
    }
}
