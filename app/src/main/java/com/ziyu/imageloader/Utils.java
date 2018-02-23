package com.ziyu.imageloader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    /**
     * 从网络下载图片
     *
     * @param url
     * @return
     */
    public static Bitmap downloadImageByUrl(String url,ImageView imageView){
        URL imageUrl=null;
        Bitmap bitmap=null;
        InputStream is=null;
        try {
            imageUrl=new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection= (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                is=connection.getInputStream();
                //压缩图片
                bitmap= decodeSampledBitmap(is,imageView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    /**
     * 从网络读取图片
     *
     * @param url
     * @return
     */
    public static Bitmap downloadImageByUrl(String url,int reqWidth,int reqHeight){
        URL imageUrl=null;
        Bitmap bitmap=null;
        InputStream is=null;
        try {
            imageUrl=new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection= (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                is=connection.getInputStream();
                //压缩图片
                bitmap= decodeSampledBitmap(is,reqWidth,reqHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }


    /**
     * 根据ImageView的大小压缩图片
     *
     * @param is
     * @param imageView
     * @return
     */
    private static Bitmap decodeSampledBitmap(InputStream is, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //转换为数组，解决问题：使用BitmapFactory.decodeStream方式压缩图片时，返回的bitmap总是为null
        byte[] data=inputStream2ByteArr(is);
        BitmapFactory.decodeByteArray(data, 0,data.length, options);

        ImageSize imageSize = getImageViewSize(imageView);
        //计算采样率
        options.inSampleSize = calculateInSampleSize(options, imageSize.width, imageSize.height);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length, options);

        return bitmap;
    }

    /**
     * 根据给定大小压缩图片
     *
     * @param is
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static Bitmap decodeSampledBitmap(InputStream is, int reqWidth,int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        byte[] data=inputStream2ByteArr(is);
        BitmapFactory.decodeByteArray(data, 0,data.length, options);
        //计算采样率
        options.inSampleSize = calculateInSampleSize(options, reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length, options);

        return bitmap;
    }

    private static byte[] inputStream2ByteArr(InputStream inputStream){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }


    //计算采样率
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if(reqWidth<=0||reqHeight<=0){
            return inSampleSize;
        }

        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 获取ImageView的高和宽
     *
     * @param imageView
     * @return
     */
    private static ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();

        DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();

        int width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width;
        }
        if (width <= 0) {
            width = imageView.getMaxWidth();
        }
        if (width <= 0) {
            width = metrics.widthPixels;
        }

        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {
            height = imageView.getMaxHeight();
        }
        if (height <= 0) {
            height = metrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private static class ImageSize {
        int width;
        int height;
    }
}
