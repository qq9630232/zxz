package com.tablet.moran.tools;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ASUS on 2016/11/28.
 */

public class DateUtils {

    /**
     * type： 0代表返回年月日
     *
     * @param timeStamp1
     * @return
     */
    public static String getDateStr(int timeStamp1) {

        SLogger.d("<<", "---->>timestamp is -->>>" + timeStamp1);
        long timeStamp = timeStamp1 * 1000l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(new Date(timeStamp));
        SLogger.d("<<", "---->>timestamp is -->>>" + str);
        String s1 = str.substring(0, 10);
        String year = s1.substring(0, 4);
        String month = s1.substring(5, 7);
        String day = s1.substring(8, 10);

        return year + "年" + month + "月" + day + "日";
    }

    public static String getDateStr(long timeStamp) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(timeStamp);
        String s1 = str.substring(0, 10);
        String year = s1.substring(0, 4);
        String month = s1.substring(5, 7);
        String day = s1.substring(8, 10);

        return year + "年" + month + "月" + day + "日";
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param timestamp  较小的时间
     * @param timestamp1 较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    //TODO 算法不好
    public static int daysBetween(long timestamp, long timestamp1) throws ParseException {
        Date smdate = getDate(timestamp);
        Date bdate = getDate(timestamp1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));

    }

    /**
     * type： 0代表返回年月日
     *
     * @param timeStamp1
     * @return
     */
    public static String getDateStrtoMounth(int timeStamp1) {

        long timeStamp = timeStamp1 * 1000l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(timeStamp);
        String s1 = str.substring(0, 10);
        String year = s1.substring(0, 4);
        String month = s1.substring(5, 7);
        String day = s1.substring(8, 10);

        return month + "月" + day + "日";
    }

    //
    public static String getDateStrtoMounth222(int timeStamp1) {
        long timeStamp = timeStamp1 * 1000l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(new Date(timeStamp));
        Log.i("<<", " str = " + str);
        return str;
    }

    /**
     * 转str为Date
     *
     * @param timeStamp
     * @return
     */
    public static Date getDate(int timeStamp) {

        long timeStamp1 = timeStamp * 1000l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(timeStamp1);
        String s1 = str.substring(0, 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(s1);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Date getDate(long timeStamp) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(timeStamp);
        String s1 = str.substring(0, 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(s1);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * yyyy-MM-dd  ------->>>Date
     *
     * @param dateStr
     * @return
     */
    public static Date getDateFromFormat(String dateStr) {

        try {
            String[] dateArray = dateStr.split("-");
            return new Date((Integer.valueOf(dateArray[0]) - 1900),
                    Integer.valueOf(dateArray[1]) - 1,
                    Integer.valueOf(dateArray[2]));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * type控制时间的返回类型，0代表所有时间，1代表返回小时，2代表.....需要再加
     *
     * @param str
     * @param type
     * @return
     */
    public static String getTime(String str, int type) {
        String s1 = str.substring(11, 19);
        String hour = s1.substring(11, 13);
        String minute = s1.substring(14, 16);
        String second = s1.substring(17, 19);

        return s1;
    }

    /**
     * 根据日期获得星期
     *
     * @param timeStamp1
     * @return
     */
    public static String getWeekOfDate(int timeStamp1) {

        SLogger.d("<<", "--->>>当前时间" + timeStamp1);

        long timeStamp = timeStamp1 * 1000l;
        String[] weekDaysName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        if (getDate(timeStamp) != null) {

            calendar.setTime(getDate(timeStamp));
        }
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }
//
//    public static String getWeekOfDate(long timeStamp1) {
//
//        long timeStamp = timeStamp1*1000;
//        String[] weekDaysName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
//        Calendar calendar = Calendar.getInstance();
//        if (getDate(timeStamp) != null) {
//
//            calendar.setTime(getDate(timeStamp));
//        }
//        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//        return weekDaysName[intWeek];
//    }

    /**
     * 显示某个日期距离当前为，几天前，几个小时前，几分钟前
     *
     * @param timeStamp 过去某个时间戳；
     * @return 。。前
     */
    public static String getPassTimePan(long timeStamp) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(timeStamp);

        String year = str.substring(0, 4);
        String month = str.substring(5, 7);
        String day = str.substring(8, 10);
        String hour = str.substring(11, 13);
        String minute = str.substring(14, 16);
        String second = str.substring(17, 19);

        long currentTime = System.currentTimeMillis();

        String currentTimeStr = format.format(System.currentTimeMillis());
        String Cyeat = currentTimeStr.substring(0, 4);
        String Cmonth = str.substring(5, 7);
        String Cday = str.substring(8, 10);
        String Chour = str.substring(11, 13);
        String Cminute = str.substring(14, 16);
        String Csecond = str.substring(17, 19);

        int sDelta = (int) (currentTime - timeStamp) / 1000;
        int minuteDelta = (int) (currentTime - timeStamp) / 1000 / 60;
        int hourDelta = (int) (currentTime - timeStamp) / 1000 / 3600;
        int dayDelta = (int) (currentTime - timeStamp) / 1000 / 3600 / 24;

        if (sDelta < 60) {
            return "1分钟前";
        } else if (sDelta >= 60 && minuteDelta < 60) {
            return minuteDelta + "分钟前";
        } else if (hourDelta < 24 && minuteDelta >= 60) {
            return hourDelta + "小时前";
        } else {
            return dayDelta + "天前";
        }

    }

    /**
     * @param dateStr 2016-03-19
     * @return 3月19号
     */
    public static String toMonthDay(String dateStr) {

        return null;
    }

    /**
     * 将时间转换为时间戳 ms单位
     */
    public static String dateToStamp(String s) throws ParseException{
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

}
