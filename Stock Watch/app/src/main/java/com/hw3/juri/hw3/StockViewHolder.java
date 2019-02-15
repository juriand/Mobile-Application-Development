package com.hw3.juri.hw3;

import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder{
    public TextView symbol;
    public TextView company;
    public TextView price;
    public TextView priceChange;

    public StockViewHolder(View view) {
        super(view);

        symbol = view.findViewById(R.id.symbol);
        company = view.findViewById(R.id.company);
        price = view.findViewById(R.id.lastPrice);
        priceChange = view.findViewById(R.id.changeAmount);
    }
}
