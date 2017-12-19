package com.hikvision.myapplication;

import java.lang.ref.WeakReference;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

public class VideoView {

	
    private String mFilePath;	// 路径

    // display surface
    private SurfaceView mSurface;	// surfaceview组件
    private SurfaceHolder holder;	// surfaceview的接口

    // media player
    private LibVLC libvlc;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSarDen;
	private int mSarNum;
    private final static int VideoSizeChanged = -1;	// 标志符
    
    private Activity mContext;	
    
    
    /*************
     * Activity
     *************/
    /*
     * 设置surface的大小
     */
	IVideoPlayer mVideoPlayer = new IVideoPlayer(){


	    @Override
	    public void setSurfaceSize(int width, int height, int visible_width,
	            int visible_height, int sar_num, int sar_den) {
	    	mVideoHeight = height;
	        mVideoWidth = width;
	        mSarNum = sar_num;
	        mSarDen = sar_den;
	        Message msg = Message.obtain(mHandler, VideoSizeChanged);	
	        msg.sendToTarget();	//发送修改信息
	    }

		@Override
		public void eventHardwareAccelerationError() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
    /*************
     * Surface
     *************/
	/*
	 * surface的接口实现
	 */
	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
	    }

	    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
	            int width, int height) {
	        if (libvlc != null)
	            libvlc.attachSurface(holder.getSurface(), mVideoPlayer);
	    }

	    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	    	libvlc.destroy();
	    }
	};

	
    /*************
     * Events
     *************/

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<VideoView> mOwner;	//弱引用

        public MyHandler(VideoView owner) {
            mOwner = new WeakReference<VideoView>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
        	VideoView player = mOwner.get();

            // SamplePlayer events
            if (msg.what == VideoSizeChanged) {
                player.setSize();
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();	//获得传递的信息
            switch (b.getInt("event")) {
            case EventHandler.MediaPlayerEndReached:
                player.releasePlayer();
                break;
            case EventHandler.MediaPlayerPlaying:
            case EventHandler.MediaPlayerPaused:
            case EventHandler.MediaPlayerStopped:
            default:
                break;
            }
        }
    }

    /*
     * 重构ViedoView方法
     */
    public VideoView(SurfaceView view, String url, Activity ac){
    	
    	mContext  =ac;
    	mFilePath = url;
        mSurface = view;
        
        holder = mSurface.getHolder();
        holder.addCallback(mSHCallback);	//添加回调函数
        
        // 设置焦点  如果不设置焦点的话 在该界面下 点击触摸屏是无效的 默认为false
        mSurface.setFocusableInTouchMode(true);
        
    }
    /*
     * 创建play
     */
    public void createPlayer() {
    	String media = mFilePath;
//        releasePlayer();
        try {

            // Create a new media player
            libvlc = new LibVLC();
            libvlc.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DECODING); // 设置硬件编码	
            libvlc.setSubtitlesEncoding("");
            libvlc.setAout(LibVLC.AOUT_OPENSLES);
            libvlc.setTimeStretching(true);
            libvlc.setChroma("RV32");
            libvlc.setVerboseMode(true);
            libvlc.restart(mContext);
            EventHandler.getInstance().addHandler(mHandler);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            MediaList list = libvlc.getMediaList();
            list.clear();
            list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);	//添加media
            libvlc.playIndex(0);
        } catch (Exception e) {
        }
    }
    /*
     * 停止play
     */
    public void stopPlayer() {
		libvlc.stop();
    }
    /*
     * 暂停后，再次播放play
     */
    public void replayPlayer() {
		libvlc.play();
    }
    /*
     * 销毁play
     */
    public void releasePlayer() {
        if (libvlc == null)
            return;
        EventHandler.getInstance().removeHandler(mHandler);
        libvlc.stop();
        libvlc.detachSurface();
        holder = null;
        libvlc.closeAout();
        libvlc.destroy();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    /*
     * 改变surface大小
     */
    private void setSize() {
//        mVideoWidth = width;
//        mVideoHeight = height;
//        if (mVideoWidth * mVideoHeight <= 1)
//            return;

        // get screen size
        int w = 912;
        int h = 684;

        
        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }
        if (w * h == 0)
			return;

        double videoAR, vw;
		double density = (double) mSarNum / (double) mSarDen;
		if (density == 1.0) {
			/* No indication about the density, assuming 1:1 */
			vw = mVideoWidth;
			videoAR = (double) mVideoWidth / (double) mVideoHeight;
		} else {
			/* Use the specified aspect ratio */
			vw = mVideoWidth * density;
			videoAR = (double) vw / (double) mVideoHeight;
		}

		// compute the display aspect ratio
		double screenAR = (double) w / (double) h;
        
//        double videoAR = (double) mVideoWidth / (double) mVideoHeight;
//        double screenAR = (double) w / (double) h;

		
        if (screenAR < videoAR) {
            h = (int) (w / videoAR);
        }
        else {
            w = (int) (h * videoAR);
        }

		
        // force surface buffer size
        holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        LayoutParams lp = mSurface.getLayoutParams();
        
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();	//重绘mSurface
    }

    
}
