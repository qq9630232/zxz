package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tablet.moran.MainActivity;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.LiningEvent;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.event.PlayModeEvent;
import com.tablet.moran.model.Picture;
import com.tablet.moran.model.PlayPictureBack;
import com.tablet.moran.model.PushBack;
import com.tablet.moran.model.TipsBack;
import com.tablet.moran.presenter.implPresenter.LiningActivityImpl;
import com.tablet.moran.presenter.implView.ILiningActivity;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.DensityUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ScreenUtils;
import com.tablet.moran.tools.ThreadPoolManager;
import com.tablet.moran.tools.photos.PhotoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiningActivity extends BaseActivity implements ILiningActivity {

    public static final int DELETE_PIC = 11;
    public static final String FRAME_COLOR = "frame_color";
    public static final String FRAME_SIZE = "frame_size";
    public static final String PICTURE = "picture";


    private static final int SIZE0 = 0;
    private static final int SIZE1 = 42;
    private static final int SIZE2 = 62;
    private static final int SIZE3 = 82;
    private static final int SIZE4 = 102;

    //集成tips
    public static final String POSITION = "position";

    @BindView(R.id.image_lining)
    ImageView imageLining;
    @BindView(R.id.frame_lining)
    ImageView frameLining;

    private int frameColor = 1;
    private int frameSize = 1;

    private String imageUrl;

    private List<Integer> pics = new ArrayList<>();
    private int index = 0;
    private int size;
    private Picture picture;
    private PlayPictureBack paint;

    LiningActivityImpl lImpl;

    private Timer timer;

    private int playTime;

    //-----------------集成tips--------------
    @BindView(R.id.tip_LL1)
    FrameLayout tipLL1;
    @BindView(R.id.tip_LL2)
    FrameLayout tipLL2;
    //    @BindView(R.id.tip_LL3)
//    FrameLayout tipLL3;
    @BindView(R.id.tip_LL5)
    FrameLayout tipLL5;
    @BindView(R.id.tip_LL4)
    FrameLayout tipLL4;
    @BindView(R.id.tips_LL_main)
    LinearLayout tipsLLMain;

//    @BindView(R.id.tip_LL6)
//    FrameLayout tipLL6;
//    @BindView(R.id.tip_LL7)
//    FrameLayout tipLL7;
//    @BindView(R.id.tip_LL8)
//    FrameLayout tipLL8;
//    @BindView(R.id.tip_LL9)
//    FrameLayout tipLL9;

    private TipsBack tipsBack;
    private View tipView;
    private TextView tipTV;
    private FrameLayout tipFL;
    private FrameLayout.LayoutParams fl;

    private float zoomX = 1f;

    private float screenH = 1872f;
    private float screenW = 1080f;

    private int lastIndex;

    private int lining = SIZE1;

    private int playMode = PushBack.PLAY_ORDER;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lining);
        ButterKnife.bind(this);

        getDatafromIntent(getIntent());

        lImpl = new LiningActivityImpl(this, token, this);
        playTime = Integer.valueOf(PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME));
        playMode = PreferencesUtils.getInt(getApplicationContext(), Constant.PLAY_MODE, PushBack.PLAY_ORDER);

        size = pics.size();

        initView();
        setListener();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null)
                timer.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
        int time = Integer.valueOf(PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME));
        playTime = time;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toRight();
                        }
                    });
                }
            }, playTime * 1000, playTime * 1000);
        }

    }

    private void getDatafromIntent(Intent intent) {
        frameSize = PreferencesUtils.getInt(getApplicationContext(), FRAME_SIZE, 1);
        frameColor = PreferencesUtils.getInt(getApplicationContext(), FRAME_COLOR, 1);

        lastIndex = index;
        index = 0;

        screenW = ScreenUtils.getScreenWidth(this);
        screenH = ScreenUtils.getScrrenHeight(this);

        paint = (PlayPictureBack) intent.getSerializableExtra(MainActivity.PAINT);
        //先获取本地暂存的tips
        TipsBack localTips = diskLruCacheHelper.getAsSerializable(Constant.TIPS);
        tipsBack = (TipsBack) intent.getSerializableExtra(POSITION);

        if (tipsBack == null && localTips != null) {
            tipsBack = localTips;
        }

        if (paint == null) {

            if (tipsBack == null) {
                PlayPictureBack pb = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
                if (pb.getPictures().size() > 0) {
                    pics.clear();
                    pics.addAll(pb.getPictures());
                }
                picture = (Picture) intent.getSerializableExtra(PICTURE);
                if (picture != null) {
                    pics.add(0, picture.getPicture_id());
                }
                paint = pb;
                paint.setPictures(pics);
                PreferencesUtils.putString(getApplicationContext(), Constant.CURRENT_LIST, JSONObject.toJSONString(paint));
            } else {

                diskLruCacheHelper.put(Constant.TIPS, tipsBack);

                PlayPictureBack pbTips = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.CURRENT_LIST), PlayPictureBack.class);
                paint = pbTips;
                pics.clear();
                pics.addAll(pbTips.getPictures());

                picture = (Picture) intent.getSerializableExtra(PICTURE);
                if (picture != null) {
                    pics.add(0, picture.getPicture_id());
                }
                paint = pbTips;
                paint.setPictures(pics);
                PreferencesUtils.putString(getApplicationContext(), Constant.CURRENT_LIST, JSONObject.toJSONString(paint));
            }

        } else {
            PreferencesUtils.putString(getApplicationContext(), Constant.CURRENT_LIST, JSONObject.toJSONString(paint));
            pics.clear();
            pics.addAll(paint.getPictures());
        }
    }

    private void initTips() {
        //确定是删除标记，并且view不为null
        if (tipsBack.getFlag() == 2) {
            tipsLLMain.setVisibility(View.GONE);
        }


        try {

            clearChildView();

            tipView = LayoutInflater.from(this).inflate(R.layout.tip_item, null);

            tipTV = (TextView) tipView.findViewById(R.id.tip_content);
            tipFL = (FrameLayout) tipView.findViewById(R.id.tip_FL);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                fl = new FrameLayout.LayoutParams(DensityUtils.dip2px(430), DensityUtils.dip2px(380));
            else
                fl = new FrameLayout.LayoutParams(DensityUtils.dip2px(470), DensityUtils.dip2px(410));
            fl.gravity = Gravity.CENTER;

            SLogger.d("<<", "texture111111111-->>" + tipsBack.getTips_texure());

            switch (tipsBack.getTips_texure()) {
                case 1:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_1));
                    break;
                case 2:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_2));
                    break;
                case 3:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_3));
                    break;
                default:
                    tipFL.setBackground(getResources().getDrawable(R.mipmap.tip_bg_1));
                    break;

            }
            tipTV.setText(tipsBack.getTips_content());

            tipView.setLayoutParams(fl);
            switch (tipsBack.getTips_location()) {
                case 1:
                    tipLL1.addView(tipView);
                    break;
                case 2:
                    tipLL2.addView(tipView);
                    break;

                case 3:
                    tipLL4.addView(tipView);
                    break;
                case 4:
                    tipLL5.addView(tipView);
                    break;
                default:
                    tipLL1.addView(tipView);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clearChildView() {
        if (tipLL1.getChildCount() != 0) {
            tipLL1.removeAllViews();
        } else if (tipLL2.getChildCount() != 0) {
            tipLL2.removeAllViews();
        } else if (tipLL4.getChildCount() != 0) {
            tipLL4.removeAllViews();
        } else if (tipLL5.getChildCount() != 0) {
            tipLL5.removeAllViews();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDatafromIntent(intent);
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferencesUtils.putInt(getApplicationContext(), Constant.LINING_INDEX, index);
    }

    @Override
    protected void initView() {
        super.initView();

        lp = new FrameLayout.LayoutParams((int) screenW, (int) screenH);

        SLogger.d("<<", "初始化view--->>>" + DensityUtils.dip2px(42) + "--->" + screenH + "--->" + screenW);

        if (tipsBack != null) {
            tipsLLMain.setVisibility(View.VISIBLE);
//            index = PreferencesUtils.getInt(getApplicationContext(), Constant.LINING_INDEX, 0);
            index = 0;
            initTips();

        } else {
            tipsLLMain.setVisibility(View.GONE);
        }
        /*//先从本地获取
        picture = diskLruCacheHelper.getAsSerializable(String.valueOf(pics.get(index)));
        //再从网络获取
        if (picture == null) {
            lImpl.getPicture(pics.get(index));
        }*/

        switch (frameColor) {
            case 1:
                frameLining.setImageDrawable(getResources().getDrawable(R.mipmap.frame1));
                break;
            case 2:
                frameLining.setImageDrawable(getResources().getDrawable(R.mipmap.frame2));
                break;
            case 3:
                frameLining.setImageDrawable(getResources().getDrawable(R.mipmap.frame3));
                break;
            case 4:
                frameLining.setImageDrawable(getResources().getDrawable(R.mipmap.frame4));
                break;
            case 5:
                frameLining.setImageDrawable(getResources().getDrawable(R.mipmap.frame5));
                break;
            default:
                frameLining.setImageDrawable(getResources().getDrawable(R.mipmap.frame1));
                break;

        }

        switch (frameSize) {
            case 1:
                lining = SIZE0;
                break;
            case 2:
                lining = SIZE1;
                break;
            case 3:
                lining = SIZE2;
                break;
            case 4:
                lining = SIZE3;
                break;
            case 5:
                lining = SIZE4;
                break;

        }
        //网络获取时 picture为null，子线程获取完后赋值
        /*if (picture != null) {

            zoomX = getZoomValue(picture.getPicture_url(), lining);
            SLogger.d("<<", "--zoomX--->>" + zoomX);
            imageLining.setScaleX(zoomX);
            imageLining.setScaleY(zoomX);
        }*/

        getDiskImage();

    }

    FrameLayout.LayoutParams lp;

    private float getZoomValue(String path, int lining) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final float PP = 16 / 9;

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        //return new int[]{options.outWidth,options.outHeight};
        float width = options.outWidth;
        float height = options.outHeight;

        SLogger.d("<<", "--width---->" + width + "--height-->" + height);
        //竖
        if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {

            //长图
            if ((height / width) >= PP) {

                return (screenH - lining * 2) / screenH;

                //宽图
            } else {


                return (screenW - lining * 2) / screenW;
            }

            //横
        } else {
            //长图
            if ((height / width) >= PP) {


                return (screenH - lining * 2) / screenH;

                //宽图
            } else {

                //TODO 不行改成addview吧，每个view保存下来，进来再重新加载试一下
                return (screenW - lining * 2) / screenW;
            }
        }
    }


    private float getZoomValueByMmp(Bitmap bitmap, int lining) {
        final float PP = 16 / 9;

        /**
         *options.outHeight为原始图片的高
         */
        //return new int[]{options.outWidth,options.outHeight};
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        SLogger.d("<<", "--width---->" + width + "--height-->" + height);
        //竖
        if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {

            //长图
            if ((height / width) >= PP) {
                return (screenH - lining * 2) / screenH;

                //宽图
            } else {
                return (screenW - lining * 2) / screenW;
            }
            //横
        } else {
            //长图
            if ((height / width) >= PP) {
                return (screenH - lining * 2) / screenH;

                //宽图
            } else {
                return (screenW - lining * 2) / screenW;
            }
        }
    }


    @Override
    protected void setListener() {
        super.setListener();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getDatafromIntent(intent);

        lImpl = new LiningActivityImpl(this, token, this);
        playTime = Integer.valueOf(PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME));

        size = pics.size();

        setContentView(R.layout.activity_lining);

        imageLining = (ImageView) findViewById(R.id.image_lining);
        frameLining = (ImageView) findViewById(R.id.frame_lining);
        tipLL1 = (FrameLayout) findViewById(R.id.tip_LL1);
        tipLL2 = (FrameLayout) findViewById(R.id.tip_LL2);
        tipLL4 = (FrameLayout) findViewById(R.id.tip_LL4);
        tipLL5 = (FrameLayout) findViewById(R.id.tip_LL5);

        tipsLLMain = (LinearLayout) findViewById(R.id.tips_LL_main);

        initView();

        setListener();

    }

    @Override
    protected void toLeft() {
        super.toLeft();

        dialog.show();
//        dialog.getMsgView().setText("加载中...");
//        ThreadPoolManager.getinstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(300);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
        switch (playMode) {
            case PushBack.PLAY_ORDER:

                --index;
                getDiskImage();
                break;
            case PushBack.PLAY_RAND:
                index = (int) (Math.random() * (pics.size()));

                getDiskImage();
                break;
            case PushBack.PLAY_SINGLE:
                break;
        }

//                    }
//                });
//            }
//        });

    }

    @Override
    protected void toUp() {
        super.toUp();

        picture = (Picture) diskLruCacheHelper.getAsSerializable(String.valueOf(pics.get(index)));

        if (picture == null)
            return;

        Intent intent = new Intent(this, PaintActivity.class);
        intent.putExtra(PICTURE, picture);
        startActivityForResult(intent, DELETE_PIC);
    }

    @Override
    protected void toRight() {
        super.toRight();

        dialog.show();
//        dialog.getMsgView().setText("加载中...");
//        ThreadPoolManager.getinstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(300);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();

        switch (playMode) {
            case PushBack.PLAY_ORDER:
                ++index;
                getDiskImage();
                break;
            case PushBack.PLAY_RAND:
                index = (int) (Math.random() * (pics.size()));
                getDiskImage();
                break;
            case PushBack.PLAY_SINGLE:
                break;
        }
//
//
//                    }
//                });
//            }
//        });

    }

    @Override
    protected void toBlRight() {
        super.toBlRight();

        toRight();
    }

    @Override
    protected void toBlUp() {
        super.toBlUp();
        toUp();
    }

    @Override
    protected void toBlDown() {
        super.toBlDown();
        toDown();
    }

    @Override
    protected void toBlLeft() {
        super.toBlLeft();

        toLeft();
    }

    private void resetPicsOrder() {
        switch (playMode) {
            case PushBack.PLAY_ORDER:
                break;
            case PushBack.PLAY_RAND:
                index = (int) (Math.random() * (pics.size()));
                break;
            case PushBack.PLAY_SINGLE:
                break;
        }
    }

    private void getDiskImage() {

        playTime = Integer.valueOf(PreferencesUtils.getString(getApplicationContext(), Constant.PLAY_TIME));

        index = index > (pics.size() - 1) ? 0 : index;

        index = index < 0 ? (pics.size() - 1) : index;
        picture = (Picture) diskLruCacheHelper.getAsSerializable(String.valueOf(pics.get(index)));

        SLogger.d("<<", "--index---77777>>" + index);


        if (picture != null) {
            //来自网络保存的
            if (picture.getPicture_url().contains("http")) {
                getSuccess(picture);
                //本地存储的
            } else {
                zoomX = getZoomValue(picture.getPicture_url(), lining);

                imageLining.setScaleX(zoomX);
                imageLining.setScaleY(zoomX);

                imageLining.setImageBitmap(BitmapFactory.decodeFile(picture.getPicture_url()));
                PreferencesUtils.putString(getApplicationContext(), Constant.PIC_URL, picture.getPicture_url());

                dialog.dismiss();

                if (picture.getVideoFile() != null) {
                    if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {
                        Intent videoIntent = new Intent(LiningActivity.this, VideoActivity.class);
                        videoIntent.putExtra(Constant.TITLE, picture.getTitle());
                        videoIntent.putExtra(Constant.VIDEO, picture.getVideoFile());
                        startActivity(videoIntent);
                    } else {
                        Intent videoIntent = new Intent(LiningActivity.this, VideoLandActivity.class);
                        videoIntent.putExtra(Constant.TITLE, picture.getTitle());
                        videoIntent.putExtra(Constant.VIDEO, picture.getVideoFile());
                        startActivity(videoIntent);
                    }

                }
            }

        } else {
            lImpl.getPicture(pics.get(index));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void toDown() {
        super.toDown();

        Intent intentToMain = new Intent(this, MainActivity.class);
//        if (paint.getPaint_id() != 0)
        intentToMain.putExtra(MainActivity.PAINT, paint);
//        intentToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentToMain);
    }

    /**
     * 删除画单
     */
    private void delete() {

        //TODO 删除全部画作？？？？？
        if (size == 1) {
            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.cannot_del_all));
        } else {
            int temp = index;

            ++index;
            index = index == size ? 0 : index;

            getDiskImage();

            pics.remove(temp);
            size = pics.size();

        }

    }


    @Override
    protected void toBlBack() {

    }

    /**
     * 蓝牙向上
     */
    public void blUp() {
        if (isDialogShow) {
            if (yesFlag != null) {
                if (yesFlag.getVisibility() == View.VISIBLE) {
                    noFlag.setVisibility(View.VISIBLE);
                    yesFlag.setVisibility(View.GONE);
                    curResId = R.id.no_btn;
                } else {
                    noFlag.setVisibility(View.GONE);
                    yesFlag.setVisibility(View.VISIBLE);
                    curResId = R.id.yes_btn;
                }
            }
        }
    }

    /**
     * 蓝牙向下
     */
    public void blDown() {
        if (isDialogShow) {
            if (yesFlag != null) {
                if (yesFlag.getVisibility() == View.VISIBLE) {
                    noFlag.setVisibility(View.VISIBLE);
                    yesFlag.setVisibility(View.GONE);
                    curResId = R.id.no_btn;
                } else {
                    noFlag.setVisibility(View.GONE);
                    yesFlag.setVisibility(View.VISIBLE);
                    curResId = R.id.yes_btn;
                }
            }
        }
    }

    /**
     * 蓝牙 确认
     */
    public void blOk() {

        switch (curResId) {
            case R.id.no_btn:
                ad.dismiss();
                isDialogShow = false;
                break;

            case R.id.yes_btn:
                ad.dismiss();
                isDialogShow = false;
                delete();
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DELETE_PIC:
                    delete();
                    break;
            }
        }
    }

    @Override
    public void showProgressDialog() {
        dialog.show();
        dialog.getMsgView().setText(getResources().getString(R.string.waiting));
    }

    @Override
    public void hidProgressDialog() {
        dialog.hide();
    }

    @Override
    public void showError(String error) {
        dialog.hide();
//        AppUtils.showToast(getApplicationContext(), error);
    }

    @Override
    public void getSuccess(final Picture pic) {

        //来源于网络
        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {

                //获取网络 的 bitmap
                Bitmap mmp = PhotoUtils.getImageFromNet(pic.getPicture_url());

                //保存bitmap到文件夹
                String picSavePath = saveBitmapFile(mmp, PhotoUtils.getDiskCacheDir(getApplicationContext(), pic.getPicture_id() + ".jpg")).getAbsolutePath();

                //保存至当前的pic
                pic.setPicture_url(picSavePath);

                picture = pic;

                //缓存新加入的pic
                if (diskLruCacheHelper.get(String.valueOf(pic.getPicture_id())) == null)
                    diskLruCacheHelper.put(String.valueOf(pic.getPicture_id()), pic);


                zoomX = getZoomValue(picSavePath, lining);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SLogger.d("<<", "--zoomX--->>" + zoomX);

                        imageLining.setScaleX(zoomX);
                        imageLining.setScaleY(zoomX);

                        imageLining.setImageBitmap(BitmapFactory.decodeFile(picture.getPicture_url()));

                        hidProgressDialog();

//                        diskLruCacheHelper.put(picture.getPicture_id() + "", picture);
//                        diskLruCacheHelper.put(picture.getPicture_url(), mmp);
                    }
                });

            }
        });

    }

    /**
     * 暂时用来测试
     *
     * @param event
     */
    public void onEventMainThread(LiningEvent event) {

        frameColor = PreferencesUtils.getInt(getApplicationContext(), FRAME_COLOR);
        frameSize = PreferencesUtils.getInt(getApplicationContext(), FRAME_SIZE);

        initView();

    }

    public void onEventMainThread(PlayModeEvent event) {

        playMode = PreferencesUtils.getInt(this, Constant.PLAY_MODE);
    }

    /**
     * bitmap----》file
     *
     * @param bitmap
     */
    private File saveBitmapFile(Bitmap bitmap, File file) {

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, bos);
            bos.flush();
            bos.close();
            bitmap.recycle();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}
