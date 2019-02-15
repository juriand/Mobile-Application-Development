package com.hw5.juri.hw5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String NEWS_LIST = "NEWS_LIST";

    private Menu opt_menu;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayAdapter sourceAdapter;
    private PageAdapter pageAdapter;
    private ViewPager pager;
    private NewsReceiver newsReceiver;

    private HashMap<String, NewsSource> sourcesData = new HashMap<>();
    private ArrayList<NewsSource> sourceList = new ArrayList<NewsSource>();
    private ArrayList<String> categoryList = new ArrayList<String>();;
    private ArrayList<String> sNameList = new ArrayList<String>();
    private ArrayList<News> newsList = new ArrayList<News>();
    private List<Fragment> fragments;

    private String curCategory = "all";
    private String curSource;
    private int curFragment = 0;
    private boolean isRestore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(MainActivity.this, NewsService.class);
        startService(serviceIntent);

        newsReceiver = new NewsReceiver();

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);
        sourceAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sNameList);
        drawerList.setAdapter(sourceAdapter);
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(doNetCheck()){
                            pager.setBackground(null);
                            NewsSource ns = sourcesData.get(sNameList.get(position));
                            curFragment = 0;
                            curSource = ns.getSourceName();
                            sendMessage(ns);
                            drawerLayout.closeDrawer(drawerList);
                        }else{
                            Toast.makeText(MainActivity.this,"No network connection!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);

        fragments = new ArrayList<>();
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if(doNetCheck()) {
            if(categoryList.size() == 0){
                new NewsSourceDownloader(this).execute("all");
            }
        }else{
            Toast.makeText(this,"No network connection!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);

//        if(doNetCheck()){
//            if(categoryList == null){
//                new NewsSourceDownloader(this).execute("all");
//            }
//        }else{
//            Toast.makeText(this,"No network connection!",Toast.LENGTH_SHORT).show();
//        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(newsReceiver);
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CUR_CATEGORY",curCategory);
        outState.putString("CUR_SOURCE",curSource);
        curFragment = pager.getCurrentItem();
        outState.putInt("CUR_FRAGMENT",curFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        curCategory = savedInstanceState.getString("CUR_CATEGORY");
        curSource = savedInstanceState.getString("CUR_SOURCE");
        curFragment = savedInstanceState.getInt("CUR_FRAGMENT");

        // Restore
        isRestore = true;
        if(doNetCheck()){
            new NewsSourceDownloader(this).execute(curCategory);
        }else{
            Toast.makeText(this,"No network connection!",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(doNetCheck()){
            curCategory = item.getTitle()+"";
            new NewsSourceDownloader(this).execute(item.getTitle()+"");
        }else{
            Toast.makeText(this,"No network connection!",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        opt_menu = menu;
        if(categoryList.size() > 1){
            for(String s:categoryList){
                opt_menu.add(s);
            }
        }
        return true;
    }

    public void sendMessage(NewsSource ns){
        setTitle(ns.getSourceName());
        Intent intent = new Intent();
        intent.setAction(NewsService.ACTION_MSG_TO_SERVICE);
        intent.putExtra(NewsService.SOURCE, ns);
        sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSourceList(ArrayList<NewsSource> slist, ArrayList<String> clist){
        sourcesData.clear();
        sourceList.clear();
        sNameList.clear();
        sourceList.addAll(slist);

        for(NewsSource ns:slist){
            sourcesData.put(ns.getSourceName(),ns);
        }
        sNameList.addAll(new ArrayList<String>(sourcesData.keySet()));
        Collections.sort(sNameList);

        if(categoryList.size() == 0){
            categoryList.addAll(clist);
            categoryList.add(0,"all");
        }
        if(opt_menu != null && opt_menu.size() == 0){
            for(String s:categoryList){
                opt_menu.add(s);
            }
        }
        sourceAdapter.notifyDataSetChanged();

        if(isRestore && curSource != null){
            pager.setBackground(null);
            sendMessage(sourcesData.get(curSource));
            isRestore = false;
        }
    }

    public void reDoFragments(ArrayList<News> list,int cur){
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);
        fragments.clear();

        for (int i = 0; i < list.size(); i++) {
            fragments.add(
                    NewsFragment.newInstance(list.get(i), i+1, list.size()));
        }
        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(cur);
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //-----------------------------------Receiver-----------------
    class NewsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            switch (action) {
                case ACTION_NEWS_STORY:
                    if (intent.hasExtra(NEWS_LIST)){
                        newsList = (ArrayList<News>) intent.getSerializableExtra(NEWS_LIST);
                    }
                    reDoFragments(newsList, curFragment);
                    break;
                default:
                    Log.d(TAG, "onReceive: Unkown broadcast received");
            }
        }

    }

    //-----------------------------Adapter----------------------------
    private class PageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }
}
