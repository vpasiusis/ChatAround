package com.example.chataround;

import java.io.Serializable;

public class ListViewComment implements Serializable {
    private String name, message, id;

    public ListViewComment(String id, String name, String message){
        this.id = id;
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
