package com.example.chataround;

import java.io.Serializable;

public class UserClass implements Serializable {
    private String id, name, avatarId, description, email;
    private int posts, likes,type;

    public UserClass(String id, String name, String avatarId, String description, String email, int posts, int likes, int type) {
        this.id = id;
        this.name = name;
        this.avatarId = avatarId;
        this.description = description;
        this.email = email;
        this.posts = posts;
        this.likes = likes;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
