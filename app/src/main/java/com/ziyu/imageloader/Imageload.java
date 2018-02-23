package com.ziyu.imageloader;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载类
 */
public class Imageload {
    private static final String TAG="Imageload";

    private static Context mContext;
    private static Imageload sImageload;
    /**
     * UI线程handler
     */
    private Handler mUIhandler;
    /**
     * 线程池线程 handler
     */
    private Handler mPoolHandler;
    /**
     * 内存缓存
     */
    private LruCache<String,Bitmap> mMemoryCache;

    private Imageload(){
        //获取应用最大可用内存
        int maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheMemory=maxMemory/8;
        mMemoryCache=new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //缓存图片的大小
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };

        //开启处理下载任务线程
        new Thread(new PoolTask()).start();
    }

    public static synchronized Imageload getInstance(Context context){
        mContext=context;
        if(sImageload==null){
            sImageload=new Imageload();
        }
        return sImageload;
    }

    public void load(String url, final ImageView imageView){
        load(url,imageView,0,0);
    }

    /**
     * 加载图片
     * @param url
     * @param imageView
     */
    public void load(String url, final ImageView imageView, final int reqWidth, final int reqHeight){

        Bitmap cacheBitmap;

        //先从内存缓存取图片
        cacheBitmap=getBitmapFromMemCache(url);
        if(cacheBitmap!=null){
            imageView.setImageBitmap(cacheBitmap);
            return;
        }
        //设置默认图片
        imageView.setImageResource(R.mipmap.image_loading);
        //设置标记
        imageView.setTag(url);

        if(mUIhandler==null){
            mUIhandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    ImageMsg imageMsg= (ImageMsg) msg.obj;
                    Log.i(TAG,"request finish >>>>>>>>"+imageMsg.url);
                    if(imageMsg.imageView.getTag().toString().equals(imageMsg.url)){
                        Bitmap bitmap=imageMsg.bitmap;
                        if(bitmap!=null){
                            addBitmapToMemoryCache(imageMsg.url,bitmap);
                            imageMsg.imageView.setImageBitmap(bitmap);
                        }else {
                            Log.i(TAG,"bitmap is null");
                        }
                    }
                }
            };
        }

        DownloadTask task=new DownloadTask(url,imageView,reqWidth,reqHeight);
        if(mPoolHandler!=null){
            Message message=new Message();
            message.obj=task;
            mPoolHandler.sendMessage(message);
        }else {
            Log.i(TAG,"pool handler is null,cannot download image");
        }
    }

    /**
     * 内存缓存：把图片添加到缓存
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key,Bitmap bitmap){

        if(getBitmapFromMemCache(key)==null&&bitmap!=null){
            mMemoryCache.put(key,bitmap);
        }

    }

    /**
     * 内存缓存：得到缓存图片
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key){
        return mMemoryCache.get(key);
    }

    /**
     *用线程池方式处理下载任务
     */
    class PoolTask implements Runnable{

        //线程池
        private ExecutorService pool= Executors.newFixedThreadPool(3);

        @Override
        public void run() {
            Looper.prepare();
            mPoolHandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    DownloadTask task= (DownloadTask) msg.obj;
                    pool.execute(task);
                }
            };

            //开启循环处理
            Looper.loop();
        }
    }

    /**
     * 图片下载task
     */
    class DownloadTask implements Runnable{

        private String url;
        private ImageView mImageView;
        private int reqWidth;
        private int reqHeight;

        public DownloadTask(String url, ImageView imageView, int reqWidth, int reqHeight) {
            this.url = url;
            mImageView = imageView;
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
        }

        @Override
        public void run() {
            Log.i(TAG,"start request image >>>>>>>"+url);
            Bitmap bitmap;
            if(reqWidth<=0||reqHeight<=0){
                bitmap=Utils.downloadImageByUrl(url,mImageView);
            }else {
                bitmap=Utils.downloadImageByUrl(url,reqWidth,reqHeight);
            }
            ImageMsg imageMsg=new ImageMsg();
            imageMsg.url=url;
            imageMsg.bitmap=bitmap;
            imageMsg.imageView=mImageView;
            Message message=new Message();
            message.obj=imageMsg;

            mUIhandler.sendMessage(message);
        }
    }


    private class ImageMsg{
        String url;
        Bitmap bitmap;
        ImageView imageView;
    }
}
