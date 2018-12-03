package com.example.mocalatte.project1.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  usage.. 예시..d
 *  DBManager dbManager;
 SQLiteDatabase db;
 1. select
 dbManager = new DBManager(this);
 db = dbManager.getWritableDatabase();
 String user_seq = String.valueOf(sp.getInt("user_seq", 0));
 Cursor cursor = db.rawQuery("SELECT category, date, content FROM " +
 dbManager.inquiryTB + " WHERE user_seq = " + "\"" + user_seq + "\"", null);
 while (cursor.moveToNext()) {
 category.add(cursor.getString(0));
 date.add(cursor.getString(1));
 content.add(cursor.getString(2));
 Log.i("Category : ", cursor.getString(0));
 Log.i("Date : ", cursor.getString(1));
 Log.i("Content : ", cursor.getString(2));
 }
 cursor.close();
 db.close();??해야겠지..
 2. insert
 db = dbManager.getWritableDatabase();
 ContentValues values = new ContentValues();
 values.put("user_seq", user_seq);
 values.put("category", category);
 values.put("date", getDateTime());
 values.put("content", content);
 db.insert(dbManager.inquiryTB, null, values);
 db.close();
 getDateTime메소드 구현은 ..
 private String getDateTime() {
 SimpleDateFormat dateFormat = new SimpleDateFormat(
 "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
 Date date = new Date();
 return dateFormat.format(date);
 }
 */


public class DBManager extends SQLiteOpenHelper {

    public final static String mainDB = "MainDB";
    public final static String ContactTB = "Contact";
    /*
    public final static String favoriteProductsTB = "FavoriteProducts";
    public final static String familyTB = "Family";
    public final static String favoriteListTB = "FavoriteList";
    public final static String inquiryTB = "Inquiry";
    public final static String reportTB = "Report";
    public final static String searchedProductsTB = "SearchedProducts";
    public final static String dictionaryIndexTB = "DictionaryIndex";
    */
    public final static int DB_ver = 1;

    public DBManager(Context context) {
        super(context, mainDB, null, DB_ver);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ContactTB + "("
                    + "name VARCHAR(10) NOT NULL"
                + ", " + "phone VARCHAR(15) NOT NULL PRIMARY KEY);");
        /**
         db.execSQL("CREATE TABLE " + favoriteProductsTB + " (pictureUri TEXT NOT NULL"
         + ", " + "name VARCHAR(100)"
         + ", " + "company VARCHAR(80)"
         + ", " + "eatingCaution TEXT"
         + ", " + "componentGrade VARCHAR(80)"
         + ", " + "shape TEXT"
         + ", " + "howToEat TEXT"
         + ", " + "permittedDate CHAR(8)"
         + ", " + "permittedLicenceNum VARCHAR(20)"
         + ", " + "manufactureNum VARCHAR(30) NOT NULL" //redis product key값(품목제조번호, 식약처 DB의 유일키)
         + ", " + "expiryDate TEXT"
         + ", " + "howToStore TEXT"
         + ", " + "component TEXT);");
         db.execSQL("CREATE TABLE " + familyTB + " (user_seq VARCHAR(50) NOT NULL"
         + ", " + "familyName VARCHAR(100) NOT NULL"
         + ", " + "familyId VARCHAR(70) NOT NULL" //id + unix time
         + ", " + "relationship VARCHAR(20) NOT NULL"
         + ", " + "age TINYINT NOT NULL"
         + ", " + "man TINYINT NOT NULL);");
         db.execSQL("CREATE TABLE " + favoriteListTB + " (id VARCHAR(50) NOT NULL"
         + ", " + "familyId VARCHAR(70) NOT NULL"
         + ", " + "pictureUri TEXT NOT NULL"
         + ", " + "name varchar(100) NOT NULL"
         + ", " + "company VARCHAR(80)"
         + ", " + "componentGrade VARCHAR(80)"
         + ", " + "manufactureNum VARCHAR(30) NOT NULL"
         + ", " + "registeredDate INTEGER"
         + ", " + "alert INTEGER NOT NULL);"); //유통기한 알림 허용 여부(1 == 허용, 0 == 불허)
         db.execSQL("CREATE TABLE " + inquiryTB + " (user_seq VARCHAR(50) NOT NULL"
         + ", " + "category VARCHAR(30) NOT NULL"
         + ", " + "date DATETIME NOT NULL"
         + ", " + "content TEXT);");
         db.execSQL("CREATE TABLE " + reportTB + " (user_seq VARCHAR(50) NOT NULL"
         + ", " + "manufactureNum INTEGER NOT NULL"
         + ", " + "productName VARCHAR(100) NOT NULL"
         + ", " + "date DATETIME NOT NULL"
         + ", " + "content TEXT);");
         // 로컬 db에 텍스트 자동완성기능을 제공하기 위한 데이터를 저장.. 과거에 검색했던 텍스트를 보여주는기능.. 히스토리 같은 느낌
         db.execSQL("CREATE TABLE " + searchedProductsTB + " ("  //id INTEGER PRIMARY KEY AUTOINCREMENT
         + ", " + "productName VARCHAR(100) PRIMARY KEY NOT NULL"
         + ", " + "count INTEGER NOT NULL);");
         db.execSQL("CREATE TABLE " + dictionaryIndexTB + " (componentName VARCHAR(40) NOT NULL);");
         //dictionaryMain listView에 넣을 영양소 목록, 서버로부터 받아옴
         */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
