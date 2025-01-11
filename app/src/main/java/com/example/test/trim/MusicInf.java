package com.example.test.trim;

import java.io.Serializable;

/**
 * 原始音乐信息
 * @author Administrator
 *
 */
public class MusicInf implements Serializable {
    public int soundId;
    public String name;
    public String time;
    public int duration;//音乐时长
    public long songId;
    public String path;
    public long last_time;//最后使用时间
    public String musicTimeStamp; // music_timeStamp 节奏点标识
    public String iconPath;//图标

}
