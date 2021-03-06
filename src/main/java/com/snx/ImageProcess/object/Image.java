package com.snx.ImageProcess.object;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Image")
public class Image implements Serializable {
    @DynamoDBHashKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "filterName-id-index")
    private String id;

    @DynamoDBRangeKey
    private String name;

    @DynamoDBAttribute
    private String s3Key;

    @DynamoDBTypeConvertedTimestamp(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timeZone = "UTC+08:00")
    @DynamoDBAttribute
    private Date time;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "filterName-id-index")
    @DynamoDBAttribute
    private String filterName;

}
