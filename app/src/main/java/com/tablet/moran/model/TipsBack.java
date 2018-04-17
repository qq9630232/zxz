package com.tablet.moran.model;

/**
 * Created by Stone on 2017/11/26.
 * {
 "opr_type": 4,
 "tips_content": "hello baby",
 "tips_location": 1,
 "tips_texture": 2
 }
 */

public class TipsBack extends BaseModel {

    private int opr_type;
    private int tips_texure;
    private int tips_location;
    private String tips_content;
    private int flag; //1: 添加  2：删除

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

    public int getTips_texure() {
        return tips_texure;
    }

    public void setTips_texure(int tips_texure) {
        this.tips_texure = tips_texure;
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
}
