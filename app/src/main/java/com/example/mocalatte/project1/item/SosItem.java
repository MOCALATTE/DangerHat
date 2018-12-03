package com.example.mocalatte.project1.item;

public class SosItem {
    String sosName;
    String sosNum;

    public SosItem(String name, String num) {
        this.sosName = name;
        this.sosNum = num;
    }

    public String getSosName() {
        return sosName;
    }

    public void setSosName(String name) {
        this.sosName = name;
    }

    public String getSosNum() {
        return sosNum;
    }

    public void setSosNum(String num) {
        this.sosNum = num;
    }
}
