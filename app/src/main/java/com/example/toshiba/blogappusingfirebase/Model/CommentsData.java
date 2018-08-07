package com.example.toshiba.blogappusingfirebase.Model;

public class CommentsData {
    private  String comment;
    private  String from;
    private String timeStamp;

    public CommentsData() {
    }

    public CommentsData(String comment, String from, String timeStamp) {
        this.comment = comment;
        this.from = from;
        this.timeStamp = timeStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
