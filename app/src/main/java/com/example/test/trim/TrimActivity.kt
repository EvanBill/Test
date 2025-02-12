package com.example.test.trim

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Handler.Callback
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.namespace.BuildConfig
import com.example.namespace.R
import com.example.namespace.databinding.ActivityMainBinding
import com.example.test.trim.AVPlayer
import com.example.test.trim.MusicInf
import com.example.test.trim.TimelineMusicSetHelper
import com.example.test.work.WorkManagerUtil
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

class TrimActivity : AppCompatActivity(), TimelineMusicSetHelper.MusicSetListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var musicSetHelper: TimelineMusicSetHelper? = null
    private var mediaPlayer: AVPlayer? = null
    private var editorMode = "editor_mode_pro"
    private var musicStart = 0
    private var musicEnd = 0
    private var isLoop = false
    private var mTimer: Timer? = null
    private var mTimerTask: AudioTimerTask? = null
    companion object{
        // 音乐播放进度
        private const val EVENT_MUSIC_PLAY_ING = 0
        private const val REFRESH_PLAY_STATE_TIME = 100L

    }


    private var myHandler: Handler? = Handler(Looper.getMainLooper(),object :Callback{
        override fun handleMessage(msg: Message): Boolean {

            when (msg.what) {
                // 音乐播放中显示进度条
                EVENT_MUSIC_PLAY_ING -> if (musicSetHelper != null) {
                    musicSetHelper?.setMusicSeekBarProgress(msg.arg1)
                }
            }

            return true
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        musicSetHelper = TimelineMusicSetHelper(this, mediaPlayer, this,false, false)


    }

    fun showAudio(inf: MusicInf?, showForVideo: Boolean = false) {
        try {
            if (inf == null) return
            musicSetHelper?.let {
                it.setMusicInf(inf, editorMode)
                it.setMediaPlayer(mediaPlayer)
                it.show(this,showForVideo)
            }

        } catch (e: java.lang.Exception) {

        }
    }

    override fun handleInteraction(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode){
            TimelineMusicSetHelper.RESULT_CODE_PLAY -> data?.let{
                val inf = data.getSerializableExtra("item") as MusicInf?
                play(inf?.path)
            }
            // 直接将数据回传到上一界面
            TimelineMusicSetHelper.RESULT_CODE_ADD ->{

            }
        }
    }

    override fun setVideosMuteState() {
        TODO("Not yet implemented")
    }

    override fun cancelMusic() {
        TODO("Not yet implemented")
    }
    private fun play(path: String?) {
        try {
            try {
                mediaPlayer?.let {
//                    it.stop()
//                    it.release() // 把各项参数恢复到最初始的状态
//                    it.dataSource = path
//                    it.setOnPreparedListener { _ ->
//                        it.start()
//                        if (musicEnd == 0) musicEnd = it.duration
//                    }
//                    it.prepareAsync()
//                    it.setVolume(1f, 1f)
//                    it.isLooping = isLoop
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (mTimer == null) mTimer = Timer(true)
            mTimer?.purge()
            if (mTimerTask != null) {
                mTimerTask?.cancel()
                mTimerTask = null
            }
            mTimerTask = AudioTimerTask()
            mTimer?.schedule(mTimerTask, 0, REFRESH_PLAY_STATE_TIME)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun stopTimer() {
        if (mTimer != null) {
            mTimer?.purge()
            // 原任务从队列中移除
            if (mTimerTask != null) {
                mTimerTask?.cancel()
                mTimerTask = null
            }
            mTimer?.cancel()
        }
    }
    private inner class AudioTimerTask : TimerTask() {
        override fun run() {
            try {
                mediaPlayer?.let {
                    if (it.isPlaying()) {
                        val msg = Message()
                        msg.what = EVENT_MUSIC_PLAY_ING
                        msg.arg1 = it.getCurrentPosition()
                        msg.arg2 = it.getDuration()
                        myHandler?.sendMessage(msg)
                        if (it.getCurrentPosition() >= musicEnd) {
                            if (isLoop) {
                                it.seekTo(musicStart)
                            } else {
                                it.pausePlay()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}