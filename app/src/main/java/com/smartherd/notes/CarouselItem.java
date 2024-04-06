package com.smartherd.notes;

public class CarouselItem {
    private final int imageResource;
    private final String text;

    public CarouselItem(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {
        return text;
    }
}
