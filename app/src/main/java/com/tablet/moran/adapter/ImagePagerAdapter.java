package com.tablet.moran.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tablet.moran.R;
import com.tablet.moran.tools.ImageLoader;

import java.util.List;

/**
 * Created by ASUS on 2017/2/27.
 */
public class ImagePagerAdapter extends MeBasePagerAdapter<String> {


    public ImagePagerAdapter(List<String> data, Context context){
        super(data, context);
    }

    @Override
    public View fetchItemView(String s) {

        final LinearLayout FL = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.pager_item, null);
        ImageView bannerImg = (ImageView) FL.findViewById(R.id.pager_image);

        ImageLoader.displayImg(context, s, bannerImg);
        return FL;
    }
}
