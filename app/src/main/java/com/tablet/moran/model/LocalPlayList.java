package com.tablet.moran.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stone on 2017/12/20.
 */

public class LocalPlayList extends BaseModel {

    private List<PlayPictureBack> list = new ArrayList<>();

    public List<PlayPictureBack> getList() {
        return list;
    }

    public void setList(List<PlayPictureBack> list) {
        this.list = list;
    }
}
