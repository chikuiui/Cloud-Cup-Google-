package com.example.cloudcup;


//?????????????????????????????????????????????????????? learn about bitmap and other things
// some utility functions that cn be called from AsyncTask.doInBackground() to fetch and decode images into Bitmaps.

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class BitmapUtils {

    private static final int STREAM_BUFFER_SIZE = 64 * 1024;

    // This class provides only static methods , so constructor should be private.
    private BitmapUtils(){}

    public static int calculateInSampleImage(int srcWidth,int srcHeight,int reqWidth,int reqHeight){
        if((reqHeight > 0) && (reqWidth > 0) && (srcHeight > reqHeight) && (srcWidth > reqWidth)){
            return Math.min(srcWidth/reqWidth,srcHeight/reqHeight);
        }
        return 1;
    }

    // Acquire input stream for the image resource identified by uri
    // this log running I/O operation that must run in a background thread.
    public static InputStream getInputStream(Context context, Uri uri) throws IOException{
        if(uri.getScheme().contentEquals(ContentResolver.SCHEME_CONTENT)){
            return context.getContentResolver().openInputStream(uri);
        }
        return (InputStream) new URL(uri.toString()).getContent();
    }

    // Decode image from inputStream into a new Bitmap of specified dimensions
    public static Bitmap decodeBitmapBounded(InputStream is,int maxWidth,int maxHeight) throws IOException{
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is,STREAM_BUFFER_SIZE);

        try {
            bufferedInputStream.mark(STREAM_BUFFER_SIZE);// should be enough to read image dimensions.

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds =true;


            BitmapFactory.decodeStream(bufferedInputStream,null,bmOptions);
            bufferedInputStream.reset();

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = calculateInSampleImage(bmOptions.outWidth,bmOptions.outHeight,maxWidth,maxHeight);

            return BitmapFactory.decodeStream(bufferedInputStream,null,bmOptions);
        }finally {
            bufferedInputStream.close();
        }
    }

}
