package com.hw3.juri.hw3;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable,Comparable<Stock>{
    private String symbol;
    private String company;
    private double price;
    private double priceChange;
    private double changePercentage;

    public Stock(){

    }

    public Stock(String symbol, String company, double price, double priceChange, double changePercentage) {
        this.symbol = symbol;
        this.company = company;
        this.price = price;
        this.priceChange = priceChange;
        this.changePercentage = changePercentage;
    }

    public Stock(String symbol, String company) {
        this.symbol = symbol;
        this.company = company;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }

    @Override
    public int compareTo(@NonNull Stock o) {
       if(symbol.equals(o.getSymbol())){
           return company.compareTo(o.getCompany());
       }else{
           return symbol.compareTo(o.getSymbol());
       }
    }
}
