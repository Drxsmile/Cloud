package com.snx.ImageProcess.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateImageInput {
    private String id;
    private String filterName;
    private String name;
    private String newName;
}
