package com.example.mocalatte.project1.item;

public class FriendListMenu {
    private String name;
    private String contactnum;

    public FriendListMenu(String name, String contactnum) {
        this.name = name;
        this.contactnum = contactnum;
    }

    public String getName() {
        return name;
    }

    public String getContactnum() {
        return contactnum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactnum(String contactnum) {
        this.contactnum = contactnum;
    }
}
