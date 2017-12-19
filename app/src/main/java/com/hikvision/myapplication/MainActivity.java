package com.hikvision.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SurfaceView v1, v2;
    VideoView mVideoView1;
    VideoView mVideoView2;
    public String wsUrl = "ws://192.168.1.119:9502";
    private LinearLayout customBarChart1, customBarChart2;
    int[] data1 = {300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500,
            700, 300, 500, 550, 500, 300, 700, 800, 750, 800};
    int[] data2 = {300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500,
            700, 300, 500, 550, 500, 300, 700, 800, 750, 800};

    TextView time_hour, time_year, week;

    private PopupWindowHelper popupWindowHelper, popupWindowHelper2;
    private View popView, popView2, rootView;
    View view;
    ImageView img_1, img_2, img_3;
    LinearLayout text;
    RelativeLayout rl;
    private ViewPager list_pager;

    private List<View> list_view;

    private viewpageAdapter adpter;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        initBarChart1();
        initBarChart2();
        //时间显示
        time_hour = (TextView) findViewById(R.id.time_hour);
        time_year = (TextView) findViewById(R.id.time_year);
        week = (TextView) findViewById(R.id.week);
        new TimeThread().start();

        popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview, null);
        popView2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupview2, null);
        new popoWindThread().start();
        new popoWindThread2().start();

        //滚动轮播
//        list_pager = (ViewPager) findViewById(R.id.list_pager);
//
//        list_view = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            View view = LayoutInflater.from(this).inflate(R.layout.fragment_page, null);
//            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//            mGridLayoutManager = new GridLayoutManager(this, 6);
//            mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
//            //设置固定大小
//            mRecyclerView.setHasFixedSize(true);
//            mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, 6, GridLayoutManager.VERTICAL, false));

//            MyAdapter mAdapter = new MyAdapter(data);
//            mRecyclerView.setAdapter(mAdapter);
//            list_view.add(view);
//        }

//        adpter = new viewpageAdapter(list_view);
//        list_pager.setAdapter(adpter);
//
//        // 刚开始的时候 吧当前页面是先到最大值的一半 为了循环滑动
//        int currentItem = Integer.MAX_VALUE / 2;
//        // 让第一个当前页是 0
//        //currentItem = currentItem - ((Integer.MAX_VALUE / 2) % 4);
//        list_pager.setCurrentItem(currentItem);

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
                    mHandler.sendEmptyMessageDelayed(2, 1000);
                    break;
                case 2:
                    img_2 = popView.findViewById(R.id.img_2);
                    img_2.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(3, 1000);
                    break;
                case 3:
                    rl = popView.findViewById(R.id.rl);
                    rl.setVisibility(View.VISIBLE);
                    text = popView.findViewById(R.id.ll_text);
                    text.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(4, 2000);
                    break;
                case 4:
//                    img_1 = popView.findViewById(R.id.img_1);
//                    img_1.setVisibility(View.GONE);
//                    img_2 = popView.findViewById(R.id.img_2);
//                    img_2.setVisibility(View.GONE);
//                    img_3 = popView.findViewById(R.id.img_3);
//                    img_3.setVisibility(View.GONE);
//                    text = popView.findViewById(R.id.ll_text);
//                    text.setVisibility(View.GONE);
//                    popupWindowHelper.dismiss();
                    break;
                case 11:
                    img_1 = popView2.findViewById(R.id.img_1);
                    img_1.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(22, 1000);
                    break;
                case 22:
                    img_2 = popView2.findViewById(R.id.img_2);
                    img_2.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(33, 1000);
                    break;
                case 33:
                    rl = popView2.findViewById(R.id.rl);
                    rl.setVisibility(View.VISIBLE);
                    text = popView2.findViewById(R.id.ll_text);
                    text.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(44, 2000);
                    break;
                case 44:
//                    img_1 = popView2.findViewById(R.id.img_1);
//                    img_1.setVisibility(View.GONE);
//                    img_2 = popView2.findViewById(R.id.img_2);
//                    img_2.setVisibility(View.GONE);
//                    img_3 = popView2.findViewById(R.id.img_3);
//                    img_3.setVisibility(View.GONE);
//                    text = popView2.findViewById(R.id.ll_text);
//                    text.setVisibility(View.GONE);
//                    popupWindowHelper2.dismiss();
                    break;
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
                        mHandler.sendEmptyMessageDelayed(1, 500);
                    } else if (msg.obj.equals("2")) {
                        popView2.setPadding(50, 0, 0, 0);
                        y = 200;
                        popupWindowHelper2 = new PopupWindowHelper(popView2);
                        popupWindowHelper2.showFromTopLeft(view, y);
                        mHandler.sendEmptyMessageDelayed(11, 500);
                    }

                    break;
                default:
                    break;
            }
        }
    };


    //启动弹出窗口线程
    public class popoWindThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(1000);
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

    private void initData() {
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
                                //获取0-23小时进入和离开的人数
                                JSONArray jsonArray = dataObject.getJSONArray("hoursStatistics");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsondata = jsonArray.getJSONObject(i);
                                    data1[i] = jsondata.getInt("in") * 50;
                                    data2[i] = jsondata.getInt("out") * 50;
                                }
                                //获取总楼层，总人数，每层楼总人数，每间寝室总人数
                                JSONObject countObject = dataObject.getJSONObject("list");

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
        int[] data1 = {300, 500, 550, 500, 300, 500, 600, 750, 550, 600, 400, 300, 400, 600, 500,
                700, 300, 500, 550, 500, 300, 700, 750, 750, 750};
        String[] xLabel = {"0", "00:00", "", "", "", "", "", "07:00", "", "", "", "", "12:00", "",
                "", "", "", "", "18:00", "", "", "", "22:00", "", ""};
        String[] yLabel = {"0", "0", "0", "0", "0", "0", "0", "0"};

        List<int[]> data = new ArrayList<>();
        data.add(data1);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color12);
        color.add(R.color.color13);
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
        int[] data2 = {3, 5, 5, 5, 3, 7, 8, 7, 5, 6, 4, 3, 4, 6, 5,
                7, 3, 5, 5, 5, 3, 7, 8, 7, 8};
        List<int[]> data = new ArrayList<>();
        data.add(data2);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color12);
        color.add(R.color.color13);
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
