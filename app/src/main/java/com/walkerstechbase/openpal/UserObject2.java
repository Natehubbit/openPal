package com.walkerstechbase.openpal;

import java.io.Serializable;

public class UserObject2 implements Serializable {

    public UserObject2() {
    }

    private String  uid,
            name,
            phone,
            notificationKey;

    private Boolean selected = false;

    public UserObject2(String uid){
        this.uid = uid;
    }
    public UserObject2(String uid, String name, String phone){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }
    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }
    public String getNotificationKey() {
        return notificationKey;
    }
    public Boolean getSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
