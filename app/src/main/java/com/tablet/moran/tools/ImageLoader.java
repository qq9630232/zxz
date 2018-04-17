package com.tablet.moran.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.tablet.moran.R;
import com.tablet.moran.clazz.StackBlurManager;
import com.tablet.moran.view.GlideRoundTransform;

import java.io.File;

/**
 * glide 图片加载器
 * 注意context 传actvity 或 fragment 方便生命周期的联动
 * Created by tristan on 15/10/23.
 */

public class ImageLoader {
    private static ImageLoader instance;
    private final static String IMAGE_CACHE_NAME = "YiYi_img";
    //内部缓存默认大小 50M
    private final static int DEFAULT_INTERNAL_CACHE_SIZE = 50 * 1024 * 1024;
    //外部缓存默认大小 200M
    private final static int DEFAULT_EXTERNAL_CACHE_SIZE = 200 * 1024 * 1024;

    public static void init(Context context) {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null)
                    instance = new ImageLoader(context);
            }
        }
    }

    public static ImageLoader getInstance() {
        if (instance == null)
            throw new NullPointerException("You should init ImageLoader firstly");
        return instance;
    }

    private ImageLoader(Context context) {
        GlideBuilder builder = new GlideBuilder(context);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //有sd卡，使用sd卡缓存
            builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, IMAGE_CACHE_NAME, DEFAULT_EXTERNAL_CACHE_SIZE));
        } else {
            //无sd卡，使用内部存储
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, IMAGE_CACHE_NAME, DEFAULT_INTERNAL_CACHE_SIZE));
        }
        Glide.setup(builder);
    }

    public void setBlurImage(final Activity activity, final String url, final ImageView targetView, ImageView goneView) {

        final Bitmap bitmap = AppUtils.getDiskLruHelper().getAsBitmap(url);
        if (bitmap == null) {
            Glide.with(activity).load(url).asBitmap()
                    .into(new BitmapImageViewTarget(goneView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);

                            applyBlur(activity, 15, resource, url, targetView);
                        }
                    });
        } else
            targetView.setImageBitmap(bitmap);
    }

    /**
     * 调用.so中的函数实现高斯模糊
     *
     * @param radius 模糊参数 代表 模糊程度
     */
    private void applyBlur(final Activity activity, final int radius, final Bitmap bitmap, final String url, final ImageView targetView) {

        final StackBlurManager stackBlurManager = new StackBlurManager(bitmap);


        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {

                try {

                    final Bitmap mBitmap = stackBlurManager.process(radius);
                    //发送模糊完成通知
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            targetView.setImageBitmap(mBitmap);
                            AppUtils.getDiskLruHelper().put(url, mBitmap);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();//OOM
                }

            }
        });
    }

    /**
     * 加载网络url图片
     *
     * @param act
     * @param url
     * @param imageView
     */
    public static void displayImg(Activity act, String url, ImageView imageView) {
        //DiskCacheStrategy.ALL 硬盘缓存策略为缓存原图，用于显示不同的大小的ImageView上会更快
        Glide.with(act).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.transparent_bg_black).into(imageView);
    }

    /**
     * 加载res
     *
     * @param act
     * @param resId
     * @param imageView
     */
    public static void displayResImg(Context act, int resId, ImageView imageView) {
        //DiskCacheStrategy.ALL 硬盘缓存策略为缓存原图，用于显示不同的大小的ImageView上会更快
        Glide.with(act).load(resId).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.transparent_bg_black).into(imageView);
    }
    /**
     * 加载头像图片
     *
     * @param act
     * @param url
     * @param imageView
     */
    public static void displayHeadImg(Activity act, String url, ImageView imageView) {
        //DiskCacheStrategy.ALL 硬盘缓存策略为缓存原图，用于显示不同的大小的ImageView上会更快
        Glide.with(act).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.transparent_bg_black).into(imageView);
    }

    /**
     * 地图特有加载网络url图片
     *
     * @param act
     * @param url
     * @param imageView
     */
    public static void displayMapImg(Activity act, String url, ImageView imageView) {
        //DiskCacheStrategy.ALL 硬盘缓存策略为缓存原图，用于显示不同的大小的ImageView上会更快
        Glide.with(act).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.transparent_bg_black).placeholder(R.mipmap.transparent_bg_black).into(imageView);
    }

    public static void displayImg(Context context, String url, ImageView imageView) {
        //DiskCacheStrategy.ALL 硬盘缓存策略为缓存原图，用于显示不同的大小的ImageView上会更快
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.transparent_bg_black).into(imageView);
    }

    //缺省图为特定图片
    public static void displayAddImg(Context context, String url, ImageView imageView) {
        //DiskCacheStrategy.ALL 硬盘缓存策略为缓存原图，用于显示不同的大小的ImageView上会更快
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.transparent_bg_black).into(imageView);
    }

    /**
     * 加载本地文件图片
     *
     * @param file
     * @param imageView
     */
    public static void displayImg(Context act, File file, ImageView imageView) {
        Glide.with(act).load(file).error(R.mipmap.transparent_bg_black).placeholder(R.mipmap.transparent_bg_black).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    /**
     * 加载本地文件圆形图片
     *
     * @param file
     * @param imageView
     */
    public static void displayCircleImg(Activity act, File file, final ImageView imageView) {
        /*Glide.with(act).load(file).error(R.mipmap.transparent_bg_black).placeholder(R.mipmap.default_house_bg).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);*/
        Glide.with(act)
                .load(file)
                .asBitmap()
                .error(R.mipmap.transparent_bg_black)
                .placeholder(R.mipmap.transparent_bg_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    /**
     * 加载资源图片转换为圆角
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void displayRoundImg(Context context, String url, final ImageView imageView) {
        /*Glide.with(act).load(file).error(R.mipmap.transparent_bg_black).placeholder(R.mipmap.default_house_bg).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);*/
        Glide.with(context)
                .load(url).error(R.mipmap.transparent_bg_black).placeholder(R.mipmap.transparent_bg_black).into(imageView);
    }

    /**
     * 加载Uri图片
     *
     * @param uri
     * @param imageView
     */
    public static void displayImg(Activity act, Uri uri, ImageView imageView) {
        Glide.with(act).load(uri).error(R.mipmap.transparent_bg_black).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    /**
     * 加载网络url图片
     *
     * @param url
     */
    public static void displayImg(Fragment fragment, String url, ImageView imageView) {
        Glide.with(fragment).load(url).error(R.mipmap.transparent_bg_black).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    /**
     * 加载本地文件图片
     *
     * @param file
     * @param imageView
     */
    public static void displayImg(Fragment fragment, File file, ImageView imageView) {
        Glide.with(fragment).load(file).placeholder(R.mipmap.transparent_bg_black).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    /**
     * context 建议为act
     * 加载Uri图片
     *
     * @param uri
     * @param imageView
     */
    public static void displayImg(Fragment fragment, Uri uri, ImageView imageView) {
        Glide.with(fragment).load(uri).placeholder(R.mipmap.transparent_bg_black).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    public static void displayCircleImg(Context context, String url, final ImageView img) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .error(R.mipmap.transparent_bg_black)
                .placeholder(R.mipmap.transparent_bg_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(img) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        img.setImageBitmap(resource);
                    }
                });
    }

    public static void displayImgWithScaleType(Context context, String url, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .dontAnimate()
                .error(R.mipmap.transparent_bg_black)
                .placeholder(R.mipmap.transparent_bg_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        if (resource.getWidth() > resource.getHeight()) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    public static void displayRoundImgWithScaleType(Context context, String url, final ImageView imageView, int dp) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .dontAnimate()
                .transform(new GlideRoundTransform(context, dp))
                .error(R.mipmap.transparent_bg_black)
                .placeholder(R.mipmap.transparent_bg_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
//                        if (resource.getWidth() > resource.getHeight()) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        } else {
//                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                        }
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    public static void displayImgWithScaleTypeFile(Context context, File file, final ImageView imageView) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .error(R.mipmap.transparent_bg_black)
                .placeholder(R.mipmap.transparent_bg_black)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        if (resource.getWidth() > resource.getHeight()) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    /**
     * 清除硬盘缓存
     *
     * @param context
     */
    public static void clearDiskCache(final Context context) {

        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
    }


    public static void displayUpMainImg(Context context, File file, final ImageView imageView) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .placeholder(R.mipmap.transparent_bg_black)
                .override(ScreenUtils.getScreenWidth(context), ScreenUtils.getScrrenHeight(context) / 2)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        if (resource.getWidth() > resource.getHeight()) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    public static void displayUpMainImg(Context context, String url, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.mipmap.transparent_bg_black)
                .override(ScreenUtils.getScreenWidth(context), ScreenUtils.getScrrenHeight(context) / 2)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        if (resource.getWidth() > resource.getHeight()) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    public static void displayImgWithCenterCrop(Context context, String url, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.mipmap.transparent_bg_black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageBitmap(resource);
                    }
                });
    }


    public static void displayImgWithCenterCropNODefault(Context context, String url, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageBitmap(resource);
                    }
                });
    }
}
