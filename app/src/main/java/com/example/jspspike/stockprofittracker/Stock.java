package com.example.jspspike.stockprofittracker;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.concurrent.ExecutionException;

import yahoofinance.quotes.stock.StockQuote;


/**
 * Created by jspspike on 12/22/2016.
 */
public class Stock {

    private String stockSymbol;
    private String name;
    private ArrayList<Instance> instances = new ArrayList<>();
    private int totalAmount;
    private double money;


    public Stock(String stockSymbol, int amount, Calendar date) throws InvalidInputException {
        this.stockSymbol = stockSymbol;

        StockLookup lookup = new StockLookup();

        BigDecimal price = new BigDecimal(0);
        String name = "";

        try {

            StockLookupResponse response = lookup.execute(new StockDate(stockSymbol, date)).get();

            if (response == null)
                throw new InvalidInputException("Wrong Input");

            price = response.getPrice();
            name = response.getName();
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

        catch (ExecutionException e) {
            e.printStackTrace();
        }

        this.name = name;
        instances.add(new Instance(price , amount));
        money = 0;
        totalAmount = amount;
    }

    public void addInstance(int amount, Calendar date) throws InvalidInputException {
        StockLookup lookup = new StockLookup();

        BigDecimal price = new BigDecimal(0);

        try {

            StockLookupResponse response = lookup.execute(new StockDate(stockSymbol, date)).get();

            if (response == null)
                throw new InvalidInputException("Wrong Input");

            price = response.getPrice();
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

        catch (ExecutionException e) {
            e.printStackTrace();
        }

        instances.add(new Instance(price , amount));
        totalAmount += amount;
    }

    public void sellAmount(int amount) throws InvalidInputException {

        SingleQuoteLookup lookup = new SingleQuoteLookup();
        try {
            StockQuote currentQuote = lookup.execute(stockSymbol).get();
            if (currentQuote == null)
                throw new InvalidInputException("Wrong Symbol");
            money += currentQuote.getPrice().doubleValue() * amount;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        totalAmount -= amount;
        MainActivity.money += money;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public ArrayList<Instance> getInstances() {
        return instances;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getName() {
        return name;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }



}
