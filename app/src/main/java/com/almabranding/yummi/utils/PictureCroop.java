package com.almabranding.yummi.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.almabranding.yummi.R;

import java.io.File;
import java.util.List;

public class PictureCroop {
    static int SizeH;
    static int SizeW;


    public static final Uri CROPPED_IMAGE_URI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "cropped_image.jpg"));
    public static final Uri CROPPED_IMAGE_FILE_URI1 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "file_cropped_image1.jpg"));
    public static final Uri CROPPED_IMAGE_FILE_URI2 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "file_cropped_image2.jpg"));
    public static final Uri CROPPED_IMAGE_FILE_URI3 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "file_cropped_image3.jpg"));
    public static final Uri CROPPED_IMAGE_FILE_URI4 = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "file_cropped_image4.jpg"));
    public static final Uri TAKEN_PHOTO_URI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmpPic.jpg"));
    public static final String TAKEN_PHOTO_EXSTENSION = "7ai3";
    public static final String TAKEN_PHOTO_CODE = "93a-d914-11e4-9c1c-000c292dcee5";

    private static Bitmap setScaleBitmap(Context context, Bitmap bm) {
        int scale;
        if (bm != null) {
            if (bm.getWidth() - SizeW > bm.getHeight() - SizeH) {
                scale = (bm.getHeight() - SizeH);
            } else {
                scale = (bm.getWidth() - SizeW);
            }

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth() - scale, bm.getHeight() - scale, false);

            return resizedBitmap;
        }
        return null;

    }


    public static Bitmap getCroopedBitmap(Context context, Bitmap bm, boolean square) {

        if (!square) {
            SizeH = (int) (context.getResources().getDimension(R.dimen.photo_size_width));
            SizeW = (int) (context.getResources().getDimension(R.dimen.photo_size_width));
        } else {
            SizeH = (int) (context.getResources().getDimension(R.dimen.photo_size_square));
            SizeW = (int) (context.getResources().getDimension(R.dimen.photo_size_square));
        }

        bm = setScaleBitmap(context, bm);

        float width = bm.getWidth();
        float height = bm.getHeight();
        float startx = width - SizeW;
        float starty = height - SizeH;

        //chceme stred takze lava a prava strana su rovnake
        if (startx > 0) startx = startx / 2;
        if (starty > 0) starty = starty / 2;

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, (int) startx, (int) starty, SizeW, SizeH);

//        TypedFile in = new TypedFile("image/jpeg", resizedBitmap);

        return resizedBitmap;
    }


    public static final int CROP_FROM_CAMERA1 = 231;
    public static final int CROP_FROM_CAMERA2 = 232;
    public static final int CROP_FROM_CAMERA3 = 233;
    public static final int CROP_FROM_CAMERA4 = 234;


    public static void doCrop(Uri path, android.support.v4.app.Fragment act, int flag) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(path, "image/*");

        List<ResolveInfo> list = act.getActivity().getPackageManager().queryIntentActivities(intent, 0);

        intent.putExtra("scale", true);
        intent.putExtra("outputX", 1000);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CROPPED_IMAGE_URI);

        Intent i = new Intent(intent);
        ResolveInfo res = list.get(0);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        act.startActivityForResult(i, flag);
    }


    public static void doCrop(Uri path, Activity act, int flag) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(path, "image/*");

        List<ResolveInfo> list = act.getPackageManager().queryIntentActivities(intent, 0);

        intent.putExtra("scale", true);
        intent.putExtra("outputX", 1000);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CROPPED_IMAGE_URI);

        Intent i = new Intent(intent);
        ResolveInfo res = list.get(0);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        act.startActivityForResult(i, flag);
    }


    public static Bitmap finalizeBmp(String path, Context context) {
        Bitmap bmp = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        options.inScreenDensity = metrics.densityDpi;
        options.inTargetDensity = metrics.densityDpi;
        options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        Toast.makeText(context, String.valueOf(BitmapFactory.decodeFile(path, options).getWidth()), Toast.LENGTH_LONG).show();

        options.inSampleSize = 1;

        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path, options);


        try {
            ExifInterface exif = new ExifInterface(path);
            Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                bmp = rotate(bmp, 90);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                bmp = rotate(bmp, 270);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                bmp = rotate(bmp, 180);
            }
        } catch (Exception e) {

        }
        return bmp;
    }


    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static void clear_temp_images() {
        File taken_photo = new File(PictureCroop.TAKEN_PHOTO_URI.getPath());
        File cropped_photo = new File(PictureCroop.CROPPED_IMAGE_URI.getPath());

        if (taken_photo.exists())
            taken_photo.delete();

        if (cropped_photo.exists())
            cropped_photo.delete();
    }



    public static void recycleBitmapIfCan(Bitmap bmp){
        if(bmp!=null){
            try {
                if (!bmp.isRecycled()) {
                    bmp.recycle();
                }
                bmp = null;
                System.gc();
            }catch (Exception e){
                e.printStackTrace();
                bmp = null;
            }
        }
    }

}
