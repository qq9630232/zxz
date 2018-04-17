package com.tablet.moran.model;

import java.util.List;

/**
 * Created by Stone on 2017/11/26.
 *
 * {
 "opr_type": 1,
 "pictures": [
 {
 "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%8D%97%E5%8D%8E%E7%A7%8B%E6%B0%B4.jpg",
 "gauss_img_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%8D%97%E5%8D%8E%E7%A7%8B%E6%B0%B4.png",
 "title": "人物故事：南华秋水测试1"
 },
 {
 "detail_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%90%B9%E7%AE%AB%E5%BC%95%E5%87%A4.jpg",
 "gauss_img_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%90%B9%E7%AE%AB%E5%BC%95%E5%87%A4.png",
 "title": "人物故事：吹箫引凤"
 },
 {
 "detail_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%AD%90%E8%B7%AF%E9%97%AE%E6%B4%A5.jpg",
 "gauss_img_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%AD%90%E8%B7%AF%E9%97%AE%E6%B4%A5.png",
 "title": "人物故事：子路问津"
 }
 ]
 }

 {"opr_type": 1,
 "pictures": [1, 2, 3],
 "title_info": {"detail_url": "https://s3.cn-north-1.amazonaws.com.cn/zhangxj/%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%8D%97%E5%8D%8E%E7%A7%8B%E6%B0%B4.jpg" ,
                "gauss_img_url"https://s3.cn-north-1.amazonaws.com.cn/zhangxj/N_%E4%BA%BA%E7%89%A9%E6%95%85%E4%BA%8B%E5%9B%BE10%E5%BC%A0++%E5%8D%97%E5%8D%8E%E7%A7%8B%E6%B0%B4.png":}
 }
 */

public class PlayPictureBack extends BaseModel{

    private List<Integer> pictures;
    private int opr_type;  //播放画单1
    private TitleInfo title_info;
    private int paint_id;
    private boolean news;//是否是资讯


    public boolean isNews() {
        return news;
    }

    public void setNews(boolean news) {
        this.news = news;
    }

    public int getPaint_id() {
        return paint_id;
    }

    public void setPaint_id(int paint_id) {
        this.paint_id = paint_id;
    }

    public List<Integer> getPictures() {
        return pictures;
    }

    public void setPictures(List<Integer> pictures) {
        this.pictures = pictures;
    }

    public TitleInfo getTitle_info() {
        return title_info;
    }

    public void setTitle_info(TitleInfo title_info) {
        this.title_info = title_info;
    }

    public int getOpr_type() {
        return opr_type;
    }

    public void setOpr_type(int opr_type) {
        this.opr_type = opr_type;
    }
}
