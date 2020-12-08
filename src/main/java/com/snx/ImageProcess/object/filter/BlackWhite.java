package com.snx.ImageProcess.object.filter;

import java.awt.*;

public class BlackWhite extends MyFilter{
    private static final String filterName = "BlackWhite";

    @Override
    public Color filterSetColor(int r, int g, int b) {
        int ave = (int) (r + g + b) / 3;
        if (ave < 100){
            return new Color(0, 0, 0);
        }else return new Color(255, 255, 255);
    }
}
