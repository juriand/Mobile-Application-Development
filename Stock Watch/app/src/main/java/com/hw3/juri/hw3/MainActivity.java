package com.hw3.juri.hw3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    private static final String TAG = "MainActivity";

    private List<Stock> stockList = new ArrayList<>();
    private HashMap<String, String> stockSymbolList = new HashMap<String, String>();
    private HashMap<String, String> dbList = new HashMap<String, String>();

    private RecyclerView recyclerView;
    private StockAdapter mAdapter;
    private SwipeRefreshLayout swiper;

    private DatabaseHandler databaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swiper = findViewById(R.id.swiper);
        recyclerView = findViewById(R.id.recycler);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(doNetCheck()){
                    doRefresh();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("No Network Connection");
                    builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                swiper.setRefreshing(false);
            }
        });

        mAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHandler = new DatabaseHandler(this);
        downloadStocks();
        databaseHandler.dumpDbToLog();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add:
                if(doNetCheck()){
                    addClick();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("No Network Connection");
                    builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void downloadStocks(){
        dbList = databaseHandler.loadStocks();
        stockList.clear();

        if(doNetCheck()){
            new AsyncNameLoadTask(this).execute();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();

            for(Map.Entry<String, String> entry:dbList.entrySet()){
                Stock s = new Stock(entry.getKey(),entry.getValue(),0,0,0);
                stockList.add(s);
            }
            Collections.sort(stockList);
            mAdapter.notifyDataSetChanged();
        }
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

    private void doRefresh() {
        HashMap<String, String> list = databaseHandler.loadStocks();
        stockList.clear();
        for(Map.Entry<String, String> entry:list.entrySet()){
            new AsyncStockLoadTask(this).execute(entry.getKey());
        };
        Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show();
    }

    public void addClick(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.add_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setTitle("Stock Selection");
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText inputSymbol = view.findViewById(R.id.inputSymbol);

                String symbol = inputSymbol.getText().toString().trim();
                if(stockSymbolList.containsKey(symbol)){
                    //Found
                    if(fuzzySearch(symbol).size() > 1){
                        //Select from multiple results
                        selectStock(fuzzySearch(symbol));
                    }else{
                        if(databaseHandler.queryStock(symbol)){
                            //Duplicate
                            duplicate(symbol);
                        }else{
                            //Only one stock
                            new AsyncStockLoadTask(MainActivity.this).execute(symbol);
                        }
                    }
                }else{
                    //Not found symbol
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Data for stock symbol");
                    builder.setTitle("Symbol Not Found:"+symbol);
                    AlertDialog warndialog = builder.create();
                    warndialog.show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
    }

    public void duplicate(String symbol){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Stock Symbol " + symbol + " is already displayed");
        builder.setTitle("Duplicate Stock");
        builder.setIcon(R.drawable.baseline_report_problem_black_48);
        AlertDialog warndialog = builder.create();
        warndialog.show();

    }

    public void selectStock(final ArrayList<Stock> result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        //Tranfer to array
        final String[] resultArray = new String[result.size()];
        for(int i=0;i<result.size();i++){
            resultArray[i] = result.get(i).getSymbol()+" - "+result.get(i).getCompany();
        }

        builder.setItems(resultArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String tempSymbol = result.get(which).getSymbol();
                if(databaseHandler.queryStock(tempSymbol)) {
                    duplicate(tempSymbol);
                }else{
                    new AsyncStockLoadTask(MainActivity.this).execute(tempSymbol);
                }
            }
        });

        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ArrayList<Stock> fuzzySearch(String symbol){
        ArrayList<Stock> resultList = new ArrayList<Stock>();
        for(Map.Entry<String, String> entry:stockSymbolList.entrySet()){
            if(entry.getKey().contains(symbol) || entry.getValue().contains(symbol)){
                resultList.add(new Stock(entry.getKey(),entry.getValue()));
            }
        }
        Collections.sort(resultList);
        return resultList;
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);
        String stockURL = "http://www.marketwatch.com/investing/stock/"+s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(stockURL));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        final Stock s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Stock Symbol "+s.getSymbol()+"?");
        builder.setIcon(R.drawable.baseline_delete_outline_black_48);
        builder.setTitle("Delete Stock");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                databaseHandler.deleteStock(s.getSymbol());
                stockList.remove(s);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    public void createStock(HashMap<String, String> symbolList) {
        stockSymbolList.putAll(symbolList);

        //Download financial data
        for(Map.Entry<String, String> entry:dbList.entrySet()){
            new AsyncStockLoadTask(this).execute(entry.getKey());
        }
    }

    public void updateStock(Stock stock) {
        stockList.add(stock);
        databaseHandler.addStock(stock);
        Collections.sort(stockList);
        mAdapter.notifyDataSetChanged();
    }
}
