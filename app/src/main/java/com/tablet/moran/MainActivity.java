package com.tablet.moran;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tablet.moran.activity.BaseActivity;
import com.tablet.moran.activity.LiningActivity;
import com.tablet.moran.activity.ResetAppActivity;
import com.tablet.moran.adapter.MainRecyclerAdaper;
import com.tablet.moran.clazz.StackBlurManager;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.LightEvent;
import com.tablet.moran.event.NewPush;
import com.tablet.moran.event.OrientEvent;
import com.tablet.moran.model.LocalPlayList;
import com.tablet.moran.model.PlayPictureBack;
import com.tablet.moran.model.TitleInfo;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.MD5Util;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.ThreadPoolManager;
import com.tablet.moran.tools.photos.PhotoUtils;
import com.tablet.moran.view.gallery.CardScaleHelper;
import com.tablet.moran.view.gallery.SpeedRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSION_CODE_LOCATION = 111;
    private static final float MIN_SCALE = .8f;
    private static final float MAX_SCALE = 1.2f;


    public static final int RIGHT = 0;
    public static final int LEFT = 1;

    public static final String PAINT = "pictures";
    public static final String PAINT_ID = "pictures";

    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.root_coordinator_layout)
    CoordinatorLayout rootCoordinatorLayout;
    @BindView(R.id.recycler_main)
    SpeedRecyclerView recyclerMain;
    @BindView(R.id.bg_blur)
    ImageView bgBlur;

    MainRecyclerAdaper mainRecyclerAdaperP;
    MainRecyclerAdaper mainRecyclerAdaperL;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.back_delete)
    ImageView backDelete;
    @BindView(R.id.paint_back)
    ImageView paintBack;

    private CardScaleHelper mCardScaleHelper = null;
    volatile List<TitleInfo> paintingList = new ArrayList<>();
    private List<PlayPictureBack> intentPicList = new ArrayList<>();

    private long exitTime;

    private int cur_index;
    private int size;

    private int[] resArray = {R.id.delete, R.id.back_btn};
    private View[] views;

    private boolean toResponse = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        getDataFromIntent(getIntent());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.LAND) {
                getDataFromIntent(getIntent());
            }
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT) == OrientEvent.POR) {
                getDataFromIntent(getIntent());
            }
        }

    }

    private void getDataFromIntent(Intent intent) {
        PlayPictureBack pb = (PlayPictureBack) intent.getSerializableExtra(PAINT);

        //显示loading 加载完毕后跳转到新来的画单列表
        dialog.show();
        dialog.getMsgView().setText(getResources().getString(R.string.downloading));

        restoreLocal();

        LocalPlayList lp = (LocalPlayList) diskLruCacheHelper.getAsSerializable(Constant.LOCAL_ALL_LIST);


        if (intentPicList.size() != 0 && mCardScaleHelper != null) {

            SLogger.d("<<", "111当前画单数---->" + intentPicList.size());

            //验证存储的是否已经存在
            boolean has = false;
            for (PlayPictureBack item : lp.getList()) {
                if (item.getPaint_id() == pb.getPaint_id()) {
                    has = true;
                    break;
                }
            }
            if (!has)
                lp.getList().add(0, pb);
            diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);

            //验证当前的list是否已经存在
            has = false;
            for (PlayPictureBack item1 : intentPicList) {
                if (item1.getPaint_id() == pb.getPaint_id()) {
                    has = true;
                    break;
                }
            }
            if (!has)
                intentPicList.add(0, pb);

            if (getOrientation() == Constant.LANDSCAP)
                recyclerMain.setAdapter(mainRecyclerAdaperL);
            else
                recyclerMain.setAdapter(mainRecyclerAdaperP);

//            //显示loading 加载完毕后跳转到新来的画单列表
//            dialog.show();
//            dialog.getMsgView().setText("正在下载画单...");
            ThreadPoolManager.getinstance().execute(new ProcessImage());
            mCardScaleHelper.setCurrentItemPos(cur_index, RIGHT);
//            AppUtils.showToast(getApplicationContext(), "下载完成");


        } else {
            SLogger.d("<<", "2222当前画单数---->" + intentPicList.size());
            //第一次创建本地lrucache
            if (lp == null) {
                PlayPictureBack localPb = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
//                PlayPictureBack newPb = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.NEW_LIST), PlayPictureBack.class);
//                PlayPictureBack testPb = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.PAINT_TEST), PlayPictureBack.class);

                intentPicList.add(localPb);
//                intentPicList.add(newPb);
//                intentPicList.add(testPb);

            } else {
                intentPicList.addAll(lp.getList());
            }
            //判断新的画单在本地是否已经存
            boolean has = false;
            for (PlayPictureBack item : intentPicList) {
                if (item.getPaint_id() == pb.getPaint_id()) {
                    has = true;
                    break;
                }
            }
            if (!has)
                intentPicList.add(0, pb);

            LocalPlayList lp1 = new LocalPlayList();
            lp1.getList().addAll(intentPicList);

            diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp1);

            ThreadPoolManager.getinstance().execute(new ProcessImage());

            views = new View[]{delete, backBtn};

            initView();

            setListener();

//            AppUtils.showToast(getApplicationContext(), "下载完成");

        }

    }


    private void restoreLocal() {

        dialog.show();
        dialog.getMsgView().setText(getResources().getString(R.string.waiting));
        LocalPlayList lp = (LocalPlayList) diskLruCacheHelper.getAsSerializable(Constant.LOCAL_ALL_LIST);

        if (lp != null) {

            for (int i = 0; i < lp.getList().size(); i++) {
                PlayPictureBack pb = lp.getList().get(i);
                //删掉本地画单
                if (pb.getPaint_id() == -1) {
                    lp.getList().remove(i);
                    break;
                }
            }

            PlayPictureBack p1 = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
            lp.getList().add(0, p1);
            diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //如果是true,刷新，链接wifi后接口eventbus事件
        if (changed) {
            getDataFromIntent(intent);

            if (mainRecyclerAdaperL == null && mainRecyclerAdaperP == null) {
                initView();

                setListener();

            }
        }
    }

    //如果没接受网络连接的event就一直是false
    boolean changed = false;

//    @Override
//    protected void initDataSource() {
//        super.initDataSource();
//        changed = true;
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromIntent(intent);

//            Intent intent1 = new Intent(this, LiningActivity.class);
//            intent1.putExtra(PAINT, pb);
//            startActivity(intent1);

    }

    @Override
    protected void initView() {
        super.initView();

        try {

            dialog.show();
            dialog.getMsgView().setText(getResources().getString(R.string.waiting));

            backBack.setVisibility(View.VISIBLE);
            backDelete.setVisibility(View.GONE);
            curResId = R.id.back_btn;


            size = intentPicList.size();

            if (getOrientation() == Constant.LANDSCAP)
                mainRecyclerAdaperL = new MainRecyclerAdaper(this, paintingList);
            else
                mainRecyclerAdaperP = new MainRecyclerAdaper(this, paintingList);
            recyclerMain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerMain.setItemAnimator(new DefaultItemAnimator());
            recyclerMain.setAdapter(getOrientation() == Constant.LANDSCAP ? mainRecyclerAdaperL : mainRecyclerAdaperP);

            recyclerMain.setOnFlingListener(null);

            mCardScaleHelper = new CardScaleHelper();
            mCardScaleHelper.setCurrentItemPos(cur_index, RIGHT);
            mCardScaleHelper.attachToRecyclerView(recyclerMain);


            mCardScaleHelper.setStillCallBack(new CardScaleHelper.StillCallBack() {
                @Override
                public void still() {

                    try {
                        int pos = ((LinearLayoutManager) recyclerMain.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        if (pos != -1) {
                            final String url = paintingList.get(pos).getGauss_img_url();
                            mBitmap = diskLruCacheHelper.getAsBitmap(url);
                            if (mBitmap != null) {
                                bgBlur.setBackground(new BitmapDrawable(getResources(), mBitmap));
                            } else {
                                ThreadPoolManager.getinstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBitmap = PhotoUtils.createMaskImage(PhotoUtils.getImageFromNet(url));
                                        diskLruCacheHelper.put(url, mBitmap);
//                final StackBlurManager stackBlurManager = new StackBlurManager(bitmap);
//                mBitmap = stackBlurManager.process(20);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                bgBlur.setBackground(new BitmapDrawable(getResources(), mBitmap));
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            final String url = intentPicList.get(0).getTitle_info().getGauss_img_url();
            mBitmap = diskLruCacheHelper.getAsBitmap(url);
            if (mBitmap != null) {
                bgBlur.setBackground(new BitmapDrawable(getResources(), mBitmap));
            } else {
                ThreadPoolManager.getinstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        mBitmap = PhotoUtils.createMaskImage(PhotoUtils.getImageFromNet(url));
                        SLogger.d("<<", "当前模糊的url---->1111111" + url);
                        if (mBitmap == null)
                            return;
                        diskLruCacheHelper.put(url, mBitmap);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                SLogger.d("<<", "当前模糊的url---->22222" + url);
                                bgBlur.setBackground(new BitmapDrawable(getResources(), mBitmap));
                            }
                        });
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    /**
     * 处理图片
     */
    class ProcessImage implements Runnable {

        @Override
        public void run() {
            paintingList.clear();
            for (int i = 0; i < intentPicList.size(); i++) {
                TitleInfo p = intentPicList.get(i).getTitle_info();
                p.setPaint_id(intentPicList.get(i).getPaint_id());
                p.saveThumbPath(getApplicationContext(), PhotoUtils.getDiskCacheDir(getApplicationContext(), "thumb" + MD5Util.MD5Encode(p.getDetail_url() + PreferencesUtils.getInt(getApplicationContext(), Constant.ORIENT), "utf-8") + ".png"));
//                p.saveThumbPath(getApplicationContext(), PhotoUtils.getDiskCacheDir(getApplicationContext(), "thumb" + ".png"));
                SLogger.d("<<", "pppppppppp--->" + p.getDetail_url());
                paintingList.add(p);
            }

            if (!isFinishing())
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getOrientation() == Constant.LANDSCAP)
                            mainRecyclerAdaperL.notifyDataSetChanged();
                        else
                            mainRecyclerAdaperP.notifyDataSetChanged();
                        dialog.dismiss();
                        toResponse = true;
                    }
                });
        }
    }

    private Bitmap mBitmap;

    /**
     * 调用.so中的函数实现高斯模糊
     *
     * @param radius 模糊参数 代表 模糊程度
     */
    private void applyBlur(final int radius, final Bitmap bitmap) {
        final StackBlurManager stackBlurManager = new StackBlurManager(bitmap);


        ThreadPoolManager.getinstance().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    mBitmap = stackBlurManager.process(radius);
                    //发送模糊完成通知
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentContent.setBackground(new BitmapDrawable(getResources(), mBitmap));
//                            AppUtils.getDiskLruHelper().put(user.getHead_image_url(), mBitmap);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();//OOM
                }

            }
        });
    }


 /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.exit_fenghuo));
                exitTime = System.currentTimeMillis();
            } else {
                MyActivityManager.getInstance().finishAllActivity();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void onClick(View v) {

        if (!toResponse)
            return;

        switch (v.getId()) {

            case R.id.back_btn:
                finish();
                break;
            case R.id.no_btn:
                ad.dismiss();
                isDialogShow = false;
                break;

            case R.id.yes_btn:
                ad.dismiss();
                isDialogShow = false;
                delete();
                break;

            case R.id.delete:
                blDelete();
                break;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
//        EventBus.getDefault().unregister(this);
    }

    /**
     * 暂时用来测试
     *
     * @param event
     */
    public void onEventMainThread(NewPush event) {

    }


    /**
     * 删除画单
     */
    private void delete() {

        //TODO 删除全部画作？？？？？
        if (intentPicList.size() == 1) {
            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.cannot_del_all));
        } else {

            intentPicList.remove(cur_index);
            if (getOrientation() == Constant.LANDSCAP)
                mainRecyclerAdaperL.notifyDataSetChanged();
            else
                mainRecyclerAdaperP.notifyDataSetChanged();
            size = intentPicList.size();
        }

    }

    public static final int DELETE_PAINT = 200;

    /**
     * 遥控器蓝牙的回调
     */
    public void blDelete() {


//        showMyDialog("是否删除该画作");
//        yesFlag.setOnClickListener(this);
//        noFlag.setOnClickListener(this);
//        isDialogShow = true;
        PlayPictureBack p = intentPicList.get(cur_index);
        if (p.isNews() || p.getPaint_id() == -1) {
            AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.cannot_del_default));
            return;
        }
        Intent intent = new Intent(this, ResetAppActivity.class);
        intent.putExtra(ResetAppActivity.FLAG, ResetAppActivity.DELETE);
        startActivityForResult(intent, DELETE_PAINT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DELETE_PAINT) {
                //重新保存本地当前画单
                intentPicList.remove(cur_index);
                LocalPlayList lp = new LocalPlayList();
                lp.getList().addAll(intentPicList);
                diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);

                if (getOrientation() == Constant.LANDSCAP)
                    recyclerMain.setAdapter(mainRecyclerAdaperL);
                else
                    recyclerMain.setAdapter(mainRecyclerAdaperP);

                //显示loading 加载完毕后跳转到新来的画单列表
                dialog.show();
                dialog.getMsgView().setText(getResources().getString(R.string.downloading));
                ThreadPoolManager.getinstance().execute(new ProcessImage());
                mCardScaleHelper.setCurrentItemPos(cur_index, RIGHT);
//            AppUtils.showToast(getApplicationContext(), "下载完成");
                dialog.dismiss();
            }
        }
    }

    /**
     * 蓝牙向上
     */
    protected void toBlUp() {
        if (toResponse) {
            paintBack.setVisibility(View.GONE);
            toMenu = true;
            if (curResId == R.id.delete) {
                backDelete.setVisibility(View.VISIBLE);
            } else {
                backBack.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 蓝牙向下
     */
    protected void toBlDown() {
        if (!toResponse)
            return;
        if (toMenu) {
            View v = recyclerMain.getChildAt(cur_index);

            ScaleAnimation zoomAnim1 = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            zoomAnim1.setDuration(150);
            zoomAnim1.setFillBefore(true);
            zoomAnim1.setRepeatCount(0);

            paintBack.setVisibility(View.VISIBLE);
            if (curResId == R.id.delete) {
                backDelete.setVisibility(View.GONE);
            } else {
                backBack.setVisibility(View.GONE);
            }

//            if (v != null)
//                v.startAnimation(zoomAnim1);

            toMenu = false;
        }
        /*else {
            blDelete();
        }*/

    }

    @Override
    protected void toBlOk() {
        super.toBlOk();
        if (!toResponse)
            return;
        if (toMenu) {
            switch (curResId) {
//            case R.id.no_btn:
//                ad.dismiss();
//                isDialogShow = false;
//                break;
//
//            case R.id.yes_btn:
//                ad.dismiss();
//                isDialogShow = false;
//                delete();
//                break;

                case R.id.delete:
                    blDelete();
                    break;
            }
        } else {
            toUp();
        }

    }


    @Override
    protected void toRight() {
        super.toRight();

        if (!toResponse)
            return;
        ++cur_index;

        if (cur_index == intentPicList.size()) {
            cur_index = intentPicList.size() - 1;
        } else {
            SLogger.d("<<", "---->next" + cur_index);
            mCardScaleHelper.setCurrentItemPos(cur_index, RIGHT);
        }

    }


    @Override
    protected void toLeft() {
        super.toLeft();

        if (!toResponse)
            return;
        --cur_index;

        if (cur_index == -1) {

            cur_index = 0;
        } else {
            SLogger.d("<<", "---->pre" + cur_index);
            mCardScaleHelper.setCurrentItemPos(cur_index, LEFT);
        }

    }

    @Override
    protected void toUp() {
        super.toUp();

        if (!toResponse)
            return;

        Intent intent = new Intent(this, LiningActivity.class);
        intent.putExtra(PAINT, intentPicList.get(cur_index));
        startActivity(intent);

    }

    private boolean toMenu = true;

/*    @Override
    protected void toDown() {
        super.toDown();

        if (!toResponse)
            return;

        blDelete();

    }*/

    @Override
    protected void toBlRight() {
        super.toBlRight();

        if (!toResponse)
            return;

        if (!toMenu) {
            toRight();
            return;
        }

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
        } else {
            if (backBack.getVisibility() == View.VISIBLE) {
                curResId = R.id.delete;
                backBack.setVisibility(View.GONE);
                backDelete.setVisibility(View.VISIBLE);
            } else {
                curResId = R.id.back_btn;
                backBack.setVisibility(View.VISIBLE);
                backDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void toBlLeft() {
        super.toLeft();

        if (!toResponse)
            return;

        if (!toMenu) {
            toLeft();
            return;
        }
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
        } else {
            if (backBack.getVisibility() == View.VISIBLE) {
                curResId = R.id.delete;
                backBack.setVisibility(View.GONE);
                backDelete.setVisibility(View.VISIBLE);
            } else {
                curResId = R.id.back_btn;
                backBack.setVisibility(View.VISIBLE);
                backDelete.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_main);
//            ButterKnife.bind(this);
        recyclerMain = (SpeedRecyclerView) findViewById(R.id.recycler_main);
        backBack = (ImageView) findViewById(R.id.back_back);
        backBtn = (TextView) findViewById(R.id.back_btn);
        delete = (ImageView) findViewById(R.id.delete);
        backDelete = (ImageView) findViewById(R.id.back_delete);
        bgBlur = (ImageView) findViewById(R.id.bg_blur);
        paintBack = (ImageView) findViewById(R.id.paint_back);

//            if (mainRecyclerAdaperL == null) {
//                mainRecyclerAdaperL = new MainRecyclerAdaper(this, paintingList);
//            }
//
//            recyclerMain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//            recyclerMain.setItemAnimator(new DefaultItemAnimator());
//            recyclerMain.setAdapter(mainRecyclerAdaperL);
//
//            mCardScaleHelper.setCurrentItemPos(0, RIGHT);
//            mCardScaleHelper.attachToRecyclerView(recyclerMain);

        intentPicList.clear();

        cur_index = 0;

        views = new View[]{delete, backBtn};

        getDataFromIntent(intent);


        if (mainRecyclerAdaperL == null && mainRecyclerAdaperP == null) {
            initView();

            setListener();

        }


    }

    public void onEventMainThread(LightEvent event) {
        changed = true;
    }
}

