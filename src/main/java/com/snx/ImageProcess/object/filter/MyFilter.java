package com.snx.ImageProcess.object.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.awt.image.BufferedImage;

@Data
@NoArgsConstructor
public class MyFilter {
    private static final String filterName = "origin";

    public BufferedImage applyMyFilter(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                Color color = new Color(pixel);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                Color newColor = filterSetColor(r, g, b);
                image.setRGB(j, i, newColor.getRGB());
            }
        }
        return image;
    }

    public Color filterSetColor(int r, int g, int b) {
        return new Color(r, g, b);
    }
}
