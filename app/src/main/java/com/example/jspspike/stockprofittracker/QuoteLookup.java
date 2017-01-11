package com.example.jspspike.stockprofittracker;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Map;

import yahoofinance.*;
import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by jspspike on 12/25/2016.
 */

public class QuoteLookup extends AsyncTask<String[], Void, ArrayList<StockQuote>> {

    @Override
    protected ArrayList<StockQuote> doInBackground(String[]... params) {
        String[] symbols = params[0];

        ArrayList<StockQuote> quotes = new ArrayList<>();

        try {
            Map<String, Stock> stocks = YahooFinance.get(symbols);
            for (int i = 0; i < symbols.length; i++) {
                quotes.add(stocks.get(symbols[i]).getQuote());
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return quotes;
    }
}
