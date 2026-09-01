package com.example.awsapp.yfinance.service.models;

import java.math.BigDecimal;

public class StockInfo {
    private String symbol;
    private BigDecimal marketPrice;

    public StockInfo(String symbol, BigDecimal marketPrice) {
        this.symbol = symbol;
        this.marketPrice = marketPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }
}
