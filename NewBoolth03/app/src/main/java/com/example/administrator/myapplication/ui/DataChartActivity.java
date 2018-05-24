package com.example.administrator.myapplication.ui;

import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.sql.Order;
import com.example.administrator.myapplication.sql.OrderDao;
import com.example.administrator.myapplication.weight.LineChart;
import com.example.administrator.myapplication.weight.LineChartData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class DataChartActivity extends Activity {
    private LineChart lineChart;
    private List<LineChartData> dataList = new ArrayList<>();
    private List<Order> orderList;//database All data
    private OrderDao ordersDao;
    private int jumpKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_chart);
        Bundle bundle = this.getIntent().getExtras();
        jumpKind = bundle.getInt("jumpKind");
        initView();
        iniSqlData();
        setData();
    }

    private void iniSqlData() {
        ordersDao = new OrderDao(this);
        if (!ordersDao.isDataExist()) {
            ordersDao.initTable();
        }
        //从数据库获取数据
        orderList = ordersDao.getAllDate();
    }

    private void initView() {
        lineChart = (LineChart) findViewById(R.id.line_chart);
    }

    private void setData() {
        for (int i = 0; i < 7; i++) {
            LineChartData data = new LineChartData();
            if(orderList!=null && orderList.size()!=0){
                data.setItem(orderList.get(i).getData()+"时");
                if (jumpKind == 1) {
                    data.setPoint(Integer.parseInt(orderList.get(i).getTemperature()));
                } else if (jumpKind == 2) {
                    data.setPoint(Integer.parseInt(orderList.get(i).getHumidity()));
                } else if (jumpKind == 3) {
                    data.setPoint(Integer.parseInt(orderList.get(i).getInfrared()));
                } else if (jumpKind == 4) {
                    data.setPoint(Integer.parseInt(orderList.get(i).getSmoke()));
                }
                dataList.add(data);
            }
        }
        lineChart.setData(dataList);
    }


}
