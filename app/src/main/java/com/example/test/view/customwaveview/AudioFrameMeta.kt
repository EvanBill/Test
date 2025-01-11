package com.example.test.view.customwaveview

/**
 * 音频每一帧的数据信息
 */
class AudioFrameMeta(
    //该帧的原有振幅
    var originalHeight: Int = 0,
    //该帧的时间点
    var time: Long = 0,
    //是否是节拍点
    var isBeatPoint: Boolean = false) {
    //该帧的实际振幅
    var height: Int = 0
    //显示的时间字符串,按一定格式
    var timeStr: String? = null
}


/**
 * 展示样式，声纹连续类型还是矩形
 */
enum class Style {
    RECT,
    CONTINUE
}

/**
 * 重心点
 */
enum class StyleGravity {
    TOP,
    CENTER,
    BOTTOM
}

enum class PlayState {
    UNSPECIFIED,
    PLAY,
    PAUSE,
    STOP
}


enum class DragSelect {
    NONE,
    LEFT,
    RIGHT
}
