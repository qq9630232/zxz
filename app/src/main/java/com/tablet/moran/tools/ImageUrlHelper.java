package com.tablet.moran.tools;


/**
 * Created by tristan on 15/11/30.
 */

public class ImageUrlHelper {

    private final static String WIDTH_640 = "_640";
    private final static String WIDTH_383 = "_383";
    private final static String WIDTH_227 = "_227";
    private final static String WIDTH_750 = "_750";
    private final static String WIDTH_957 = "_957";
    private final static String WIDTH_1080 = "_1080";
    private final static String WIDTH_1615 = "_1615";
    private final static String WIDTH_337 = "_337";
    private final static String WIDTH_101 = "_101";

    public static String getHouseUrlParams(int width){

        if (width<=227){
            return WIDTH_383;
        }else if (width<383){
            return WIDTH_227;
        }else if (width<=640){
            return WIDTH_640;
        }else if (width<=695){
            return WIDTH_640;
        }else if (width<=750){
            return WIDTH_750;
        }else if (width<840){
            return WIDTH_750;
        }else if (width<957){
            return WIDTH_957;
        }else if (width<1020){
            return WIDTH_957;
        }else if (width<1080){
            return WIDTH_1080;
        }else if (width<1348){
            return WIDTH_1080;
        }else {
            return WIDTH_1615;
        }
    }

    public static String getUserUrlParams(int width) {
        if (width <= 101) {
            return WIDTH_337;
        } else if (width <= 219) {
            return WIDTH_337;
        } else if (width <= 337) {
            return WIDTH_337;
        } else if (width <= 640) {
            return WIDTH_640;
        } else if (width <= 750) {
            return WIDTH_750;
        } else if (width < 1080) {
            return WIDTH_1080;
        }else {
            return  WIDTH_1080;
        }
    }

}
