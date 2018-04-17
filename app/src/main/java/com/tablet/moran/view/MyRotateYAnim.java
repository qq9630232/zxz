package com.tablet.moran.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * Created by ASUS on 2016/7/20.
 */
public class MyRotateYAnim extends Animation {
    int centerX, centerY;
    Camera camera = new Camera();

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        centerX = width / 2;
        centerY = height / 2;
        setDuration(1200);
        setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        final Matrix matrix = t.getMatrix();
        camera.save();
        camera.rotateY(360 * interpolatedTime);
        //把我们的摄像头加在变换矩阵上
        camera.getMatrix(matrix);
        //在动画前后移动选中中心点
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        camera.restore();
    }
}
