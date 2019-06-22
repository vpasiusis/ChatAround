package com.example.chataround;

import android.graphics.Bitmap;

public class ListViewItem {
    private String id, name, message, time;
    private Bitmap image;

    public ListViewItem(String id, String name, Bitmap image, String message, String time) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.message = message;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}