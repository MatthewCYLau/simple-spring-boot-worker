package com.example.awsapp.position.service;
import lombok.extern.slf4j.Slf4j;
import com.example.awsapp.position.models.Position;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Slf4j
@Service
@EnableScheduling
public class Poller {

    private final DynamoDbTable<Position> table;

    public Poller(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("stock_trading_positions", TableSchema.fromBean(Position.class));
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
}