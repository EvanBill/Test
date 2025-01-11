package com.example.test.trim

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.SparseIntArray
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.common.EdLog
import com.example.common.SystemUtility
import com.example.common.ThreadUtil
import com.example.namespace.R
import com.example.test.view.customwaveview.AudioFrameMeta

/**
 * 音乐设置对话框的操作
 *
 * @author Administrator
 */
class TimelineMusicSetHelper(
    private val mContext: Context?, private var mediaPlayer: AVPlayer?,
    private val musicListener: MusicSetListener?,
    isCamera: Boolean, fromMusic: Boolean
) {
    companion object {
        const val TAG: String = "TimelineMusicDialogHelper"
        const val RESULT_CODE_NONE = 0
        const val RESULT_CODE_PLAY = 1 //播放音乐

        const val RESULT_CODE_ADD = 2 //添加到音乐

        const val RESULT_CODE_MUSIC_TRIM = 3 //设置剪切音乐时间

        const val RESULT_CODE_MUSIC_STOP = 4 //音乐暂停播放

        //一秒多少帧
        private const val RATE = 10


        //数据加载状态
        private const val STATE_START = 100
        private const val STATE_END = 101
        private const val STATE_BEATS_END = 102
        private const val STATE_SCALE = 103
        private var volume = 100

    }

    private val isLoop = true // 是否循环
    private var editorMode: String? = null // editor_mode_easy:傻瓜自动模式  editor_mode_pro：专业手动模式
    private var isCamera = false


    private var dialog: TimelineMusicTrimDialog? = null

    private var mInf: MusicInf? = null //当前操作的音乐


    private var fromMusic = false //是否来自配乐

    private var musicFromVideo = false


    init {
        this.isCamera = isCamera
        this.fromMusic = fromMusic
    }

    private val mHandler =
        Handler(Looper.getMainLooper(), Handler.Callback { msg ->
            when (msg.what) {
                STATE_START -> {
                }

                STATE_END -> {
                    val arrayList =
                        msg.obj as List<AudioFrameMeta>
                    dialog?.updateDuration(arrayList.size * 100)
                    dialog?.addFrames(arrayList)
                }

                STATE_BEATS_END -> {
                    dialog?.setBeats(msg.obj as SparseIntArray)
                }

                STATE_SCALE -> {
                    val obj = msg.obj ?: return@Callback true
                    dialog?.updateFrames(obj as List<AudioFrameMeta>)
                }
            }
            false
        })

    fun setMusicInf(inf: MusicInf?, editorMode1: String?) {
        if (inf == null) {
            return
        }
        this.mInf = inf
        this.editorMode = editorMode1
        //加载音频数据，用于显示波形图
        mHandler.sendEmptyMessage(STATE_START)
//        AVTools.nativeSetLogLevel(AVTools.kLogLevel_INFO)

        val localPath = mInf?.path ?: ""

        if (TextUtils.isEmpty(localPath)) {
            return
        }
//        waveForm = AudioWaveForm(localPath, FileManager.getCacheDir(),
//            object : AudioWaveForm.OnAudioWaveFormListener {
//                override fun onInited(waveForm: AudioWaveForm) {
//                    val samplesCount = waveForm.durationMs * RATE / 1000 + 1
//                    val arrayList = ArrayList<AudioFrameMeta>()
//                    val samples = ShortArray(samplesCount.toInt())
//                    val ret =
//                        waveForm.seekAndGetSampleDataMs(0, Long.MAX_VALUE, samples, samples.size)
//                    for (j in 0 until ret) {
//                        arrayList.add(
//                            AudioFrameMeta(
//                                samples[j].toInt(),
//                                ((j / RATE.toFloat()) * 1000).toLong(), false
//                            )
//                        )
//                    }
//                    if (arrayList.size > 0) {
//                        EdLog.d("audio", "波形图有数据")
//                    } else {
//                        EdLog.d("audio", "波形图没有数据")
//                    }
//                    EdLog.d("audio", "wave data count:" + arrayList.size)
//                    val msg = Message()
//                    msg.what = STATE_END
//                    msg.obj = arrayList
//                    mHandler.sendMessage(msg)
//                }
//
//                override fun onAudioWaveError(waveForm: AudioWaveForm?, value: String?) {
//                }
//
//            })

    }

    fun setMediaPlayer(paraMediaPlayer: AVPlayer?) {
        if (mediaPlayer == null && paraMediaPlayer != null) {
            this.mediaPlayer = paraMediaPlayer
        }
    }

    fun show(activity: FragmentActivity?, musicFromVideo: Boolean) { //每次显示都应该将时间设置为当前音乐的时间
        if (mInf != null && mInf!!.path != null && activity != null) {
            this.musicFromVideo = musicFromVideo
            val localPath = mInf?.path ?: ""

            if (TextUtils.isEmpty(localPath)) {
                return
            }
            val iconPath = if (musicFromVideo) {
                mInf?.path
            } else {
                mInf?.iconPath
            }
            dialog = TimelineMusicTrimDialog(iconPath, mInf?.name, musicFromVideo)
            updateTopBottomPaintColor(Color.WHITE)
            updateFrameBgColor(
                ContextCompat.getColor(
                    activity,
                    R.color.timeline_audio_bg
                )
            )
            updateWaveColor(
                ContextCompat.getColor(
                    activity,
                    R.color.timeline_audio_wave
                )
            )
            updateDragResId(
                R.drawable.btn_video_drag_left_nor,
                R.drawable.btn_video_drag_right_nor,
                10, 26
            )

            dialog?.setOnClickListener(object :
                TimelineMusicTrimDialog.MusicTrimDialogListener {
                override fun seekTo(process: Int) {
                    mediaPlayer?.seekTo(process)
                }

                override fun stopPlay() {
                    mediaPlayer?.stopPlay()
                }

                override fun startPlay() {
                    mediaPlayer?.startPlay()
                }

                override fun pausePlay() {
                    mediaPlayer?.pausePlay()
                }

                override fun isPlaying(): Boolean {
                    return mediaPlayer?.isPlaying() ?: false
                }

                override fun getDuration(): Int {
                    return mediaPlayer?.getDuration() ?: 0
                }

                override fun getCurrentPosition(): Int {
                    return mediaPlayer?.getCurrentPosition() ?: 0
                }


                override fun onConfirmClickListener(musicStart: Int, musicEnd: Int) {
                    addMusic(mInf, musicStart, musicEnd)
                }

                override fun onScaleListener(newCount: Int, musicDuration: Int) {
                    ThreadUtil.getThreadPool(ThreadUtil.THREAD_POOL_TYPE_CACHED).execute {

                        val tempFrameList = ArrayList<AudioFrameMeta>()
                        val samples = ShortArray(newCount)
//                        val ret =
//                            waveForm?.seekAndGetSampleDataMs(0, Long.MAX_VALUE, samples, samples.size)
//                                ?: 0
//                        val timeOperate: Double = musicDuration / newCount.toDouble()
//                        for (j in 0 until ret) {
//                            if (samples[j] < 0) {
//                                EdLog.d("audio", "wave height: " + samples[j])
//                            }
//                            tempFrameList.add(
//                                AudioFrameMeta(
//                                    samples[j].toInt(),
//                                    (timeOperate * j / 100 * 100).toLong(), false
//                                )
//                            )
//                        }
//                        EdLog.d("audio", "wave data count:" + tempFrameList.size)
//                        val msg = Message()
//                        msg.what = STATE_SCALE
//                        msg.obj = tempFrameList
//                        mHandler.sendMessage(msg)
                    }
                }
            })
            dialog?.show(activity.supportFragmentManager)
        }
    }

    /**
     * @param inf 音乐信息 添加按钮的处理
     */
    fun addMusic(inf: MusicInf?, musicStart: Int, musicEnd: Int) {
        val mSound = SoundEntity()
        mSound.gVideoStartTime = 0
        mSound.soundId = inf!!.soundId
        mSound.name = inf.name
        mSound.path = inf.path
        mSound.local_path = inf.path
        mSound.startTime = musicStart.toLong()
        if (musicEnd <= musicStart) {
            mSound.endTime = mediaPlayer!!.getDuration().toLong()
        } else {
            mSound.endTime = musicEnd.toLong()
        }
        mSound.duration = mediaPlayer!!.getDuration().toLong()
        mSound.isLoop = isLoop
        mSound.volume = volume
        mSound.musicTimeStamp = inf.musicTimeStamp
        mSound.isFromVideo = musicFromVideo
        //        if(inf.music_type==2){
//            mSound.categoryID=inf.categoryID;
//            mSound.info=inf.info;
//            mSound.music_type=2;
//        }
        val data = Intent()
        data.putExtra("item", mSound)
        //data.putExtra("volume", volume);
        //data.putExtra("musicset_voice", musicset_voice);
        inf.last_time = System.currentTimeMillis()
        if (inf.songId == 0L) {
            inf.duration = mSound.duration.toInt()
            inf.time = SystemUtility.getTimeMinSecFormt(mSound.duration.toInt())
        }
        data.putExtra("music_from_video", musicFromVideo)

        musicListener!!.handleInteraction(0, RESULT_CODE_ADD, data)
    }


    fun isShow(): Boolean {
        return dialog?.isVisible ?: false
    }

    fun dismissMusicSettingDialog() {
        if (mediaPlayer?.isPlaying() == true) {
            mediaPlayer?.stopPlay()
        }
        dialog?.dismissMusicSettingDialog()
    }

    fun updateTopBottomPaintColor(@ColorInt color: Int) {

        dialog?.updateTopBottomPaintColor(color)
    }

    fun updateFrameBgColor(@ColorInt color: Int) {
        dialog?.updateFrameBgColor(color)
    }

    fun updateWaveColor(@ColorInt color: Int) {
        dialog?.updateWaveColor(color)
    }

    fun updateDragResId(leftResId: Int, rightResId: Int, dragWidthDp: Int, dragHeightDp: Int) {
        dialog?.updateDragResId(leftResId, rightResId, dragWidthDp, dragHeightDp)
    }

    fun setMusicSeekBarProgress(musicPosition: Int) {
        dialog?.updateProgress(musicPosition)
    }
    /**
     * Activity实现该接口，Fragment和Activity共享资源
     */
    interface MusicSetListener {
        /**
         * 将音乐片段及设置传递个界面
         *
         * @param requestCode 请求码依据传递情况而定
         * @param resultCode  结果码 0表示取消 1表示添加
         * @param
         * @return handle result
         */
        fun handleInteraction(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        )

        fun setVideosMuteState()

        fun cancelMusic()


    }

}

