package com.tablet.moran.tools.photos;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.tablet.moran.tools.SLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by tristan on 2015/8/3 0003.
 */
public class PhotoUtils {
    /**
     * 因为处理不同
     *
     * @param takePhotoCode
     * Uri originalUri = data.getData();
     * image=ImageUtil.getBitmapFromUrl(originalUri.toString());
     * *****
     * **********************************************************
     * ******************
     * @param imageCode
     * Bundle extras = data.getExtras(); image = (Bitmap)
     * extras.get("data");
     * @param tempFile
     * 拍照时的临时文件 需要zoom时 *
     */

    public final static int TAKEVIDEO = 519;
    public final static int TAKEPHOTO = 520;
    public final static int PICKPHTOO = 521;
    public final static int MUTIPICK = 522;
    public final static int CROPPHOTO = 110;
    public final static int CHOOSEVIDEO = 600;


    public final static String NO_SDCARD = "请插入sd卡!";

    public static boolean getPhoto(final Activity activity,
                                   final int takePhotoCode, final int pickPhotoCode,
                                   final String fileName, final Fragment f) {
        final CharSequence[] items = {"相册", "拍照"};
        AlertDialog dlg = new AlertDialog.Builder(activity).setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 1) {
                            takePhoto(activity, takePhotoCode, fileName, f);
                        } else {
                            pickPhoto(activity, pickPhotoCode, f);
                        }
                    }
                }).create();
        dlg.show();
        return true;
    }

    /**
     * @param activity
     * @param takePhotoCode
     * @param fileName      注意需要后缀 。png或。jpg
     * @return
     */
    public static boolean takePhoto(Activity activity, int takePhotoCode, String fileName, Fragment f) {

        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        // FileMy baseFile = getPublicStoragePath(activity);
        File baseFile = getDiskCacheDir(activity, "imgCache");
        SLogger.i("-->>", "camera base file = " + baseFile);
        if (baseFile == null)
            return false;
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        File file = new File(baseFile, fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (f == null) {
            SLogger.i("-->>", "camera file = " + file.getAbsolutePath());
            activity.startActivityForResult(getImageByCamera, takePhotoCode);
        } else {
            SLogger.i("-->>", "camera file = " + file.getAbsolutePath());
            f.startActivityForResult(getImageByCamera, takePhotoCode);
        }

        return true;
    }

    public static File getPublicStoragePath(Context context) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            return path;
        } else {
            Toast.makeText(context, "请插入SD卡！", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static boolean pickPhoto(Activity activity, int imageCode, Fragment f) {
        // Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        // getImage.addCategory(Intent.CATEGORY_OPENABLE);
        // getImage.setType("image/jpeg");
        Intent getImage = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (f == null) {
            activity.startActivityForResult(getImage, imageCode);
        } else {
            f.startActivityForResult(getImage, imageCode);
        }

        return true;
    }


    public static File onPhotoZoom(String path, int rw, int rh, int compress,
                                   Context context) {
        File f = new File(path);
        Bitmap btp = PhotoUtils.getLocalImage(f, rw, rh);
        compressImage(btp, f, compress, context);
        return f;
    }

    public static File onPhotoFromCamera(Activity activity, String fileName) {
        File baseFile = getDiskCacheDir(activity, "imgCache");
        if (baseFile == null)
            return null;
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        File file = new File(baseFile, fileName);
        return file;
    }

    /**
     * 选择图片后得到原始大图的文件路径。
     *
     * @param activity
     * @param data
     * @return
     */
    public static File onPhotoFromPick(Activity activity, Intent data) {

        Uri uri = data.getData();
        if (TextUtils.isEmpty(uri.getAuthority())) {
            return null;
        }
        Cursor cursor = activity.getContentResolver()
                .query(uri, new String[]{MediaStore.Images.Media.DATA},
                        null, null, null);
        // 返回 没找到选择图片
        if (null == cursor) {
            return null;
        }
        // 光标移动至开头 获取图片路径
        cursor.moveToFirst();
        String pathImage = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return new File(pathImage);

    }

    // /**
    // * 通过URI 获取真实路劲
    // *
    // * @param activity
    // * @param contentUri
    // * @return
    // */
    // public static String getRealPathFromURI(Activity activity, Uri
    // contentUri) {
    // Cursor cursor = null;
    // String result = contentUri.toString();
    //
    // String[] proj = { MediaStore.MediaColumns.DATA };
    // cursor = activity.managedQuery(contentUri, proj, null, null, null);
    // if (cursor == null)
    // throw new NullPointerException("reader file field");
    // if (cursor != null) {
    // int column_index = cursor
    // .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
    // cursor.moveToFirst();
    // result = cursor.getString(column_index);
    // if (Integer.parseInt(Build.VERSION.SDK) < 14) {
    // cursor.close();
    // }
    // }
    // return result;
    // }

    /**
     * 图片压缩 上传图片时建议compress为30
     *
     * @param bm
     * @param f
     */
    public static void compressImage(Bitmap bm, File f, int compress,
                                     Context context) {
        if (bm == null)
            return;
        File file = f;

        File baseFile = getDiskCacheDir(context, "imgCache");
        if (baseFile == null)
            return;
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, compress, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void compressBitmap(Bitmap bitmap, int width, int height, OnProcessImgFinishListener listener){
        new BitmapTask(bitmap, width, height, listener).execute();
    }

    /**
     * 压缩bitmap（比例）
     */
    private static Bitmap doCompressBitmap(Bitmap image, int width, int height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = height;//这里设置高度为800f
        float ww = width;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }


    /**
     * @param size 压缩的大小下限，单位kb
     * @return
     */
    public static boolean compressImageWithSize(File inputFile, File outPutFile, int size, Context context) {
        boolean isDone = false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap image = getImageAutoResize(inputFile);
//        Bitmap image = BitmapFactory.decodeFile(inputFile.getAbsolutePath());
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//质量压缩方法把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {  //循环判断如果压缩后图片是否大于 size,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            if (options == 10) {
                break;
            }
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        OutputStream outStream = null;
        try {
            if (outPutFile.exists()) {
                outPutFile.delete();
            }
            outPutFile.createNewFile();
            outStream = new FileOutputStream(outPutFile);
            baos.writeTo(outStream);
//            outStream.flush();
//            outStream.close();
            isDone = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            image.recycle();
            image = null;
            System.gc();
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
                if (outStream != null) {
                    outStream.close();
                    outStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return isDone;
    }


    /**
     * 获取文件中图片的压缩bitmap
     * @param file
     * @param width
     * @param height
     * @param listener
     */
    public static void getFileBitmapAsync(File file, int width, int height, OnProcessImgFinishListener listener) {

        new ImgTask(file, width, height, listener).execute();

    }

    /**
     * 获取resource中图片压缩的bitmap
     * @param context
     * @param resId
     * @param width
     * @param height
     * @param listener
     */
    public static void getResourceBitmapAsync(Context context, int resId, int width, int height, OnProcessImgFinishListener listener) {

        new ImgTask(context, resId, width, height, listener).execute();

    }

    /**
     * 由本地文件获取希望大小的文件
     *
     * @param f
     * @return
     */
    public static Bitmap getLocalImage(File f, int swidth, int sheight) {
        File file = f;
        if (file.exists()) {
            try {
                file.setLastModified(System.currentTimeMillis());
                FileInputStream in = new FileInputStream(file);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                int sWidth = swidth;
                int sHeight = sheight;
                int mWidth = options.outWidth;
                int mHeight = options.outHeight;
                int s = 1;
                while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2)) {
                    s *= 2;
                }
                options = new BitmapFactory.Options();
                options.inSampleSize = s;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inPurgeable = true;
                options.inInputShareable = true;
                try {
                    // 4. inNativeAlloc 属性设置为true，可以不把使用的内存算到VM里
                    BitmapFactory.Options.class.getField("inNativeAlloc")
                            .setBoolean(options, true);
                } catch (Exception e) {
                }
                in.close();
                // 再次获取
                in = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
        return null;
    }

    /**
     * 由Drawable文件转化为某个大小的bitmap
     *
     * @param f
     * @return
     */
    public static Bitmap getResourceImage(Context context, int f, int swidth, int sheight) {
        if (f != 0) {
            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(), f, options);
                int sWidth = swidth;
                int sHeight = sheight;
                int mWidth = options.outWidth;
                int mHeight = options.outHeight;
                int s = 1;
                while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2)) {
                    s *= 2;
                }
                options = new BitmapFactory.Options();
                options.inSampleSize = s;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inPurgeable = true;
                options.inInputShareable = true;
                try {
                    // 4. inNativeAlloc 属性设置为true，可以不把使用的内存算到VM里
                    BitmapFactory.Options.class.getField("inNativeAlloc")
                            .setBoolean(options, true);
                } catch (Exception e) {
                }
                // 再次获取
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), f, options);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
        return null;
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
                // cachePath = context.getExternalFilesDir(null).getAbsolutePath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + uniqueName);
        } catch (Exception e) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + uniqueName);
        }


    }


    /**
     * aspectY Y对于X的比例 outputX X 的宽 *
     */
    public static File photoZoom(Fragment fragment, Uri inputUri,
                                 int photoResoultCode, int aspectX, int aspectY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        if (aspectY > 0) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }
        intent.putExtra("scale", aspectX == aspectY);
        File baseFile = getDiskCacheDir(fragment.getActivity(), "imgCache");
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        File temp = new File(baseFile, System.currentTimeMillis() + "crop.jpg");
        if (temp.exists()) {
            temp.delete();
        }
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri outUri = Uri.fromFile(temp);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); //
        fragment.startActivityForResult(intent, photoResoultCode);
        return temp;
    }

    public static File photoZoom(Activity activity, Uri inputUri,
                                 int photoResoultCode, int aspectX, int aspectY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        if (aspectY > 0) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }
        intent.putExtra("scale", aspectX == aspectY);
        File baseFile = getDiskCacheDir(activity, "imgCache");
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        File temp = new File(baseFile, System.currentTimeMillis() + "crop.jpg");
        if (temp.exists()) {
            temp.delete();
        }
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri outUri = Uri.fromFile(temp);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); //
        activity.startActivityForResult(intent, photoResoultCode);
        return temp;
    }

    static class ImgTask extends AsyncTask<Void, Void, Bitmap> {

        private int mWidth;
        private int mHeight;
        private File mFile;
        private int resId;
        private Context context;
        private OnProcessImgFinishListener mListener;

        public ImgTask(File file, int width, int height, OnProcessImgFinishListener listener) {
            this.mWidth = width;
            this.mHeight = height;
            this.mListener = listener;
            mFile = file;
        }

        public ImgTask(Context context, int resId, int width, int height, OnProcessImgFinishListener listener) {
            this.mWidth = width;
            this.mHeight = height;
            this.mListener = listener;
            this.resId = resId;
            this.context = context;
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mFile != null)
                return getLocalImage(mFile, mWidth, mHeight);
            else
                return getResourceImage(context, resId, mWidth, mHeight);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mListener.onProcessImgFinish(bitmap);
        }
    }

    static class BitmapTask extends AsyncTask<Void, Void, Bitmap> {

        private int mWidth;
        private int mHeight;
        private Bitmap mBitmap;
        private int resId;
        private Context context;
        private OnProcessImgFinishListener mListener;

        public BitmapTask(Bitmap bitmap, int width, int height, OnProcessImgFinishListener listener) {
            this.mWidth = width;
            this.mHeight = height;
            this.mListener = listener;
            mBitmap = bitmap;
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mBitmap != null)
                return doCompressBitmap(mBitmap, mWidth, mHeight);
            else
                throw new NullPointerException("you should have a bitmap first!");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mListener.onProcessImgFinish(bitmap);
        }
    }

    public static Bitmap getImageAutoResize(File f) {
        File file = f;
        Bitmap bitmap = null;
        if (file.exists()) {
            try {
                file.setLastModified(System.currentTimeMillis());
                FileInputStream in = new FileInputStream(file);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                float sWidth = 0;
                float sHeight = 0;
                int mWidth = options.outWidth;
                int mHeight = options.outHeight;
                float scale = mHeight / (float) mWidth;
                Log.i("-->>", "height = " + mHeight + " width = " + mWidth);
                Log.i("-->>", "scale = " + scale);
                if (scale >= 1) {
                    sHeight = 1280;
                    sWidth = sHeight / scale;
                } else {
                    sWidth = 720;
                    sHeight = sWidth * scale;
                }
                Log.i("-->>", "need width = " + sWidth + " need height = " + sHeight);
                int s = 1;
                while ((mWidth / s > sWidth * 2) || (mHeight / s > sHeight * 2)) {
                    s *= 2;
                }
                options = new BitmapFactory.Options();
                options.inSampleSize = s;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inPurgeable = true;
                options.inInputShareable = true;
                try {
                    // 4. inNativeAlloc 属性设置为true，可以不把使用的内存算到VM里
                    BitmapFactory.Options.class.getField("inNativeAlloc")
                            .setBoolean(options, true);
                } catch (Exception e) {
                }
                in.close();
                // 再次获取
                in = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(in, null, options);
                in.close();
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
        return null;
    }


    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 处理翻转图片的task
     */
    public static class DegreeImgTask extends AsyncTask<Void, Void, File> {

        private File mFile;
        private onDegreeImgFinishListerner mListener;
        private int mDegree;
        private ProgressDialog dialog;

        public DegreeImgTask(Context context, File file, int degree, onDegreeImgFinishListerner listener) {
            mDegree = degree;
            this.mListener = listener;
            mFile = file;
            dialog = new ProgressDialog(context);
            dialog.setMessage("图片处理中");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog != null) {
                dialog.show();
            }
        }

        @Override
        protected File doInBackground(Void... params) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap oldImg = BitmapFactory.decodeFile(mFile.getAbsolutePath());
            Bitmap newImg = rotaingImageView(mDegree, oldImg);
            newImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            oldImg.recycle();
            oldImg = null;
            // TODO: 15/12/25
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mFile);
                baos.writeTo(fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return mFile;
        }

        @Override
        protected void onPostExecute(File cnm) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (mListener != null) {
                mListener.onDegreeImgFinish(cnm);
            }
        }
    }

    public interface onDegreeImgFinishListerner {
        void onDegreeImgFinish(File file);
    }

    public interface OnProcessImgFinishListener {
        void onProcessImgFinish(Bitmap img);
    }

    /**
     *
     * @param imgPath
     * @return
     */
    public static String imgToBase64(String imgPath) {
        Bitmap bitmap = null;
        if (imgPath !=null && imgPath.length() > 0) {
            bitmap = readBitmap(imgPath);
        }
        if(bitmap == null){
            //bitmap not found!!
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    /**
     *
     * @param originalImage
     * @param flag 0:竖   1:横
     * @return
     */
    public static Bitmap createReflectedImage(Bitmap originalImage, int flag)
    {
        boolean isP = false;

        boolean isH = false;

        if(originalImage == null) {
            return null;
        }
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if(height > width && flag == 1) {
            isP = true;
        } else if(height < width && flag == 0){
            isH = true;
        }

        if(isP) {

            height = width*9/16;
            originalImage = Bitmap.createBitmap(originalImage, 0, 0, width, height);
        } else if (isH) {
            int oriW = width;
            width = height*9/16;
            originalImage = Bitmap.createBitmap(originalImage, (oriW - width)/2, 0, width, height);
        }
        Matrix matrix = new Matrix();
        // 实现图片翻转90度
        matrix.preScale(1, -1);
        // 创建倒影图片（是原始图片的一半大小）
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);
        // 创建总图片（原图片 + 倒影图片）
        Bitmap finalReflection = Bitmap.createBitmap(width, ((height) + (height/2)), Bitmap.Config.RGB_565);
        // 创建画布
        Canvas canvas = new Canvas(finalReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        //把倒影图片画到画布上
        canvas.drawBitmap(reflectionImage, 0, (height + 1), null);
//        canvas.drawBitmap(foreBitmap, 0, height + 1, null);
        Paint shaderPaint = new Paint();
        //创建线性渐变LinearGradient对象
//        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, finalReflection.getHeight() + 1, 0x77ffffff,
//                0x00000000,Shader.TileMode.MIRROR);

        LinearGradient shader = new LinearGradient(0, height, 0, finalReflection.getHeight() + 1, 0x70ffffff,
                0x00ffffff,Shader.TileMode.CLAMP);
        shaderPaint.setShader(shader);
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果。
        canvas.drawRect(0, (height + 1), width, finalReflection.getHeight(), shaderPaint);

        SLogger.d("<<", "---========>>99999");
        return finalReflection;
    }

    /**
     * 创建带有黑色渐变蒙板的图片
     * @param originalImage
     * @return
     */
    public static Bitmap createMaskImage(Bitmap originalImage)
    {
        if(originalImage == null) {
            return null;
        }
        SLogger.d("<<", "-->>>create!");
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Matrix matrix = new Matrix();
        // 实现图片翻转90度
        matrix.preScale(1, -1);
        // 创建倒影图片（是原始图片的一半大小）
//        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, false);
        // 创建总图片（原图片 + 倒影图片）
        Bitmap finalReflection = Bitmap.createBitmap(width, ( height), Bitmap.Config.RGB_565);
        // 创建画布
        Canvas canvas = new Canvas(finalReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        //把倒影图片画到画布上
//        canvas.drawBitmap(reflectionImage, 0, height + 1, null);
        Paint shaderPaint = new Paint();
        //创建线性渐变LinearGradient对象
        LinearGradient shader = new LinearGradient(0, 0, 0, finalReflection.getHeight(), 0xff000000,
                0x00000000, Shader.TileMode.MIRROR);
        shaderPaint.setShader(shader);
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果。
        canvas.drawRect(0, 0, width, finalReflection.getHeight(), shaderPaint);
        return finalReflection;
    }

    public static Bitmap getImageFromNet(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //设置请求方法
            conn.setConnectTimeout(10000); //设置连接服务器超时时间
            conn.setReadTimeout(5000);  //设置读取数据超时时间

            conn.connect(); //开始连接

            int responseCode = conn.getResponseCode(); //得到服务器的响应码
            if (responseCode == 200) {
                //访问成功
                InputStream is = conn.getInputStream(); //获得服务器返回的流数据
                Bitmap bitmap = BitmapFactory.decodeStream(is); //根据流数据 创建一个bitmap对象

                Bitmap b = bitmap;
                return b;

            } else {
                //访问失败
                SLogger.d("<<", "访问失败===responseCode：" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //断开连接
            }
        }
        return null;
    }
}
