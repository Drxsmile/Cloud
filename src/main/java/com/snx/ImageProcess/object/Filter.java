package com.snx.ImageProcess.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor

public class Filter {
    private String name;
    private String originalImageId;
    public Filter(){
        this.name = "origin";
        this.originalImageId = "";
    }
    public Filter(String id){
        this.name = "origin";
        this.originalImageId = id;
    }
}
