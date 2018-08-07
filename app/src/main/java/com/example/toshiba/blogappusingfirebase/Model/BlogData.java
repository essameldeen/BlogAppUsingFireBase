package com.example.toshiba.blogappusingfirebase.Model;


import java.sql.Date;



public class BlogData extends BlogPostId {
     public  String user_id , image_url, desc ,thumb;
     public   String timeStamp ;



    public BlogData() {
    }

    public BlogData(String user_id, String image_url, String desc, String thumb,String timeStamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.thumb = thumb;
        this.timeStamp = timeStamp;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.thumb = image_thumb;
    }
    public String getTimeStam() {
        return timeStamp;
    }

    public void setTime(String time) {
        this.timeStamp = time;
    }

}
