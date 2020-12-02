package com.snx.ImageProcess.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateImageInput {
    private String id;
    private String time;
    private String filterName;
    private String name;
    private Boolean saveAnother;
}
