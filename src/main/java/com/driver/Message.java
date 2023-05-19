package com.driver;

import java.util.Date;

public class Message {
    private int id;
    private String content;
    private Date timestamp;

    //Will make the constructor
    public Message(int id, String content){
        this.id=id;
        this.content=content;
        this.timestamp= new Date();// give Current Date
        //LocalDateTime. - you can also use this
    }

    //Will get the getters


    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
