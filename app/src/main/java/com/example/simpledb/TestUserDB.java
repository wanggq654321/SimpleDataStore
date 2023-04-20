package com.example.simpledb;

import android.database.Cursor;

import com.mapping.db.annotation.Column;
import com.mapping.db.annotation.PrimaryKey;
import com.mapping.db.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@TableName(name = "User")
public class TestUserDB implements Serializable {

    public static final String TABLENAME = "NOTE";

    /**
     * 字段属性
     */
    public static class Properties {
        //  public final static Property Id = new Property(0, Long.class, "id", true, "_id"); 可以注解生成
        // 下面字段直接查询的时候引用，是类的属性名-name，不是数据库字段
        public final static String id = "id";
        public final static String name = "name";
        public final static String age = "age";
        public final static String price = "price";
        public final static String height = "height";
        public final static String isGood = "isGood";
        public final static String date = "date";
        public final static String timestamp = "timestamp";
    }

    @PrimaryKey(autoIncrement = true)
    private long id = 0;

    @Column(name = "name")
    private String name = "wgq";

    private int age = 20;

    private float price = 133f;

    private double height = 1213d;

    private boolean isGood = true;

    private Date date = new Date();

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public TestUserDB() {
    }

    public TestUserDB(long id, String name, int age, float price, double height, boolean isGood) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.price = price;
        this.height = height;
        this.isGood = isGood;
    }


    public TestUserDB readEntity(Cursor cursor, int offset) {
        TestUserDB entity = new TestUserDB(
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1),
                cursor.getInt(offset + 2),
                cursor.getFloat(offset + 3),
                cursor.getDouble(offset + 4),
                cursor.getInt(offset + 5) == 1);
        return entity;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }
}
