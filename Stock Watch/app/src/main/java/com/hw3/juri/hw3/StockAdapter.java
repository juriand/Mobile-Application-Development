package com.hw3.juri.hw3;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private List<Stock> stockList;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        mainAct = ma;
    }

    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_item, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int i) {
        DecimalFormat df = new DecimalFormat("0.00");
        Stock stock = stockList.get(i);
        holder.symbol.setText(stock.getSymbol());
        holder.company.setText(stock.getCompany());
        holder.price.setText(df.format(stock.getPrice())+"");

        if(stock.getPriceChange() > 0){
            holder.symbol.setTextColor(Color.GREEN);
            holder.company.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.priceChange.setText("▲" + df.format(stock.getPriceChange())+" ("+df.format(stock.getChangePercentage())+"%)");
            holder.priceChange.setTextColor(Color.GREEN);
        }else if(stock.getPriceChange() < 0){
            holder.symbol.setTextColor(Color.RED);
            holder.company.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.priceChange.setText("▼" + df.format(stock.getPriceChange())+" ("+df.format(stock.getChangePercentage())+"%)");
            holder.priceChange.setTextColor(Color.RED);
        }else{
            holder.symbol.setTextColor(Color.WHITE);
            holder.company.setTextColor(Color.WHITE);
            holder.price.setTextColor(Color.WHITE);
            holder.priceChange.setText(df.format(stock.getPriceChange())+" ("+df.format(stock.getChangePercentage())+"%)");
            holder.priceChange.setTextColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
