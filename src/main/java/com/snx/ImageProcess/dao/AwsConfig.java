package com.snx.ImageProcess.dao;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AwsConfig {
    @Value("${ACCESS_KEY}")
    private String accessKey;
    private String secretKey = "QAy5peDdO/uv+1jk614KsXArsjJp2hsiZdrKrLvE";
    @Value("${aws.s3.bucket}")
    private String bucketName;


    @Bean
    public AmazonS3 s3Client() {

        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("ap-southeast-1")
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .disableChunkedEncoding()
                .build();

        return s3Client;
    }

    @Bean
    public AmazonDynamoDB dbClient() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonDynamoDB dbClient = AmazonDynamoDBClientBuilder.standard()
                .withRegion("ap-southeast-1")
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
        return dbClient;
    }

    @Bean
    public DynamoDBMapper dbMapper() {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER)
                .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                .withTableNameOverride(null)
                .withPaginationLoadingStrategy(DynamoDBMapperConfig.PaginationLoadingStrategy.EAGER_LOADING)
                .build();
        DynamoDBMapper dbMapper = new DynamoDBMapper(dbClient(), mapperConfig);
        return dbMapper;
    }


}
