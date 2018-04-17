package com.tablet.moran.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tablet.moran.R;
import com.tablet.moran.config.Constant;
import com.tablet.moran.model.LocalPlayList;
import com.tablet.moran.model.Picture;
import com.tablet.moran.model.PlayPictureBack;
import com.tablet.moran.presenter.implPresenter.LiningActivityImpl;
import com.tablet.moran.presenter.implView.ILiningActivity;
import com.tablet.moran.tools.AppUtils;
import com.tablet.moran.tools.ImageLoader;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaintActivity extends BaseActivity implements View.OnClickListener, ILiningActivity {

    public static final int DELETE_PIC = 300;

    @BindView(R.id.image_paint)
    ImageView imagePaint;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.collect)
    ImageView collect;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.title_paint)
    TextView titlePaint;
    @BindView(R.id.category_paint)
    TextView categoryPaint;
    @BindView(R.id.back_delete)
    ImageView backDelete;
    @BindView(R.id.back_collect)
    ImageView backCollect;
    @BindView(R.id.back_back)
    ImageView backBack;
    @BindView(R.id.back_btn)
    TextView backBtn;
    @BindView(R.id.detail_LL)
    LinearLayout detailLL;
    @BindView(R.id.anim_LL)
    LinearLayout animLL;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.time)
    TextView time;

    private Picture picture;

    private View[] backViews;
    private int[] resIds = {R.id.delete, R.id.collect, R.id.back_btn};

    private LiningActivityImpl lImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        ButterKnife.bind(this);

        picture = (Picture) getIntent().getSerializableExtra(LiningActivity.PICTURE);

        backViews = new View[]{backDelete, backCollect, backBack};

        lImpl = new LiningActivityImpl(this, token, this);

//        initView();
//        setListener();

        lImpl.getPicture1(picture.getPicture_id());
    }

    private void goneAllView() {
        for (int i = 0; i < backViews.length; i++) {
            backViews[i].setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView() {
        super.initView();

        goneAllView();

        curResIndex = 2;

        try {
            backBack.setVisibility(View.VISIBLE);
            curResId = resIds[curResIndex];

            //改成加载本地存储的画
            Picture p = diskLruCacheHelper.getAsSerializable(String.valueOf(picture.getPicture_id()));
            if (p != null) {
                imagePaint.setImageBitmap(BitmapFactory.decodeFile(p.getPicture_url()));
            } else
                ImageLoader.displayImg(this, picture.getPicture_url(), imagePaint);

            titlePaint.setText(picture.getTitle());

            PlayPictureBack p1 = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
            for (int i = 0; i < p1.getPictures().size(); i++) {
                if (picture.getPicture_id() == p1.getPictures().get(i)) {
                    collect.setImageDrawable(getResources().getDrawable(R.mipmap.collected));
                    break;
                }
            }

            if (!TextUtils.isEmpty(picture.getDetail()))
                categoryPaint.setText(picture.getDetail());
            else {
                categoryPaint.setVisibility(View.GONE);
            }

            //设置作者 和 年代
            if (!TextUtils.isEmpty(picture.getAuthor())) {

                author.setVisibility(View.VISIBLE);
                author.setText(picture.getAuthor());

            } else {
                author.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(picture.getTime())) {
                time.setVisibility(View.VISIBLE);
                time.setText(picture.getTime());
            } else {
                time.setVisibility(View.GONE);
            }

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.pk_anim_show_tag);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animLL.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {


                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animLL.startAnimation(animation);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setListener() {
        super.setListener();

        delete.setOnClickListener(this);
        collect.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void toBlRight() {
        super.toBlRight();
        curResIndex++;

        curResIndex = (curResIndex == backViews.length ? 0 : curResIndex);

        goneAllView();

        backViews[curResIndex].setVisibility(View.VISIBLE);
        curResId = resIds[curResIndex];
    }

    @Override
    protected void toRight() {
        super.toRight();
        curResIndex++;

        curResIndex = (curResIndex == backViews.length ? 0 : curResIndex);

        goneAllView();

        backViews[curResIndex].setVisibility(View.VISIBLE);
        curResId = resIds[curResIndex];

    }

    @Override
    protected void toBlLeft() {
        super.toBlLeft();
        curResIndex--;

        curResIndex = (curResIndex == -1 ? (backViews.length - 1) : curResIndex);

        goneAllView();

        backViews[curResIndex].setVisibility(View.VISIBLE);
        curResId = resIds[curResIndex];
    }

    @Override
    protected void toLeft() {
        super.toLeft();

        curResIndex--;

        curResIndex = (curResIndex == -1 ? (backViews.length - 1) : curResIndex);

        goneAllView();

        backViews[curResIndex].setVisibility(View.VISIBLE);
        curResId = resIds[curResIndex];
    }

    @Override
    protected void toUp() {
        super.toUp();
        toBlUp();
    }

    @Override
    protected void toDown() {
        super.toDown();
        finish();
    }

    /**
     * 遥控器蓝牙的回调
     */
    public void blDelete() {
        showMyDialog("是否删除该画作");
        isDialogShow = true;
    }

    /**
     * 蓝牙向上
     */
    protected void toBlUp() {
        /*if (isDialogShow) {
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
        }*/

        switch (curResId) {
            case R.id.delete:
                Intent intent = new Intent(this, ResetAppActivity.class);
                intent.putExtra(ResetAppActivity.FLAG, ResetAppActivity.DELETEPIC);
                startActivityForResult(intent, DELETE_PIC);
                break;
            case R.id.collect:
                PlayPictureBack p1 = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
                boolean has = false;
                for (int i = 0; i < p1.getPictures().size(); i++) {
                    if (picture.getPicture_id() == p1.getPictures().get(i)) {
                        p1.getPictures().remove(i);
                        has = true;
                        break;
                    }
                }
                if (has) {
                    collect.setImageDrawable(getResources().getDrawable(R.mipmap.collect));
//                        p1.getPictures().add(picture.getPicture_id());
                    PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, JSON.toJSONString(p1));
                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.collect));
//                    Intent intent1 = new Intent();
//                    intent1.putExtra(Constant.PIC, 1);
//                    setResult(RESULT_OK, intent1);
                } else {
                    collect.setImageDrawable(getResources().getDrawable(R.mipmap.collected));
                    p1.getPictures().add(picture.getPicture_id());
                    SLogger.d("<<", "-收藏的id11->>" + picture.getPicture_id());
                    SLogger.d("<<", "-收藏的id22->>" + p1.getPictures().toString());
                    PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, JSON.toJSONString(p1));
                    AppUtils.showToast(getApplicationContext(), getResources().getString(R.string.collected));
                }
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == DELETE_PIC) {
                setResult(RESULT_OK);


                PlayPictureBack p = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
                for(int i = 0; i < p.getPictures().size(); i++) {
                    int pic_id = p.getPictures().get(i);

                    if(pic_id == picture.getPicture_id()){
                        p.getPictures().remove(i);
                        break;
                    }
                }
                PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, JSON.toJSONString(p));
                finish();
//                PlayPictureBack p = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
//                p.getPictures().remove(picture.getPicture_id());
//                PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, JSON.toJSONString(p));
//
//                AppUtils.showToast(getApplicationContext(), "删除成功");
//                finish();
            }
        }
    }

    /**
     * 蓝牙向下
     */
    protected void toBlDown() {
        /*if (isDialogShow) {
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
        }*/

        finish();
    }

    /**
     * 蓝牙 确认
     */
    protected void toBlOk() {

        toBlUp();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                PlayPictureBack p = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
                p.getPictures().remove(picture.getPicture_id());
                PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, JSON.toJSONString(p));
                AppUtils.showToast(getApplicationContext(), "删除成功");
                finish();
                break;
            case R.id.collect:
                PlayPictureBack p1 = JSONObject.parseObject(PreferencesUtils.getString(this, Constant.LOCAL_LIST), PlayPictureBack.class);
                p1.getPictures().add(picture.getPicture_id());
                PreferencesUtils.putString(getApplicationContext(), Constant.LOCAL_LIST, JSON.toJSONString(p1));
                AppUtils.showToast(getApplicationContext(), "收藏成功");
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        commonInit();

    }

    private void commonInit() {
        setContentView(R.layout.activity_paint);

        backBack = (ImageView) findViewById(R.id.back_back);
        backBtn = (TextView) findViewById(R.id.back_btn);
        delete = (ImageView) findViewById(R.id.delete);
        backDelete = (ImageView) findViewById(R.id.back_delete);
        collect = (ImageView) findViewById(R.id.collect);
        backCollect = (ImageView) findViewById(R.id.back_collect);
        imagePaint = (ImageView) findViewById(R.id.image_paint);
        titlePaint = (TextView) findViewById(R.id.title_paint);
        categoryPaint = (TextView) findViewById(R.id.category_paint);
        animLL = (LinearLayout) findViewById(R.id.anim_LL);
        time = (TextView) findViewById(R.id.time);
        author = (TextView) findViewById(R.id.author);

        picture = (Picture) getIntent().getSerializableExtra(LiningActivity.PICTURE);

        backViews = new View[]{backDelete, backCollect, backBack};


        lImpl = new LiningActivityImpl(this, token, this);

//        initView();
//        setListener();

        lImpl.getPicture1(picture.getPicture_id());
    }

    @Override
    public void showProgressDialog() {
        dialog.show();

    }

    @Override
    public void hidProgressDialog() {

        dialog.dismiss();
    }

    @Override
    public void showError(String error) {

//        dialog.dismiss();
//        ImageLoader.displayImg(this, new File(picture.getPicture_url()), imagePaint);

        //改成加载本地存储的画
        Picture p = diskLruCacheHelper.getAsSerializable(String.valueOf(picture.getPicture_id()));
        if (p != null) {
            imagePaint.setImageBitmap(BitmapFactory.decodeFile(p.getPicture_url()));
        } else
            ImageLoader.displayImg(this, picture.getPicture_url(), imagePaint);

        dialog.dismiss();
    }

    @Override
    public void getSuccess(Picture picture) {

        dialog.dismiss();

        this.picture = picture;

        initView();

        setListener();
    }
}
