package com.example.jspspike.stockprofittracker;

import android.os.AsyncTask;

import yahoofinance.*;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by jspspike on 12/27/2016.
 */

public class SingleQuoteLookup extends AsyncTask<String, Void, StockQuote> {
    @Override
    protected StockQuote doInBackground(String... params) {
        String symbol = params[0];
        StockQuote quote;


        try {
            yahoofinance.Stock stock = YahooFinance.get(symbol);
            quote = stock.getQuote();
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return quote;
    }
}
