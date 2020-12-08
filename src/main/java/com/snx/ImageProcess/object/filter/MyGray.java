package com.snx.ImageProcess.object.filter;

import java.awt.*;

public class MyGray extends MyFilter {
    private static final String filterName = "MyGray";

    @Override
    public Color filterSetColor(int r, int g, int b) {
        int ave = (int) (r * 0.3 + g * 0.59 + b * 0.11);
//                int ave = (int) (r + g + b) / 3;
        return new Color(ave, ave, ave);
    }
}
