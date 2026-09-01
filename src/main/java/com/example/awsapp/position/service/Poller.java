package com.example.awsapp.position.service;
import com.example.awsapp.yfinance.service.YahooFinanceService;
import com.example.awsapp.yfinance.service.models.StockInfo;
import lombok.extern.slf4j.Slf4j;
import com.example.awsapp.position.models.Position;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

    private static BigDecimal applyShock(BigDecimal originalPrice, double percentage) {
        BigDecimal multiplier = BigDecimal.valueOf(1.0 + percentage);
        return originalPrice.multiply(multiplier);
    }

    @Scheduled(fixedDelay = 60_000)
    public void getStockPrices(){
        List<String> tickers = List.of("AAPL", "MSFT");
        List<StockInfo> stockInfoList = new ArrayList<>();
        tickers.forEach((stockSymbol) -> {
            BigDecimal stockPrice = yahooFinanceService.getCurrentPrice(stockSymbol);
            log.info("Current {} stock price: {}", stockSymbol, stockPrice);
            stockInfoList.add(new StockInfo(stockSymbol, stockPrice));
        });

        List<StockInfo> shockedStocks = stockInfoList.stream()
                .map(stock -> new StockInfo(
                        stock.getSymbol(),
                        applyShock(stock.getMarketPrice(), 0.05)
                )).toList();
        for (StockInfo stock : shockedStocks) {
            log.info("Shocked {} stock price: {}", stock.getSymbol(), stock.getMarketPrice());
        }
    }
}