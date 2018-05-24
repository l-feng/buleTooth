package com.example.administrator.myapplication.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class OrderDao {
    private static final String TAG = "OrdersDao";

    // 列定义
    private final String[] ORDER_COLUMNS = new String[]{"data", "temperature", "humidity", "infrared", "smoke"};

    private Context context;
    private OrderDBHelper ordersDBHelper;

    public OrderDao(Context context) {//创建一个OrderDao用于处理所有的数据操作方法。在OrderDao钟实例化OrderDBHelper：
        this.context = context;
        ordersDBHelper = new OrderDBHelper(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist() {
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {//数据库操作无外乎：“增删查改”。对于“增删改”这类对表内容变换的操作，我们需先调用getWritableDatabase()，
            // 在执行的时候可以调用通用的execSQL(String sql)方法或对应的操作API：insert()、delete()、update()。而对“查”，
            // 需要调用getReadableDatabase()，这时就不能使用execSQL方法了，得使用query()或rawQuery()方法
            db = ordersDBHelper.getReadableDatabase();
            // select count(Id) from Orders
            cursor = db.query(OrderDBHelper.TABLE_NAME, new String[]{"COUNT(data)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public void initTable() {
        SQLiteDatabase db = null;
        String temperature;
        String humidity;
        String infrared;
        String smoke;
        try {//增加数据 在我的Demo中，有两种增加数据操作：初始化数据
            //在进入Demo程序时，先判断表中是否有数据，如果表中没有数据，我将先添加一些数据。
            //在初始化数据时，因为一次性要添加的数据比较多，所以我直接采用的是execSQL方法：
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (1, '36','10','20','60')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (2, '30','14','29','63')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (3, '33','18','22','64')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (4, '39','11','24','69')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (5, '31','12','29','63')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (6, '32','17','22','66')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (7, '36','10','20','60')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (8, '30','14','29','63')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (9, '33','18','22','64')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (10, '39','11','24','69')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (11, '31','12','29','63')");
//            db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (12, '35','15','25','65')");

            //TODO
           /* String time = DateFormat.format("hh", System.currentTimeMillis()).toString();
            //lan ya
            for(int i=0;i<7;i++) {
                db.execSQL("insert into " + OrderDBHelper.TABLE_NAME + " (data, temperature,humidity,infrared,smoke) values (time, temperature,humidity,infrared,smoke)");
            }*/
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")) {
                // Toast.makeText(context, R.string.strUnableSql, Toast.LENGTH_SHORT).show();
            } else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")) {
                db = ordersDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
                // Toast.makeText(context, R.string.strSuccessSql, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Toast.makeText(context, R.string.strErrorSql, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 查询数据库中所有数据
     */
    public List<Order> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = ordersDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(OrderDBHelper.TABLE_NAME, ORDER_COLUMNS, null, null, null, null, "data desc");
            if (cursor.getCount() > 0) {
                List<Order> orderList = new ArrayList<Order>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseOrder(cursor));
                }
                return orderList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 查询数据库中所有数据
     */
    public List<Order> getTimeDate(String currenTime) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(OrderDBHelper.TABLE_NAME, ORDER_COLUMNS, "data = ?", new String[]{currenTime}, null, null, "data desc");
            if (cursor.getCount() > 0) {
                List<Order> orderList = new ArrayList<Order>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseOrder(cursor));
                }
                return orderList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }



    /**
     * 根据主键查询一条数据
     */
    public Order getTimeOneDate(String currenTime) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Order order=null;
        try {
            db = ordersDBHelper.getReadableDatabase();
            cursor = db.query(OrderDBHelper.TABLE_NAME, ORDER_COLUMNS, "data = ?", new String[]{currenTime}, null, null, "data desc");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    order=parseOrder(cursor);
                }
                return order;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }
    /**
     * 新增一条数据
     */
    public boolean insertDate(String data, String temperature, String humidity, String infrared, String smoke) {
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();//使用insert()方法我们插入一条新数据(7, "Jne", 700, "China")，对于修改数据的操作我们一般当作事务(Transaction)处理：
            // "temperature", "humidity", "infrared", "smoke"
            // insert into Orders(Id, CustomName, OrderPrice, Country) values (7, "Jne", 700, "China");
            ContentValues contentValues = new ContentValues();
            contentValues.put("data", data);
            contentValues.put("temperature", temperature);
            contentValues.put("humidity", humidity);
            contentValues.put("infrared", infrared);
            contentValues.put("smoke", smoke);
            db.insertOrThrow(OrderDBHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 删除一条数据  此处删除Id为7的数据
     */
    public boolean deleteOrder() {
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Orders where Id = 7
            db.delete(OrderDBHelper.TABLE_NAME, "data = ?", new String[]{String.valueOf(7)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 修改一条数据
     */
    public boolean updateOrder(String data, String temperature, String humidity, String infrared, String smoke) {
        SQLiteDatabase db = null;
        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("data", data);
            contentValues.put("temperature", temperature);
            contentValues.put("humidity", humidity);
            contentValues.put("infrared", infrared);
            contentValues.put("smoke", smoke);
            db.update(OrderDBHelper.TABLE_NAME,contentValues, "data = ?",new String[] {data});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 数据查询
     */
    public List<Order> getBorOrder() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();

            // select * from Orders where CustomName = 'Bor'
            cursor = db.query(OrderDBHelper.TABLE_NAME,
                    ORDER_COLUMNS,
                    "CustomName = ?",
                    new String[]{"Bor"},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<Order> orderList = new ArrayList<Order>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Order order = parseOrder(cursor);
                    orderList.add(order);
                }
                return orderList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 统计查询  此处查询Country为China的用户总数
     */
//    public int getChinaCount(){
//        int count = 0;
//
//        SQLiteDatabase db = null;
//        Cursor cursor = null;
//
//        try {
//            db = ordersDBHelper.getReadableDatabase();
//            // select count(Id) from Orders where Country = 'China'
//            cursor = db.query(OrderDBHelper.TABLE_NAME,
//                    new String[]{"COUNT(Id)"},
//                    "Country = ?",
//                    new String[] {"China"},
//                    null, null, null);
//
//            if (cursor.moveToFirst()) {
//                count = cursor.getInt(0);
//            }
//        }
//        catch (Exception e) {
//            Log.e(TAG, "", e);
//        }
//        finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            if (db != null) {
//                db.close();
//            }
//        }
//
//        return count;
//    }

    /**
     * 比较查询  此处查询单笔数据中OrderPrice最高的
     */
    public Order getMaxOrderPrice() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select Id, CustomName, Max(OrderPrice) as OrderPrice, Country from Orders
            cursor = db.query(OrderDBHelper.TABLE_NAME, new String[]{"data", "weather"}, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    return parseOrder(cursor);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 将查找到的数据转换成Order类
     */
    private Order parseOrder(Cursor cursor) {
        Order order = new Order();
        order.data = (cursor.getString(cursor.getColumnIndex("data")));
        order.temperature = (cursor.getString(cursor.getColumnIndex("temperature")));
        order.humidity = (cursor.getString(cursor.getColumnIndex("humidity")));
        order.infrared = (cursor.getString(cursor.getColumnIndex("infrared")));
        order.smoke = (cursor.getString(cursor.getColumnIndex("smoke")));
        return order;
    }
}

