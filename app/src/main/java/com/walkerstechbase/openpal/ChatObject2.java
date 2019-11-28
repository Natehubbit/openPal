package com.walkerstechbase.openpal;


import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject2 implements Serializable {
    private String chatId;

    private ArrayList<UserObject2> userObjectArrayList = new ArrayList<>();

    public ChatObject2(String chatId){
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
    public ArrayList<UserObject2> getUserObjectArrayList() {
        return userObjectArrayList;
    }




    public void addUserToArrayList(UserObject2 mUser){
        userObjectArrayList.add(mUser);
    }
}
