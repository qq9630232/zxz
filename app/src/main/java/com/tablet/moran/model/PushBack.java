package com.tablet.moran.model;

import java.util.List;

/**
 * Created by Stone on 2017/11/26.
 *
 * 新增画作推送：
 * {
 "opr_type": 5,
 "picture_detail": {
 "picture_id": 1,
 "author": null,
 "picture_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/人物故事图10张++南华秋水.jpg",
 "detail": "人物故事：南华秋水",
 "time": "2101年",
 "size": "1700*123"
 }
 }


 -------------
 {"opr_type": 1,
    "paint_title": "\u4ec1\u4e49\u4e3b\u9898",
    "pictures": [17001060, 17000945, 17000458, 17000139, 17000301, 17000593, 17000309, 17000373],
    "paint_id": 17000001,
    "title_info": {"detail_url": "https://s3.cn-north-1.amazonaws.com.cn/moran/17001060.jpg",
    "gauss_img_url": "https://s3.cn-north-1.amazonaws.com.cn/moran/17001060B.jpg",
    "picture_url": "https://s3.cn-north-1.amazonaws.com.cn/moran/17001060A.jpg"}}
 */

public class PushBack extends BaseModel {

    public static final int PLAY = 1;
    public static final int MODE = 3;
    public static final int TIPS = 4;
    public static final int LINING = 5;
    public static final int PICTURE = 6;

    public static final int PLAY_RAND = 1;
    public static final int PLAY_ORDER = 2;
    public static final int PLAY_SINGLE = 3;


    //亮度、播放时间、播放顺序  都放在播放模式里面

    //播放模式 ： {"opr_type": 3, "play_type": 3}  1 随机  2 顺序 3 单张
    // 20 正常  21 夜间  22 睡眠

    private int opr_type;  //1:播放2： 3：播放模式  4：tips  5：内衬  6:画作  播放时间：7  循环模式：8  亮度：9
    private int frame_colour;
    private int frame_size;
    private int play_type;
    private int tips_texture;
    private int tips_location;
    private String tips_content;
    private List<Integer> pictures;
    private TitleInfo title_info;
    private Picture picture_detail;
    private int flag;//tips 删除和显示  1:显示 2：删除
    private int paint_id = 0;
    private String paint_title;

    public String getPaint_title() {
        return paint_title;
    }

    public void setPaint_title(String paint_title) {
        this.paint_title = paint_title;
    }

    public int getPaint_id() {
        return paint_id;
    }

    public void setPaint_id(int paint_id) {
        this.paint_id = paint_id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getOpr_type() {
        return opr_type;
    }

    public void setOpr_type(int opr_type) {
        this.opr_type = opr_type;
    }

    public int getFrame_colour() {
        return frame_colour;
    }

    public void setFrame_colour(int frame_colour) {
        this.frame_colour = frame_colour;
    }

    public int getFrame_size() {
        return frame_size;
    }

    public void setFrame_size(int frame_size) {
        this.frame_size = frame_size;
    }

    public int getPlay_type() {
        return play_type;
    }

    public void setPlay_type(int play_type) {
        this.play_type = play_type;
    }

    public int getTips_texture() {
        return tips_texture;
    }

    public void setTips_texture(int tips_texture) {
        this.tips_texture = tips_texture;
    }

    public int getTips_location() {
        return tips_location;
    }

    public void setTips_location(int tips_location) {
        this.tips_location = tips_location;
    }

    public String getTips_content() {
        return tips_content;
    }

    public void setTips_content(String tips_content) {
        this.tips_content = tips_content;
    }

    public TitleInfo getTitle_info() {
        return title_info;
    }

    public void setTitle_info(TitleInfo title_info) {
        this.title_info = title_info;
    }

    public List<Integer> getPictures() {
        return pictures;
    }

    public void setPictures(List<Integer> pictures) {
        this.pictures = pictures;
    }

    public Picture getPicture_detail() {
        return picture_detail;
    }

    public void setPicture_detail(Picture picture_detail) {
        this.picture_detail = picture_detail;
    }
}
