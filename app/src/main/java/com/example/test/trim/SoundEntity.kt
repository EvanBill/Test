package com.example.test.trim

import android.net.Uri
import com.example.common.MediaInfoUtil.getMediaDuration
import java.io.Serializable

/**
 * Sound音乐数据结构(配乐、配音、录音)
 * @author chao.chen
 */
data class SoundEntity(
    // 草稿各个效果唯一标识ID
    var uuid: Int = 1,
    /** 音乐UI显示下标*/
    var index: Int = -1,
    /** 音乐Id*/
    var soundId: Int = 0,
    var soundType: SoundType = SoundType.Music,
    var fadeIn: Boolean = true,
    var fadeOut: Boolean = true,
    /** 效果本身的开始时间，单位毫秒（ms）*/
    var startTime: Long = 0, // ms, 1000ms=1s
    /** 效果本身的开始时间，单位毫秒（ms）*/
    var endTime: Long = 0,
    /** 视频时间轴中的开始时间，单位：ms*/
    var gVideoStartTime: Long = 0,
    /** 视频时间轴中的结束时间，单位：ms*/
    var gVideoEndTime: Long = 0,
    /** 音乐名称*/
    var name: String? = "",
    /** 音乐路径*/
    var path: String? = null,
    /** 音乐本地路径*/
    var local_path: String? = null,
    /** 音乐时长*/
    var duration: Long = 0,
    // 变声频率
    var frequency: Double = 1.0,
    /** 是否循环播放(默认不自动播放)*/
    var isLoop: Boolean = true,
    /** 音乐调节音量,默认100*/
    var volume: Int = 100,
    /** 音乐调节音量临时存储*/
    var volume_tmp: Int = volume,
    /** 是否是录像视频背景音乐*/
    var isCamera: Boolean = false,
    /** 是否是主题添加*/
    var isThemeAdd: Boolean = false,
    /** 是否可删除*/
    var deletable: Boolean = true,
    /** music_timeStamp 节奏点标识*/
    var musicTimeStamp: String? = null,
    /** 是否从视频中提取的音乐，加这个字段暂时是为了埋点*/
    var isFromVideo: Boolean = false,
    /** 关联音乐类型:,2.在线音乐爱听下载*/
    var music_type: Int = 0,
    // HF音乐SDK的音乐ID
    var itemId: String = "",
    /** 关键帧列表*/
    var moveDragList: ArrayList<FxMoveDragEntity> = ArrayList<FxMoveDragEntity>(),
    var originalPath: String = "",
    //AiMusic特有参数
    var musicRequestType: String? = null,
    var musicLyrics: String? = null,
    var musicStyle: String? = null,
    var musicState: Int = 0,
) : Serializable {

    fun getCopyEntity(): SoundEntity {
        val sound = this.copy()
        val moveList = ArrayList<FxMoveDragEntity>()
        for (entity in moveDragList) {
            val entityNew = entity.copy()
            moveList.add(entityNew)
        }
        sound.moveDragList = moveList
        return sound
    }

    // 设置录制路径到录音对象
    fun setRecordPathToVoice(path: String) {
        this.path = path
        originalPath = path
        val pathUri = Uri.parse(path)
        if (pathUri != null) {
            // 获取媒体时长(视频或音频)
            this.duration = getMediaDuration(path).toLong() // 即为时长 是ms
            this.gVideoEndTime = this.gVideoStartTime + this.duration
        } else {
            this.duration = this.gVideoEndTime - this.gVideoStartTime
        }
    }

    fun getFxMoveDragEntityByTime(time: Int): FxMoveDragEntity? {
        for (entity in moveDragList) {
            if (entity.time == time) {
                return entity
            }
        }
        return null
    }

    fun removeFxMoveDragEntity(fxMoveDragEntity: FxMoveDragEntity) {
        moveDragList.remove(fxMoveDragEntity)
    }

    // 获取音乐处理后的音量
    fun getHandleVolume(): Int {
        if (soundType == SoundType.Record) {
            return volume * 4
        } else {
            return volume
        }
    }
}