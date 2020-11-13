package com.snx.ImageProcess.object;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Image {
    private String storeLink;
    private String name;
    private Date date;
    private String place;
    private String filter;
}
