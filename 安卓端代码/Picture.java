package com.example.lenovo.recognition;

import android.graphics.Bitmap;

public class Picture {
    private Bitmap bitmap;
    private String title;
    private String time;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Picture(Bitmap bitmap, String title, String time) {
        this.bitmap = bitmap;
        this.title = title;
        this.time = time;
    }

    public Picture() {
    }
}
