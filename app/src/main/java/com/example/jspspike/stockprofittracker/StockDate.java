package com.example.jspspike.stockprofittracker;

import java.util.Calendar;

/**
 * Created by jspspike on 12/24/2016.
 */

public class StockDate {
    private String symbol;
    private Calendar date;

    public StockDate(String symbol, Calendar date) {
        this.symbol = symbol;
        this.date = date;
    }

    public String getSymbol() {
        return symbol;
    }

    public Calendar getDate() {
        return date;
    }
}
