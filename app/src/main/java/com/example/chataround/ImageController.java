package com.example.chataround;

import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ImageController {

    public static String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedString;
    }

    public static Bitmap ResizeImage(Bitmap bitmap, int maxSize){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float)width/(float)height;
        if(ratio>1){
            width = maxSize;
            height = (int)(width/ratio);
        }else{
            height = maxSize;
            width = (int)(height*ratio);
        }
        return Bitmap.createScaledBitmap(bitmap,width,height,true);
    }

    public static byte[] BitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
}
