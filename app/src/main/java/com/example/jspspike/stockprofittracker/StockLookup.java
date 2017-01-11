package com.example.jspspike.stockprofittracker;

import android.os.AsyncTask;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import yahoofinance.*;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 * Created by jspspike on 12/24/2016.
 */

public class StockLookup extends AsyncTask<StockDate, Void, StockLookupResponse> {
    @Override
    protected StockLookupResponse doInBackground(StockDate... params) {
        StockDate stockDate = params[0];

        BigDecimal price;
        String name;

        Calendar closeInterval = stockDate.getDate();

        try {
            yahoofinance.Stock stock = YahooFinance.get(stockDate.getSymbol(), true);
            List<HistoricalQuote> stockHistory = stock.getHistory(stockDate.getDate(), closeInterval, Interval.DAILY);
            price = stockHistory.get(0).getClose();
            name = stock.getName();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }



        return new StockLookupResponse(price, name);
    }
}
