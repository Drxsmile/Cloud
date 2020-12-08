package com.snx.ImageProcess.object.filter;

import java.awt.*;

public class Film extends MyFilter{
    private static final String filterName = "Film";

    @Override
    public Color filterSetColor(int r, int g, int b) {
        return new Color(255-r, 255-g, 255-b);
    }
}
