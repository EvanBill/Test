package com.example.test.trim

import java.io.Serializable

/**
 * 效果的移动轨迹数据结构
 * @author chao.chen
 */
data class FxMoveDragEntity(
    // 草稿各个效果唯一标识ID
    var uuid: Int = 1,
    var time: Int = 0,
    var volume: Int = 100,
    /** 显示开始时间,单位：S*/
    @JvmField var startTime: Float = 0f ,
    /** 显示结束时间,单位：S*/
    @JvmField var endTime: Float = 0f ,
    /** 全局EnMediaController显示X*/
    @JvmField var posX: Float= 0f ,
    /** 全局EnMediaController显示Y*/
    @JvmField var posY: Float = 0f,
    // 缩放比例
    var scale: Float = 1.0f,
    var scaleY: Float = 1.0f,
    var rotate: Float = 0f
) : Serializable
