package com.example.common;

public class SystemUtility {


    public static String getTimeString(int msec) {
        if (msec < 0) {
            return String.format("--:--:--");
        }
        int total = msec / 1000;
        int hour = total / 3600;
        total = total % 3600;
        int minute = total / 60;
        int second = total % 60;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 时间的转换
     * 
     * @param time
     * @return
     */
    public static String getTimeMinSecFormt(int time) {
        return getTimeMinSecMsFormt(time, "%02d:%02d.%01d");
    }


    /**
     * 时间的转换
     *
     * @param time
     * @return
     */
    public static String getTimeMinSecNoMilliFormt(int time) {
        return getTimeMinSecMsFormt(time, "%02d:%02d");
    }

	/**
	 * 格式化到分秒
	 * @param msec
	 * @return
	 */
	public static String formatMsecToMinuteAndMsec(int msec){
	    if (msec < 0) {
            return String.format("--:--");
        }
        
        int total = msec / 1000;
        //int hour = total / 3600;
        total = total % 3600;
        int minute = total / 60;
        int second = total % 60;
        //int tail = (msec % 1000)/100;

        return String.format("%02d:%02d", minute, second);
	}

    /**
     * 带小数点毫秒格式字符串
     * 00:00.1
     * @param time
     * @return
     */
    public static String getTimeMinSecMsFormt(int time) {
        return getTimeMinSecMsFormt(time, "%02d:%02d.%01d");
    }
    
    /**
     * 带小数点毫秒格式字符串
     * 00:00.1
     * @param time
     * @param format 格式
     * @return
     */
    public static String getTimeMinSecMsFormt(int time,String format) {
        int timeTmp = time;
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format(format, minute, second, (timeTmp - (timeTmp / 1000) * 1000) / 100);
    }

    /**
     * 时间格式，毫秒四舍五入到秒
     * @param time
     * @return
     */
    public static String getTimeMinSecMsFormtRound(int time) {
        int timeTmp = time;
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        int ms = (timeTmp - (timeTmp / 1000) * 1000)/100;
        if(ms>=5){
            //小数毫秒点四舍五入计算
            second = second+1;
            if(second>=60){
                minute = minute+1;
                second = 0;
            }
        }
        return String.format("%02d:%02d", minute, second);
    }


    
    /**
     * 分秒格式字符串
     * 00:00
     * @param time
     * @return
     */
    public static String getMinSecFormtTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format("%02d:%02d", minute, second);
    }




}
