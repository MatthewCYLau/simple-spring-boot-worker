package com.example.awsapp.yfinance.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

@Service
public class YahooFinanceService {

    private final RestClient restClient;

    public YahooFinanceService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://query1.finance.yahoo.com")
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build();
    }

    public BigDecimal getCurrentPrice(String symbol) {
        JsonNode response = restClient.get()
                .uri("/v8/finance/chart/{symbol}?range=1d&interval=1m", symbol)
                .retrieve()
                .body(JsonNode.class);

        if (response != null && response.has("chart")) {
            JsonNode meta = response.path("chart")
                    .path("result")
                    .get(0)
                    .path("meta");

            if (meta.has("regularMarketPrice")) {
                return new BigDecimal(meta.get("regularMarketPrice").asText());
            }
        }

        throw new RuntimeException("Could not fetch current price for symbol: " + symbol);
    }
}