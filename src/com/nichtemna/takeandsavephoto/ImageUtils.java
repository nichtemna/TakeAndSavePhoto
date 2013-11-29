package com.nichtemna.takeandsavephoto;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shishova Galina
 * nichtemna@gmail.com
 */
public class ImageUtils {
    private static final int MEDIA_TYPE_IMAGE = 100;
    private static final int IMAGE_MAX_SIZE = 500;
    public static final String TAKE_AND_SAVE_PHOTO = "TakeAndSavePhoto";

    public static Bitmap rescaleBitmap(Bitmap bitmap) {
        int scale = 1;
        if (bitmap.getHeight() > IMAGE_MAX_SIZE || bitmap.getWidth() > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(bitmap.getHeight(), bitmap.getWidth())) / Math.log(0.5)));
        }
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
    }

    public static Bitmap rotate(Bitmap bitmap, int degree, boolean flip) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        if (flip) {
            matrix.preScale(-1, 1);
        }
        matrix.preRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAKE_AND_SAVE_PHOTO);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            mediaFile = null;
        }
        return mediaFile;
    }
}
