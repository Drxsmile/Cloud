package com.snx.ImageProcess.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private String id;
    private String name;
    private String location;
    private String time;
    private Filter filter;
    static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public Image(String id, String name, String location){
        this.id = id;
        this.name = name;
        this.location = location;
        this.time = format.format(new Date());
        this.filter = new Filter(id);
    }

    public void setFilter(String filterName) {
        this.filter.setName(filterName);
    }
}
