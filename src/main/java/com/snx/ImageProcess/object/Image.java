package com.snx.ImageProcess.object;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

//    public Date getTime() throws ParseException {
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        String s = format.format(time);
//        return format.parse(s);
//    }
}
