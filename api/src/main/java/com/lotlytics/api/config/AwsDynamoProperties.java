package com.lotlytics.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("aws.dynamodb")
public class AwsDynamoProperties {
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private String defaultTableName;

    public Region getRegion() {
        switch (this.region) {
            case "us-east-1":
                return Region.US_EAST_1;
            default:
                return Region.US_EAST_1;
        }
    }

    @Override
    public String toString() {
        return "AWS DynamoDB Properties\n" +
               "  Endpoint: " + endpoint + "\n" +
               "  Region: " + region + "\n" +
               "  Access Key: " + accessKey + "\n" +
               "  Secret Key: " + secretKey + "\n" +
               "  Default Table Name: " + defaultTableName + "\n";
    }
}