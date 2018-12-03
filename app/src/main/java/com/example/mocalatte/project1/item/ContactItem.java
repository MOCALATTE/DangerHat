package com.example.mocalatte.project1.item;

import android.content.Context;
import android.provider.ContactsContract;

import java.io.Serializable;

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
