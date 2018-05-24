package com.example.administrator.myapplication.sql;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class Order {
    public String data;
    public String temperature;
    public String humidity;
    public String infrared;
    public String smoke;

    public Order() {
    }

    public Order(String id, String temperature, String humidity, String infrared, String smoke) {
        this.data = id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.infrared = infrared;
        this.smoke = smoke;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }//获得温度

    public String getHumidity() {return humidity;}

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getInfrared() {
        return infrared;
    }

    public void setInfrared(String infrared) {
        this.infrared = infrared;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
