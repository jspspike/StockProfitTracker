package com.example.jspspike.stockprofittracker;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jspspike on 12/22/2016.
 */
public class Instance {
    private BigDecimal purchaseAmount;
    private int amount;

    public Instance(BigDecimal purchaseAmount, int amount) {
        this.purchaseAmount = purchaseAmount;
        this.amount = amount;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
