package com.hikvision.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    SurfaceView v1, v2;
    VideoView mVideoView1;
    VideoView mVideoView2;
    //    public String wsUrl = "ws://192.168.1.119:9502";
    public String wsUrl = "ws://101.201.28.83:86";
    private LinearLayout customBarChart1, customBarChart2;
    int[] data1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int[] data2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    TextView time_hour, time_year, week;

    private PopupWindowHelper popupWindowHelper, popupWindowHelper2, popupWindowHelper3;
    private View popView, popView2, popView3;
    View view;
    ImageView img_1, img_2, img_3;
    LinearLayout text;
    RelativeLayout rl;

    String noticeContent = "", noticeId = "", studentId = "", studentId2 = "", studentId3 = "", studentId4 = "", studentId5 = "";
    String imageUrl, weather;
    Bitmap bitmapWeather;
    WebView webView;
    int tag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化保存noticeId
        SharedPreferences notice = getSharedPreferences("noticeId", MODE_PRIVATE);
        SharedPreferences.Editor edit = notice.edit(); //编辑文件
        edit.putString("id", "0");
        edit.commit();  //保存数据信息

        //studentId
        SharedPreferences sId = getSharedPreferences("studentId", MODE_PRIVATE);
        SharedPreferences.Editor edit1 = sId.edit(); //编辑文件
        edit1.putString("id", "0");
        edit1.commit();

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/html/index.html");

        view = View.inflate(this, R.layout.activity_main, null);
        v1 = (SurfaceView) findViewById(R.id.v1);
        v2 = (SurfaceView) findViewById(R.id.v2);
        String video1 = "rtsp://admin:admin@192.168.1.6:554";
        String video2 = "rtsp://admin:Abcd1234@192.168.1.4:554";
        mVideoView1 = new VideoView(v1, video1, this);
        mVideoView2 = new VideoView(v2, video2, this);
        mVideoView1.createPlayer();
        mVideoView2.createPlayer();
        customBarChart1 = (LinearLayout) findViewById(R.id.customBarChart1);
        customBarChart2 = (LinearLayout) findViewById(R.id.customBarChart2);
        initData();
//        initData2();
        initId();
//        new TimeGetIdThread().start();

        initBarChart1();
        initBarChart2();
        //时间显示
        time_hour = (TextView) findViewById(R.id.time_hour);
        time_year = (TextView) findViewById(R.id.time_year);
        week = (TextView) findViewById(R.id.week);
        new TimeThread().start();

    }

    private void initId() {
        String url = "http://192.168.1.122:8080/";
        com.loopj.android.http.AsyncHttpClient client = new com.loopj.android.http.AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray id = response.getJSONArray("id");
                    Log.d("id", id.toString());
                    if (id.length() == 0) {
                        initId();
                    } else {
                        SharedPreferences getSId = getSharedPreferences("studentId", 0);
                        String sid = getSId.getString("id", "0");
                        Log.d("sid", "......" + sid);
                        switch (1) {
                            case 1:
                                studentId = id.get(0).toString().replace("\"", "");
                                //保存数据信息
                                if (sid.equals(studentId)) {
                                    initId();
                                } else {
                                    tag = 1;
                                    SharedPreferences sId2 = getSharedPreferences("studentId", MODE_PRIVATE);
                                    SharedPreferences.Editor edit2 = sId2.edit(); //编辑文件
                                    edit2.putString("id", studentId);
                                    edit2.commit();
                                    Toast.makeText(MainActivity.this, studentId, Toast.LENGTH_LONG).show();
                                    popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview, null);
                                    new popoWindThread().start();

                                }
                                break;
                            case 2:
                                studentId = id.get(0).toString().replace("\"", "");
                                Toast.makeText(MainActivity.this, studentId, Toast.LENGTH_LONG).show();
                                popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview, null);
                                new popoWindThread().start();
                                studentId2 = id.get(1).toString().replace("\"", "");
                                popView2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview2, null);
                                new popoWindThread2().start();
                                initId();
                                break;
                            case 3:
                                studentId = id.get(0).toString().replace("\"", "");
                                Toast.makeText(MainActivity.this, studentId, Toast.LENGTH_LONG).show();
                                popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview, null);
                                new popoWindThread().start();

                                studentId2 = id.get(1).toString().replace("\"", "");
                                popView2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview2, null);
                                new popoWindThread2().start();

                                studentId3 = id.get(2).toString().replace("\"", "");
                                popView3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview2, null);
                                new popoWindThread3().start();
                                initId();
                                break;
                            default:
                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 1:
                    img_1 = popView.findViewById(R.id.img_1);
                    img_1.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(2, 2000);
                    break;
                case 2:
                    img_1 = popView.findViewById(R.id.img_1);
                    img_1.setVisibility(View.VISIBLE);
                    img_1.setImageDrawable(getResources().getDrawable(R.drawable.img_moshengrenshibei));
//                    img_2 = popView.findViewById(R.id.img_2);
//                    img_2.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(3, 3000);
                    break;
                case 3:
//                    rl = popView.findViewById(R.id.rl);
//                    rl.setVisibility(View.VISIBLE);
//                    path = Environment.getExternalStorageDirectory() ; //获得SDCard目录
//                    String sdDir = Environment.getExternalStorageDirectory().getPath();
//                    Log.d("jpgUrl", sdDir + "/school/" + studentId + ".jpg");
//                    img_3 = popView.findViewById(R.id.img_3);
//                    img_3.setImageURI(Uri.fromFile(new File(sdDir + "/school/" + studentId + ".jpg")));
//                    text = popView.findViewById(R.id.ll_text);
//                    text.setVisibility(View.VISIBLE);
                    img_1 = popView.findViewById(R.id.img_1);
                    img_1.setVisibility(View.VISIBLE);
                    String sdDir = Environment.getExternalStorageDirectory().getPath();
                    img_1.setImageURI(Uri.fromFile(new File(sdDir + "/school/" + studentId + ".jpg")));
                    text = popView.findViewById(R.id.ll_text);
                    text.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(4, 5000);
                    break;
                case 4:
//                    img_1 = popView.findViewById(R.id.img_1);
//                    img_2 = popView.findViewById(R.id.img_2);
//                    img_2.setVisibility(View.INVISIBLE);
//                    img_3 = popView.findViewById(R.id.img_3);
//                    img_3.setVisibility(View.INVISIBLE);
//                    String sdDir2 = Environment.getExternalStorageDirectory().getPath();
//                    img_1.setImageURI(Uri.fromFile(new File(sdDir2 + "/school/" + studentId + ".jpg")));
//                    mHandler.sendEmptyMessageDelayed(5, 500);
                    popupWindowHelper.dismiss();
                    if (tag == 1) {
                        initId();
                    }
                    break;
                case 5:
                    popupWindowHelper.dismiss();
                    if (tag == 1) {
                        initId();
                    }
                    break;
//                case 11:
//                    img_1 = popView2.findViewById(R.id.img_1);
//                    img_1.setVisibility(View.VISIBLE);
//                    mHandler.sendEmptyMessageDelayed(22, 300);
//                    break;
//                case 22:
//                    img_2 = popView2.findViewById(R.id.img_2);
//                    img_2.setVisibility(View.VISIBLE);
//                    mHandler.sendEmptyMessageDelayed(33, 300);
//                    break;
//                case 33:
//                    rl = popView2.findViewById(R.id.rl);
//                    rl.setVisibility(View.VISIBLE);
//                    String sdDirr = Environment.getExternalStorageDirectory().getPath();
//                    img_3 = popView2.findViewById(R.id.img_3);
//                    img_3.setImageURI(Uri.fromFile(new File(sdDirr + "/school/" + studentId2 + ".jpg")));
//
//                    text = popView2.findViewById(R.id.ll_text);
//                    text.setVisibility(View.VISIBLE);
//                    mHandler.sendEmptyMessageDelayed(44, 200);
//                    break;
//                case 44:
//
//                    popupWindowHelper2.dismiss();
//                    break;
//                case 111:
//                    img_1 = popView3.findViewById(R.id.img_1);
//                    img_1.setVisibility(View.VISIBLE);
//                    mHandler.sendEmptyMessageDelayed(222, 300);
//                    break;
//                case 222:
//                    img_2 = popView3.findViewById(R.id.img_2);
//                    img_2.setVisibility(View.VISIBLE);
//
//                    mHandler.sendEmptyMessageDelayed(333, 300);
//                    break;
//                case 333:
//                    rl = popView3.findViewById(R.id.rl);
//                    rl.setVisibility(View.VISIBLE);
//                    String sdDir3 = Environment.getExternalStorageDirectory().getPath();
//                    img_3 = popView3.findViewById(R.id.img_3);
//                    img_3.setImageURI(Uri.fromFile(new File(sdDir3 + "/school/" + studentId3 + ".jpg")));
//                    text = popView3.findViewById(R.id.ll_text);
//                    text.setVisibility(View.VISIBLE);
//                    mHandler.sendEmptyMessageDelayed(444, 200);
//                    break;
//                case 444:
//                    popupWindowHelper3.dismiss();
//                    break;
                case 6:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                    final Calendar c = Calendar.getInstance();
                    String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
                    if ("1".equals(mWay)) {
                        mWay = "周日";
                    } else if ("2".equals(mWay)) {
                        mWay = "周一";
                    } else if ("3".equals(mWay)) {
                        mWay = "周二";
                    } else if ("4".equals(mWay)) {
                        mWay = "周三";
                    } else if ("5".equals(mWay)) {
                        mWay = "周四";
                    } else if ("6".equals(mWay)) {
                        mWay = "周五";
                    } else if ("7".equals(mWay)) {
                        mWay = "周六";
                    }
                    time_hour.setText(format1.format(date));
                    time_year.setText(format2.format(date));
                    week.setText(mWay);
                    break;
                case 7:
                    int y = 0;
                    popView.setPadding(50, 0, 0, 0);
                    if (msg.obj.equals("1")) {
                        y = 120;
                        popupWindowHelper = new PopupWindowHelper(popView);
                        popupWindowHelper.showFromTopLeft(view, y);
                        mHandler.sendEmptyMessageDelayed(1, 300);
                    } else if (msg.obj.equals("2")) {
                        popView2.setPadding(50, 0, 0, 0);
                        y = 200;
                        popupWindowHelper2 = new PopupWindowHelper(popView2);
                        popupWindowHelper2.showFromTopLeft(view, y);
                        mHandler.sendEmptyMessageDelayed(11, 300);
                    } else if (msg.obj.equals("3")) {
                        popView3.setPadding(50, 0, 0, 0);
                        y = 280;
                        popupWindowHelper3 = new PopupWindowHelper(popView3);
                        popupWindowHelper3.showFromTopLeft(view, y);
                        mHandler.sendEmptyMessageDelayed(111, 300);
                    }

                    break;
                case 8:
                    MarqureeTextView noticeText = (MarqureeTextView) findViewById(R.id.notice);
                    noticeText.setText(noticeContent);
                    break;
                case 9:
                    ImageView tianqi_img = (ImageView) findViewById(R.id.tianqi_img);
                    TextView tianqi_text = (TextView) findViewById(R.id.tianqi_text);

                    tianqi_img.setImageBitmap(bitmapWeather);
                    tianqi_text.setText(weather);
                    break;
                case 10:
                    initId();
                    break;
                default:
                    break;
            }
        }
    };

    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    //启动弹出窗口线程
    public class popoWindThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(1);
                Message msg = new Message();
                msg.what = 7;
                msg.obj = "1";
                mHandler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class popoWindThread2 extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(3000);
                Message msg = new Message();
                msg.what = 7;
                msg.obj = "2";
                mHandler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class popoWindThread3 extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(5000);
                Message msg = new Message();
                msg.what = 7;
                msg.obj = "3";
                mHandler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class TimeGetIdThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(2000);
                    Message msg = new Message();
                    msg.what = 10;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 6;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    public void initData2() {
        AsyncHttpClient.getDefaultInstance().websocket(wsUrl, "my-protocol", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                webSocket.send(jsonMacData());
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        System.out.println("I got a string: " + s);
                    }
                });
            }
        });
    }

    public void initData() {
        AsyncHttpClient.getDefaultInstance().websocket(wsUrl, "my-protocol", new AsyncHttpClient.WebSocketConnectCallback() {

            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                Log.d("mac==>>", jsonMacData());
                webSocket.send(jsonMacData());
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        System.out.println("I got a string: " + s);
                        try {
                            JSONObject object = new JSONObject(s);
                            if (object.has("data")) {
                                JSONObject dataObject = object.getJSONObject("data");
                                //获取天气情况
                                JSONObject weatherinfoObject = dataObject.getJSONObject("weatherinfo");
                                if (weatherinfoObject.length() != 0) {
                                    imageUrl = weatherinfoObject.getString("img2");
                                    weather = weatherinfoObject.getString("weather");
                                    if (weather.equals("晴")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.qing);
                                    } else if (weather.equals("多云")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.duoyun);
                                    } else if (weather.equals("阴")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.yin);
                                    } else if (weather.equals("阵雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.zhenyu);
                                    } else if (weather.equals("小雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoyu);
                                    } else if (weather.equals("中雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.zhongyu);
                                    } else if (weather.equals("大雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.dayu);
                                    } else if (weather.equals("雷阵雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.leizhenyu);
                                    } else if (weather.equals("暴雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.baoyu);
                                    } else if (weather.equals("大暴雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.dabaoyu);
                                    } else if (weather.equals("特大暴雨")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.tedabaoyu);
                                    } else if (weather.equals("雨夹雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.yujiaxue);
                                    } else if (weather.equals("阵雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.zhenxue);
                                    } else if (weather.equals("小雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoxue);
                                    } else if (weather.equals("中雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.zhongxue);
                                    } else if (weather.equals("大雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.daxue);
                                    } else if (weather.equals("暴雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.baoxue);
                                    } else if (weather.equals("雾")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.wu);
                                    } else if (weather.equals("雾霾")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.wumai);
                                    } else if (weather.equals("冰雹和冻雪")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.bingbaohedongyu);
                                    } else if (weather.equals("沙尘暴")) {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.shaochengbao);
                                    } else {
                                        bitmapWeather = BitmapFactory.decodeResource(getResources(), R.drawable.duoyun);
                                    }
                                    Message msg = new Message();
                                    msg.what = 9;
                                    mHandler.sendMessage(msg);
                                }
                                //获取0-23小时进入和离开的人数
                                JSONArray jsonArray = dataObject.getJSONArray("hoursStatistics");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsondata = jsonArray.getJSONObject(i);
                                    data1[i] = jsondata.getInt("in") * 40;
                                    data2[i] = jsondata.getInt("out") * 40;
                                }
                                //获取通知
                                SharedPreferences getId = getSharedPreferences("noticeId", 0);
                                String id = getId.getString("id", "0");
                                JSONObject noticeObject = dataObject.getJSONObject("notice");
                                if (noticeObject.getString("id").equals("" + id)) {
                                    Log.d("noticeObject", "");
                                    noticeContent = "暂时没有通知";
                                    Message msg = new Message();
                                    msg.what = 8;
                                    mHandler.sendMessage(msg);
                                } else {
                                    noticeId = noticeObject.getString("id");
                                    SharedPreferences notice = getSharedPreferences("noticeId", MODE_PRIVATE);
                                    SharedPreferences.Editor edit = notice.edit(); //编辑文件
                                    edit.putString("id", noticeId);
                                    edit.commit();

                                    noticeContent = noticeObject.getString("content");

                                    Log.d("noticeContent", noticeContent + ":" + noticeId + ":" + id);

                                    if (!noticeId.equals(id)) {
                                        Message msg = new Message();
                                        msg.what = 8;
                                        mHandler.sendMessage(msg);
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 初始化柱状图1数据
     */
    private void initBarChart1() {

        String[] xLabel = {"0", "00:00", "", "", "", "", "", "07:00", "", "", "", "", "12:00", "",
                "", "", "", "", "18:00", "", "", "", "22:00", "", ""};
        String[] yLabel = {"0", "0", "0", "0", "0", "0", "0", "0"};

        List<int[]> data = new ArrayList<>();
        data.add(data1);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color12);
        color.add(R.color.color16);
        color.add(R.color.color16);
        customBarChart1.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }

    /**
     * 初始化柱状图2数据
     */
    private void initBarChart2() {
        String[] xLabel = {"0", "00:00", "", "", "", "", "", "07:00", "", "", "", "", "12:00", "",
                "", "", "", "", "18:00", "", "", "", "22:00", "", ""};
        String[] yLabel = {"0", "0", "0", "0", "0", "0", "0", "0"};
//        int[] data2 = {3, 5, 5, 5, 3, 7, 8, 7, 5, 6, 4, 3, 4, 6, 5,
//                7, 3, 5, 5, 5, 3, 7, 8, 7, 8};
        List<int[]> data = new ArrayList<>();
        data.add(data2);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color12);
        color.add(R.color.color16);
        color.add(R.color.color16);
        customBarChart2.addView(new CustomBarChart(this, xLabel, yLabel, data, color));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView1.replayPlayer();
        mVideoView2.replayPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView1.stopPlayer();
        mVideoView2.stopPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView1.releasePlayer();
        mVideoView2.releasePlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public String jsonMacData() {
        String jsonresult = "";//定义返回字符串
//        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
            JSONObject jsonObj = new JSONObject();//pet对象，json形式
            jsonObj.put("sushe", "1_1");//向pet对象里面添加值
            // 把每个数据当作一对象添加到数组里
//            jsonarray.put(jsonObj);//向json数组里面添加pet对象
//            object.put("data", jsonarray);//向总对象里面添加包含pet的数组
            jsonresult = jsonObj.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("生成的json串为:", jsonresult);
        return jsonresult;
    }


    //防止RecyclerView在刷新数据的时候会出现异常，导致崩溃
    public class WrapContentLinearLayoutManager extends GridLayoutManager {


        public WrapContentLinearLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            try {
                super.onLayoutCompleted(state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

    }
}
