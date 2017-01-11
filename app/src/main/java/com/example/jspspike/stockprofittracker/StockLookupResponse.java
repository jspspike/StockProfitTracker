package com.example.jspspike.stockprofittracker;

import java.math.BigDecimal;

/**
 * Created by jspspike on 12/25/2016.
 */

public class StockLookupResponse {
    private BigDecimal price;
    private String name;

    public StockLookupResponse(BigDecimal price, String name) {
        this.price = price;
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
