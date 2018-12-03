package com.example.mocalatte.project1.item;

public class ContactItem {
        String contactName;
        String contactNum;

    public ContactItem(String name, String num) {
        this.contactName = name;
        this.contactNum = num;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String name) {
        this.contactName = name;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String num) {
        this.contactNum = num;
    }
}
