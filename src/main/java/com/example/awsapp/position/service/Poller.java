package com.example.awsapp.position.service;
import com.example.awsapp.yfinance.service.YahooFinanceService;
import lombok.extern.slf4j.Slf4j;
import com.example.awsapp.position.models.Position;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class Poller {

    private final DynamoDbTable<Position> table;
    private final YahooFinanceService yahooFinanceService;

    public Poller(DynamoDbEnhancedClient enhancedClient, YahooFinanceService yahooFinanceService) {
        this.table = enhancedClient.table("stock_trading_positions", TableSchema.fromBean(Position.class));
        this.yahooFinanceService = yahooFinanceService;
    }

    @Scheduled(fixedDelay = 60_000)
    public void pollDynamoDb() {
        try {
            table.scan()
                    .items()
                    .forEach(item -> {
                        if (item != null) {
                            log.info("Processing Position ID: {}, Symbol: {}", item.getPositionId(), item.getStockSymbol());
                        }
                    });

        } catch (Exception e) {
            System.err.println("Error reading from DynamoDB: " + e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 60_000)
    public void getStockPrices(){
        List<String> tickers = List.of("AAPL", "MSFT");
        tickers.forEach((stockSymbol) -> {
            BigDecimal stockPrice = yahooFinanceService.getCurrentPrice(stockSymbol);
            log.info("Current {} stock price: {}", stockSymbol, stockPrice);
        });
    }
}