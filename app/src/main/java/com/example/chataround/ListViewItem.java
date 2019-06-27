package com.example.chataround;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class ListViewItem implements Serializable {
    private String id, name, message, time;
    private Bitmap image;
    private boolean isLoading=false;

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

    public boolean getIsLoading(){
        return isLoading;
    }

    public void setIsLoading(boolean value){
        isLoading = value;
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
