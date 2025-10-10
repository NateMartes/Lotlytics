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

/**
 * The DynamoDBConfig defines a specifc configuration to get DynamoDB clients using
 * the AwsDynamoProperties.
 * 
 * @see AwsDynamoProperties
 */
@Configuration
@EnableConfigurationProperties(AwsDynamoProperties.class)
public class DynamoDBConfig {

    private AwsDynamoProperties properties;
    

    /**
     * The DynamoDBConfig constructor calls out for a AwsDynamoProperties bean.
     * Such a bean should already exists from the 
     * EnableConfigurationProperties(AwsDynamoProperties.class) decorator.
     * 
     * @param properties an AwsDynamoProperties bean.
     */
    public DynamoDBConfig(AwsDynamoProperties properties) {
        this.properties = properties;
    }

    /**
     * The dynamoDbClient method creates a DynamoDB client bean using the properties
     * defined in AwsDynamoProperties.
     * 
     * @see AwsDynamoProperties
     * 
     * @return a configured DynamoDbClient.
     */
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

    /**
     * The dynamoDbEnhancedClient takes an already existing DynamoDbClient and enhanches it using
     * software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient builder method. This method
     * creates a DynamoDbEnhancedClient bean.
     * 
     * @param dynamoDbClient a existing DynamoDbClient
     * @return a configured DynamoDbEnhancedClient.
     */
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    /**
     * The getDefaultTable method creates a bean of type DynamoDbTable<defaultTableSchemaClass>
     * using the AwsDynamoProperties. If no schema class is defined, then the underlying method fails.
     * 
     * @see AwsDynamoProperties
     * 
     * @param enhancedClient the client to get the table from
     * @return DynamoDbTable<defaultTableSchemaClass>
     */
    @Bean
    public DynamoDbTable<?> getDefaultTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(
            properties.getDefaultTableName(), 
            TableSchema.fromBean(properties.getDefaultTableSchemaClass())
        );
    }
}
