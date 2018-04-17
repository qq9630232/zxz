package com.tablet.moran.model;

/**
 * Created by Stone on 2017/11/26.
 * {
 "opr_type": 5,
 "frame_colour": 1,
 "frame_size": 2
 }
 */

public class LiningBack extends BaseModel {

  private int opr_type;
  private int frame_colour;
  private int frame_size;


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
}
