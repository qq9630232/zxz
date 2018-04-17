package com.igexin;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.tablet.moran.MainActivity;
import com.tablet.moran.activity.LiningActivity;
import com.tablet.moran.activity.TipsActivity;
import com.tablet.moran.config.Constant;
import com.tablet.moran.event.LightEvent;
import com.tablet.moran.event.LiningEvent;
import com.tablet.moran.event.PlayModeEvent;
import com.tablet.moran.model.LocalPlayList;
import com.tablet.moran.model.Picture;
import com.tablet.moran.model.PlayPictureBack;
import com.tablet.moran.model.PushBack;
import com.tablet.moran.model.TipsBack;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.SLogger;
import com.tablet.moran.tools.diskCache.DiskLruCacheHelper;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.tablet.moran.activity.BaseActivity.LIGHT_SETTING;
import static com.tablet.moran.activity.BaseActivity.NIGHT;
import static com.tablet.moran.activity.BaseActivity.NORMAL;
import static com.tablet.moran.activity.BaseActivity.SLEEP;

public class PushDemoReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */

    /**
     * "push_type": 1,  1:评论  2：点赞  3：加好友
     * "reference_data": 12,   1：comment_id  2: challenge_join_id   3:user_id
     * "android_data": "您又收到新的评论啦！快去查看吧"
     */

    public static StringBuilder payloadData = new StringBuilder();
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private Context mContext;

    protected DiskLruCacheHelper diskLruCacheHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            diskLruCacheHelper = new DiskLruCacheHelper(context);

        } catch (Exception e) {

        }

        if (builder == null)
            this.builder = new NotificationCompat.Builder(context);
        if (notificationManager == null)
            this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mContext == null)
            this.mContext = context;
        Bundle bundle = intent.getExtras();
        SLogger.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload1 = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                SLogger.d("GetuiSdkDemo", "onReceive() action===>>1");
//
                if (payload1 != null) {
                    String data1 = new String(payload1);
                    PushBack pushData = JSON.parseObject(data1, PushBack.class);
                    SLogger.d("<<", "-->>" + data1);

                    toActivity(pushData);
                }

                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                break;

            case PushConsts.THIRDPART_FEEDBACK:


                break;

            default:
                break;
        }

    }


    /**
     * 跳转到相应的页面
     * //1:播放2： 3：播放模式  4：tips  5：内衬
     *
     * @param pushBack
     */
    private void toActivity(PushBack pushBack) {
        switch (pushBack.getOpr_type()) {
            case PushBack.PLAY:

                try{
                    //建立一个推送的画单
                    int paint_id = PreferencesUtils.getInt(mContext, MainActivity.PAINT_ID);

                    Intent intentToPlay = new Intent(mContext.getApplicationContext(), LiningActivity.class);
                    PlayPictureBack pl = new PlayPictureBack();
                    pl.setTitle_info(pushBack.getTitle_info());
                    pl.setPictures(pushBack.getPictures());
                    pl.setPaint_id(pushBack.getPaint_id());
                    pl.getTitle_info().setPaint_title(pushBack.getPaint_title());
                    pl.getTitle_info().setPaint_id(pushBack.getPaint_id());
                    pl.getTitle_info().setNews(false);
                    pl.setNews(false);
                    PreferencesUtils.putInt(mContext, MainActivity.PAINT_ID, paint_id);


                    //保存推送的画单到本地
                    if(diskLruCacheHelper != null) {

                        SLogger.d("<<", "----->>收到！1" );
                        LocalPlayList lp = (LocalPlayList) diskLruCacheHelper.getAsSerializable(Constant.LOCAL_ALL_LIST);

                        if(lp == null) {
                            SLogger.d("<<", "--推送-->>>3333");

                            lp = new LocalPlayList();
                            PlayPictureBack localPb = JSONObject.parseObject(PreferencesUtils.getString(mContext, Constant.LOCAL_LIST), PlayPictureBack.class);
                            lp.getList().add(localPb);
                            lp.getList().add(pl);
                            diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);
                        } else {
                            List<PlayPictureBack> list = lp.getList();
                            boolean isHas = false;
                            for(int i = 0; i < list.size(); i++) {
                                PlayPictureBack p = list.get(i);
                                if(p.getPaint_id() == pl.getPaint_id()){
                                    isHas = true;
                                    break;
                                }
                            }
                            SLogger.d("<<", "--推送-->>>111111111111" + isHas);
                            if(!isHas) {
                                list.add(pl);
                                lp.setList(list);
                                SLogger.d("<<", "--推送-->>>222222222222222");
                                diskLruCacheHelper.put(Constant.LOCAL_ALL_LIST, lp);
                            }

                        }

                    }

                    //启动 lining activity
                    intentToPlay.putExtra(MainActivity.PAINT, pl);
                    intentToPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intentToPlay);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case PushBack.MODE:
                if (pushBack.getPlay_type() < 4 && pushBack.getPlay_type() > 0) {
                    EventBus.getDefault().post(new PlayModeEvent());
                    PreferencesUtils.putInt(mContext, Constant.PLAY_MODE, pushBack.getPlay_type());
                }

                if (pushBack.getPlay_type() > 19 && pushBack.getPlay_type() < 23){
                    switch (pushBack.getPlay_type()) {
                        case 20:
                            PreferencesUtils.putInt(mContext, LIGHT_SETTING, NORMAL);
                            break;
                        case 22:
                            PreferencesUtils.putInt(mContext, LIGHT_SETTING, SLEEP);
                            break;

                        case 21:
                            PreferencesUtils.putInt(mContext, LIGHT_SETTING, NIGHT);
                            break;

                    }
                    EventBus.getDefault().post(new LightEvent());

                }

                break;

            //TODO 内衬，先搞这个
            case PushBack.LINING:
                PreferencesUtils.putInt(mContext, LiningActivity.FRAME_COLOR, pushBack.getFrame_colour());
                PreferencesUtils.putInt(mContext, LiningActivity.FRAME_SIZE, pushBack.getFrame_size());
                EventBus.getDefault().post(new LiningEvent());
//                Intent intentToLining = new Intent(mContext.getApplicationContext(), LiningActivity.class);
                /*LiningBack liningBack = new LiningBack();
                liningBack.setFrame_colour(pushBack.getFrame_colour());
                liningBack.setFrame_size(pushBack.getFrame_size());*/
//                intentToLining.putExtra(LiningActivity.FRAME_SIZE, pushBack.getFrame_size());
//                intentToLining.putExtra(LiningActivity.FRAME_COLOR, pushBack.getFrame_colour());
//                intentToLining.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intentToLining);
                break;
            case PushBack.TIPS:
                Intent intentToTips = new Intent(mContext.getApplicationContext(), LiningActivity.class);
                TipsBack tipsBack = new TipsBack();
                tipsBack.setTips_content(pushBack.getTips_content());
                tipsBack.setTips_location(pushBack.getTips_location());
                tipsBack.setTips_texure(pushBack.getTips_texture());
                tipsBack.setFlag(pushBack.getFlag());
                intentToTips.putExtra(TipsActivity.POSITION, tipsBack);
                intentToTips.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentToTips);
                break;

            case PushBack.PICTURE:
                Intent intentToLining = new Intent(mContext.getApplicationContext(), LiningActivity.class);
                /*LiningBack liningBack = new LiningBack();
                liningBack.setFrame_colour(pushBack.getFrame_colour());
                liningBack.setFrame_size(pushBack.getFrame_size());*/
//                    intentToLining.putExtra(LiningActivity.FRAME_SIZE, pushBack.getFrame_size());
//                    intentToLining.putExtra(LiningActivity.FRAME_COLOR, pushBack.getFrame_colour());
                Picture picture = pushBack.getPicture_detail();
                intentToLining.putExtra(LiningActivity.PICTURE, picture);
                intentToLining.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentToLining);
                break;
        }
    }


}
