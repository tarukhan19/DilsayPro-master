package com.dbvertex.dilsayproject.Model;

public class ChatListDTO
{
    String username,chatId,rec_user_id;




    public String getchatId() {return chatId;}

    public void setchatId(String chatId) {
        this.chatId = chatId;
    }

    public String getrec_user_id() {return rec_user_id;}

    public void setrec_user_id(String rec_user_id) {
        this.rec_user_id = rec_user_id;
    }


    public String getusername() {return username;}

    public void setusername(String username) {
        this.username = username;
    }



}
