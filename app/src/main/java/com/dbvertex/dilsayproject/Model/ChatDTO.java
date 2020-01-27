package com.dbvertex.dilsayproject.Model;

public class ChatDTO {
    private String msg;
    private String sendBy,recieveBy;
    private String time,image;

    public String getRecieveBy() {
        return recieveBy;
    }

    public void setRecieveBy(String recieveBy) {
        this.recieveBy = recieveBy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getrecieveBy() {
        return recieveBy;
    }

    public void setrecieveBy(String recieveBy) {
        this.recieveBy = recieveBy;
    }
}
