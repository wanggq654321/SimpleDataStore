package com.example.simpledb;

import com.mapping.db.annotation.PrimaryKey;
import com.mapping.db.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@TableName(name = "Roles")
public class TestRolesDB implements Serializable {

    @PrimaryKey(autoIncrement = true)
    private long id = 0;

    private String role = "admin";

    private Date date = new Date();

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());


}
