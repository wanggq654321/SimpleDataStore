package com.example.simpledb;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import cn.mappingdb.AbstractDao;
import cn.mappingdb.database.DBTool;
import cn.mappingdb.log.DaoLog;
import cn.mappingdb.query.QueryBuilder;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mapping.db.PHDbUtils;
import com.mapping.db.log.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_show;
    private Gson gson = new Gson();
    private AbstractDao abstractDao = new AbstractDao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_show = findViewById(R.id.tv_show);

        getString(R.string.app_name);

        PHDbUtils.getInstance().initDB(this, null);

        DBTool.getInstance().initDB(this, 1, "simpleMapping2.db", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add) {

//            long last = System.currentTimeMillis();
//            for (int i = 0; i < 10; i++) {
//                TestUserNewDB testUserNewDB = new TestUserNewDB();
//                testUserNewDB.setName("wgq" + i);
//                testUserNewDB.setAge(i);
//                long rowId = abstractDao.insert(testUserNewDB);
//                DaoLog.e("rowId: " + rowId);
//            }
//            Log.e("插入耗时： " + ((System.currentTimeMillis() - last)) + " 毫秒");


            long last = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                TestUserDB testUserDB = new TestUserDB();
                testUserDB.setName("wgq" + i);
                testUserDB.setAge(i);
                PHDbUtils.getInstance().insert(testUserDB);
            }
            Log.e("插入耗时： " + ((System.currentTimeMillis() - last)) + " 毫秒");
        } else if (view.getId() == R.id.query) {
//            QueryBuilder builder = QueryBuilder.initBuild(TestUserNewDB.class);
//            builder.distinct()
//                    .whereOr(builder.property("name").eq("wgq1"),builder.column("age").eq("9"))
//                    .limit(10);
//            List<TestUserNewDB> list = abstractDao.queryBuild(builder);

//            List<TestUserNewDB> list = abstractDao.queryAll(TestUserNewDB.class);
            // List<TestUserDB> list = PHDbUtils.getInstance().query(TestUserDB.class, "age=?", new String[]{" "});
            List<TestUserDB> list = PHDbUtils.getInstance().query(TestUserDB.class, "age=?", new String[]{"1"});
            tv_show.setText(gson.toJson(list) + "");

        } else if (view.getId() == R.id.delete) {
            PHDbUtils.getInstance().delete(TestUserDB.class, "age=?", new String[]{"1"});
//            QueryBuilder builder = QueryBuilder.initBuild(TestUserNewDB.class);
//            builder.where(builder.property("name").eq("wgq1"));
//            abstractDao.deleteBuild(builder);
        } else if (view.getId() == R.id.update) {
//            PHDbUtils.getInstance().query(TestUserDB.class);
        }

    }


    private static String getSerializedLambda(Function func) {
//        // 从function取出序列化方法
//        Method writeReplaceMethod;
//        try {
//            writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//        return writeReplaceMethod.getName();


        return "";
    }

}