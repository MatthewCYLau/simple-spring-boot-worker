package com.example.awsapp.yfinance.service.models;

import java.math.BigDecimal;

public class ShockedStockInfo extends StockInfo{

    private BigDecimal shockedMarketPrice;

    public ShockedStockInfo(String symbol, BigDecimal marketPrice, BigDecimal shockedMarketPrice) {
        super(symbol, marketPrice);
        this.shockedMarketPrice = shockedMarketPrice;
    }

    public BigDecimal getShockedMarketPrice() {
        return shockedMarketPrice;
    }

    public void setShockedMarketPrice(BigDecimal shockedMarketPrice) {
        this.shockedMarketPrice = shockedMarketPrice;
    }
}
