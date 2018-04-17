package com.igexin;

/**
 * Created by ASUS on 2016/7/29.
 */

/**
 * "push_type": 1,  1:评论  2：点赞  3：加好友
 * "reference_data":    'TOMORROW' => 1,
 * 'ALL' => 2,
 * 'PAY_SUCCESS' => 3,
 * 'WITHDRAW_SUCCESS' => 4,
 * <p/>
 * "android_data": "您又收到新的评论啦！快去查看吧"
 */
public class PushContent {
    private String android_data;
    private int push_type;
    private int reference_data;

    private PUSH_TYPE currentType;


    public PUSH_TYPE getCurrentType() {
        switch (push_type) {
            case 1:
                currentType = PUSH_TYPE.TOMORROW;
                break;

            case 2:
                currentType = PUSH_TYPE.ALL;
                break;

            case 3:
                currentType = PUSH_TYPE.PAY_SUCCESS;
                break;

            default:
                currentType = PUSH_TYPE.WITHDRAW_SUCCESS;
                break;

        }

        return currentType;

    }

    public void setCurrentType(PUSH_TYPE currentType) {
        this.currentType = currentType;
    }

    public String getAndroid_data() {
        return android_data;
    }

    public void setAndroid_data(String android_data) {
        this.android_data = android_data;
    }

    public int getPush_type() {
        return push_type;
    }

    public void setPush_type(int push_type) {
        this.push_type = push_type;
    }

    public int getReference_data() {
        return reference_data;
    }

    public void setReference_data(int reference_data) {
        this.reference_data = reference_data;
    }

    enum PUSH_TYPE {
        TOMORROW,
        ALL,
        PAY_SUCCESS,
        WITHDRAW_SUCCESS
    }
}
