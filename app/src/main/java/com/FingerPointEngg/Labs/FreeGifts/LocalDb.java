package com.FingerPointEngg.Labs.FreeGifts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.concurrent.locks.ReentrantLock;

public class LocalDb {

    private SQLiteDatabase db;
    private ReentrantLock lock = new ReentrantLock();

    public LocalDb(Context ctx){
        lock.lock();
        db=ctx.openOrCreateDatabase("FreeGifts", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS orders(series INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,username VARCHAR,orderid VARCHAR, pid VARCHAR, order_date VARCHAR,address VARCHAR, payment VARCHAR, price VARCHAR,giftwrap VARCHAR, phone VARCHAR, status VARCHAR, delivery VARCHAR, quantity VARCHAR, delivery_fee VARCHAR, imageurl VARCHAR, title VARCHAR,order_month VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS userdata( username VARCHAR, password VARCHAR, email VARCHAR, address VARCHAR, phone VARCHAR, loggedin VARCHAR)");
        lock.unlock();

    }

    void addOrderDetails(String username,String orderid, String pid,String order_date,String address,String payment,String price,String giftwrap,String phone,String status,String delivery,String quantity,String delivery_fee, String imageurl, String p_title, String order_month){
        lock.lock();
        db.execSQL("INSERT INTO orders(username,orderid,pid,order_date,address,payment,price,giftwrap,phone,status,delivery,quantity,delivery_fee,imageurl,title,order_month) VALUES('"+username+"','"+orderid+"','"+pid+"','"+order_date+"','"+address+"','"+payment+"','"+price+"','"+giftwrap+"','"+phone+"','"+status+"','"+delivery+"','"+quantity+"','"+delivery_fee+"','"+imageurl+"','"+p_title+"','"+order_month+"');");
        lock.unlock();
    }

    void addUser(String username,String password,String email,String address,String phone){
        lock.lock();
        db.execSQL("INSERT INTO userdata VALUES('"+username+"','"+password+"','"+email+"','"+address+"','"+phone+"', 'true');");
        lock.unlock();
    }

    boolean checkUserLoggedin(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT loggedin FROM userdata LIMIT 1",null);
        lock.unlock();
        if(c.moveToFirst()){
            if(c.getString(0).equals("true")) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
        return true;
    }

    void logout(){
            lock.lock();
            db.execSQL("UPDATE userdata SET loggedin ='false'");
            lock.unlock();
    }

    String getUsername(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT username FROM userdata LIMIT 1" ,null);
        lock.unlock();

        if(c.moveToFirst()){
            String getString = c.getString(0);
            c.close();
            return getString;
        }
        c.close();
        return null;
    }

    boolean checkNewUser(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT count(*) FROM orders",null);
        lock.unlock();
        if(c.moveToFirst()){
            if(c.getInt(0)<1) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
        return false;
    }
    void orderStatusUpdate(String price, String order_id, String status){

        lock.lock();
        db.execSQL("UPDATE orders SET status ='"+status+"' WHERE orderid ='"+order_id+"'AND price = '"+price+"'");
        lock.unlock();

    }
    void login(){
        lock.lock();
        db.execSQL("UPDATE userdata SET loggedin ='true'");
        lock.unlock();
    }
    boolean alreadyRegistered(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT username FROM userdata",null);
        lock.unlock();
        if(c.moveToFirst()){
            if(c.getString(0)!=null&&!c.getString(0).equals("null")) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
        return false;
    }

    boolean checkUser(String username, String password){
        lock.lock();
        Cursor c=db.rawQuery("SELECT username,password FROM userdata",null);
        lock.unlock();
        if(c.moveToFirst()){
            if(c.getString(0).equals(username)&&c.getString(1).equals(password)) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
        return false;
    }
    void setDeliveryDetails(String ad, String email, String phn){
        if(!(ad.isEmpty()&&phn.isEmpty())) {
            lock.lock();
            db.execSQL("UPDATE userdata SET address ='" + ad + "', phone = '" + phn + "', email = '"+email+"'");
            lock.unlock();
        }
    }
    String getAddress(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT address FROM userdata",null);
        lock.unlock();

        if(c.moveToFirst()){
            String getString = c.getString(0);
            c.close();
            return getString;
        }
        c.close();
        return null;
    }


    Bundle getOrderDetailsByID(String orderid){
        lock.lock();
        Cursor c=db.rawQuery("SELECT * FROM orders WHERE orderid = '"+orderid+"'",null);
        lock.unlock();

        if(c.moveToFirst()){
            Bundle data = new Bundle();
            data.putString("username",c.getString(1));
            data.putString("order_id",c.getString(2));
            data.putString("pid",c.getString(3));
            data.putString("order_date",c.getString(4));
            data.putString("address",c.getString(5));
            data.putString("payment",c.getString(6));
            data.putString("price",c.getString(7));
            data.putString("giftwrap",c.getString(8));
            data.putString("phone",c.getString(9));
            data.putString("status",c.getString(10));
            data.putString("delivery",c.getString(11));
            data.putString("quantity",c.getString(12));
            c.close();
            return data;
        }
        c.close();
        return null;
    }

    Bundle getOrderDetailsBySeries(int series){
        lock.lock();
        Cursor c=db.rawQuery("SELECT * FROM orders WHERE series = '"+series+"'",null);
        lock.unlock();

        if(c.moveToFirst()){
            Bundle data = new Bundle();
            data.putString("username",c.getString(1));
            data.putString("order_id",c.getString(2));
            data.putString("pid",c.getString(3));
            data.putString("order_date",c.getString(4));
            data.putString("address",c.getString(5));
            data.putString("payment",c.getString(6));
            data.putString("price",c.getString(7));
            data.putString("giftwrap",c.getString(8));
            data.putString("phone",c.getString(9));
            data.putString("status",c.getString(10));
            data.putString("delivery",c.getString(11));
            data.putString("quantity",c.getString(12));
            data.putString("delivery_fee",c.getString(13));
            data.putString("imageurl",c.getString(14));
            data.putString("title",c.getString(15));
            c.close();
            return data;
        }
        c.close();
        return null;
    }


    Bundle getUserDetails(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT * FROM userdata",null);
        lock.unlock();

        if(c.moveToFirst()){
            Bundle data = new Bundle();
            data.putString("username",c.getString(0));
            data.putString("password",c.getString(1));
            data.putString("email",c.getString(2));
            data.putString("address",c.getString(3));
            data.putString("phone",c.getString(4));
            c.close();
            return data;
        }
        c.close();
        return null;
    }

    int getTotalOrders(){
        lock.lock();
        Cursor c=db.rawQuery("SELECT series FROM orders ORDER BY series DESC LIMIT 1",null);
        lock.unlock();

        if(c.moveToFirst()){
            int retval=c.getInt(0);
            c.close();
            return retval;
        }
        c.close();
        return 0;
    }

    boolean freeOfferAvailable(String month) {
        lock.lock();
        Cursor c=db.rawQuery("SELECT count(*) FROM orders WHERE order_month = '"+month+"'",null);
        lock.unlock();
        if(c.moveToFirst()){
            if(c.getInt(0)<4) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
        return false;

    }
}
