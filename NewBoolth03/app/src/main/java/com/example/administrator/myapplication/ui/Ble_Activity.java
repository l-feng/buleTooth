package com.example.administrator.myapplication.ui;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.service.BluetoothLeService;
import com.example.administrator.myapplication.sql.Order;
import com.example.administrator.myapplication.sql.OrderDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Ble_Activity extends Activity implements OnClickListener {

    private final static String TAG = Ble_Activity.class.getSimpleName();
    //蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static String EXTRAS_DEVICE_RSSI = "RSSI";
    //蓝牙连接状态
    private boolean mConnected = false;
    private String status = "disconnected";
    //蓝牙名字
    private String mDeviceName;
    //蓝牙地址
    private String mDeviceAddress;
    //蓝牙信号值
    private String mRssi;
    private Bundle b;
    //蓝牙service,负责后台的蓝牙服务
    private static BluetoothLeService mBluetoothLeService;
    //文本框，显示接受的内容
    private TextView connect_state;

    private TextView temperatureValueTextView;
    private TextView humidityValueTextView;
    private TextView infraredValueTextView;
    private TextView smokeValueTextView;
    private TextView temperatureButton;
    private TextView humidityButton;
    private TextView infraredButton;
    private TextView smokeButton;
    private TextView timeText;

    private List<Order> orderList;//database All data
    private OrderDao ordersDao;
    private Order order;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    //蓝牙特征值
    private static BluetoothGattCharacteristic target_chara = null;
    private Handler mhandler = new Handler();
    // 2.重写消息处理函数
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 判断发送的消息，1是连接到蓝牙，2是更新时间表
                case 1: {
                    // 更新View
                    String state = msg.getData().getString("connect_state");
                    connect_state.setText(state);
                    break;
                }
                case 2:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
                    timeText.setText(sysTimeStr); //更新时间
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_activity);
        b = getIntent().getExtras();
        //从意图获取显示的蓝牙信息
        mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
        mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
        mRssi = b.getString(EXTRAS_DEVICE_RSSI);

		/* 启动蓝牙service */
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        iniSqlData();
        initView();
        initTimeThread();
        setData();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: init
     * @Description: TODO(初始化UI控件)
     */
    private void initView() {
        connect_state = (TextView) this.findViewById(R.id.connect_state);
        connect_state.setText(status);
        temperatureValueTextView = (TextView) findViewById(R.id.temperature);
        humidityValueTextView = (TextView) findViewById(R.id.humidity);
        infraredValueTextView = (TextView) findViewById(R.id.infrared);
        smokeValueTextView = (TextView) findViewById(R.id.smoke);
        temperatureButton = (TextView) findViewById(R.id.recent_temperature);
        humidityButton = (TextView) findViewById(R.id.recent_humidity);
        infraredButton = (TextView) findViewById(R.id.recent_infrared);
        smokeButton = (TextView) findViewById(R.id.recent_smoke);
        temperatureButton.setOnClickListener(this);
        humidityButton.setOnClickListener(this);
        infraredButton.setOnClickListener(this);
        smokeButton.setOnClickListener(this);
        timeText = (TextView) findViewById(R.id.time);
    }


    private void iniSqlData() {
        ordersDao = new OrderDao(this);
//        if (!ordersDao.isDataExist()) {
//            ordersDao.initTable();
//        }
        String time = DateFormat.format("hh", System.currentTimeMillis()).toString();
        //从数据库获取数据
        // orderList = ordersDao.getTimeDate(time);
        order = ordersDao.getTimeOneDate(time);
    }

    private void setData() {
        if (order != null) {
            temperatureValueTextView.setText(order.getTemperature());
            humidityValueTextView.setText(order.getHumidity());
            infraredValueTextView.setText(order.getInfrared());
            smokeValueTextView.setText(order.getSmoke());
        }
    }

    private void initTimeThread() {
        new TimeThread().start(); //启动新的线程
    }


    /* BluetoothLeService绑定的回调函数 */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            // 根据蓝牙地址，连接设备
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }

    };

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt���ӳɹ�
            {
                mConnected = true;
                status = "connected";
                //更新连接状态
                updateConnectionState(status);
                System.out.println("BroadcastReceiver :" + "device connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//
                    .equals(action)) {
                mConnected = false;
                status = "disconnected";
                //更新连接状态
                updateConnectionState(status);
                System.out.println("BroadcastReceiver :"
                        + "device disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//
                    .equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                //获取设备的所有蓝牙服务
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
                System.out.println("BroadcastReceiver :"
                        + "device SERVICES_DISCOVERED");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //处理发送过来的数据

                String bluetoothDate = intent.getExtras().getString(
                        BluetoothLeService.EXTRA_DATA);
                String[] bluetoothDateArr = bluetoothDate.split("&");
                if (bluetoothDateArr.length == 4) {
                    Order order = ordersDao.getTimeOneDate(DateFormat.format("hh", System.currentTimeMillis()).toString());
                    if (order == null) {
                        ordersDao.insertDate(DateFormat.format("hh", System.currentTimeMillis()).toString(),
                                bluetoothDateArr[0], bluetoothDateArr[1], bluetoothDateArr[2], bluetoothDateArr[3]);
                    } else {
                        ordersDao.updateOrder(DateFormat.format("hh", System.currentTimeMillis()).toString(),
                                bluetoothDateArr[0], bluetoothDateArr[1], bluetoothDateArr[2], bluetoothDateArr[3]);
                    }
                } else {
                    System.out.print("BroadcastReceiver :" + "数据格式不对");
                }
                System.out.println("BroadcastReceiver onData:"
                        + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    /* 更新连接状态 */
    private void updateConnectionState(String status) {
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("connect_state", status);
        msg.setData(b);
        //将连接状态更新的UI的textview上
        myHandler.sendMessage(msg);
        System.out.println("connect_state:" + status);

    }

    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: displayGattServices
     * @Description: TODO(处理蓝牙服务)
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";

        // 服务数据,可扩展下拉列表的第一级数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // 特征数据（隶属于某一级服务下面的特征值集合）
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        // 部分层次，所有特征值集合
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            // 获取服务列表
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

            gattServiceData.add(currentServiceData);

            System.out.println("Service uuid:" + uuid);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

            // 从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString()
                        .equals(HEART_RATE_MEASUREMENT)) {
                    // 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
                    mhandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mBluetoothLeService
                                    .readCharacteristic(gattCharacteristic);
                        }
                    }, 200);

                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBluetoothLeService.setCharacteristicNotification(
                            gattCharacteristic, true);
                    target_chara = gattCharacteristic;
                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    // mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic
                        .getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    System.out.println("---descriptor UUID:"
                            + descriptor.getUuid());
                    // 获取特征值的描述
                    mBluetoothLeService.getCharacteristicDescriptor(descriptor);
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    // true);
                }

                gattCharacteristicGroupData.add(currentCharaData);
            }
            // 按先后顺序，分层次放入特征值集合中，只有特征值
            mGattCharacteristics.add(charas);
            // 构件第二级扩展列表（服务下面的特征值）
            gattCharacteristicData.add(gattCharacteristicGroupData);

        }

    }


    /*
      * 点击按键时间，跳转至图像界面
      */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recent_temperature:
                jumpBleActivity(1);
                break;
            case R.id.recent_humidity:
                jumpBleActivity(2);
                break;
            case R.id.recent_infrared:
                jumpBleActivity(3);
                break;
            case R.id.recent_smoke:
                jumpBleActivity(4);
                break;
        }
    }

    //跳转到图像详情界面
    public void jumpBleActivity(int jumpKind) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(Ble_Activity.this, DataChartActivity.class);
        bundle.putInt("jumpKind", jumpKind);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 2;  //消息(一个整型值)
                    myHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除广播接收器
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    // Activity出来时候，绑定广播接收器，监听蓝牙连接服务传过来的事件
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        //绑定广播接收器
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            //根据蓝牙地址，建立连接
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

}
