package com.example.chataround;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class ListViewItem implements Serializable {
    private String id, name, message, time,imageId;
    private int comments, likes;
    private Bitmap image;
    private boolean isLiked=false;



    public ListViewItem(String id, String name, Bitmap image, String message, String imageId, String time, int comments, int likes) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.message = message;
        this.imageId = imageId;
        this.time = time;
        this.comments = comments;
        this.likes=likes;
    }
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
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

    public void setComments(int comments){
        this.comments = comments;
    }

    public int getComments(){
        return comments;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }

    public int getLikes(){
        return likes;
    }

    public void setLiked(boolean isLiked){
        this.isLiked = isLiked;
    }
    public boolean getLiked(){
        return isLiked;
    }
}
