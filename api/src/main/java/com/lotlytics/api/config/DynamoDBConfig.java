package com.lotlytics.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import com.lotlytics.api.entites.event.Event;

@Configuration
@EnableConfigurationProperties(AwsDynamoProperties.class)
public class DynamoDBConfig {

    private AwsDynamoProperties properties;

    public DynamoDBConfig(AwsDynamoProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(java.net.URI.create(properties.getEndpoint()))
                .region(properties.getRegion())
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(properties.getAccessKey(), 
                                                        properties.getSecretKey()
                                                )
                            )
                )
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<Event> eventTable(
            DynamoDbEnhancedClient enhancedClient, 
            AwsDynamoProperties properties) {
        return enhancedClient.table(
            properties.getDefaultTableName(), 
            TableSchema.fromBean(Event.class)
        );
    }
}
