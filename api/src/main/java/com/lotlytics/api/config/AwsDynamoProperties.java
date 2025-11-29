package com.lotlytics.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import lombok.Getter;
import lombok.Setter;
import com.lotlytics.api.entites.event.Event;

/*
 * The AwsDynamoProperties class aggregates the configuration
 * properties defined in the application.properties file for
 * connecting to an AWS DynamoDB instance.
 *
 * Configuration keys:
 *   aws.dynamodb.endpoint   – The endpoint URL where DynamoDB is hosted.
 *                             Typically used for local testing (e.g., http://localhost:8000).
 *   aws.dynamodb.region     – The AWS region of the DynamoDB database.
 *   aws.dynamodb.accessKey  – The AWS access key ID used for authentication.
 *   aws.dynamodb.secretKey  – The AWS secret access key paired with the access key ID.
 *   aws.defaultTableName    – The default DynamoDB table name to be used.
 */
@Getter
@Setter
@ConfigurationProperties("aws.dynamodb")
public class AwsDynamoProperties {
    private String endpoint = "";
    private String region;
    private String accessKey;
    private String secretKey;
    private String defaultTableName;
    private String defaultTableSchemaClass;

    /**
     * The getRegion method returns the AWS Region object from the given region string.
     * 
     * @throws IllegalArgumentExeception if no valid region is given.
     * @return software.amazon.awssdk.regions.Region.
     */
    public Region getRegion() {
        switch (this.region) {
            case "us-east-1": return Region.US_EAST_1;
            default:
                throw new IllegalArgumentException("No Region defined for: " + this.region);
        }
    }

    /**
     * The getDefaultTableSchemaClass method returns the class from the given defaultTableSchemaClass string.
     * 
     * @throws IllegalArgumentExeception if no valid class is given.
     * @return Class<defaultTableSchemaClass>.
     */
    public Class<?> getDefaultTableSchemaClass() {
        switch (this.defaultTableSchemaClass) {
            case "Event": return Event.class;
            default:
                throw new IllegalArgumentException("No DTO defined for table: " + this.defaultTableSchemaClass);
        }
    }

    /**
     * The toString method for AwsDynamoProperties formats the properties as:
     *  AWS DynamoDB Properties
     *  Endpoint: ...
     *  Region: ...
     *  Access Key: ...
     *  ...
     * 
     * @return String representation of AwsDynamoProperties
     */
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