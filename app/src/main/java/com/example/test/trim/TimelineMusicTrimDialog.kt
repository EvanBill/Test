package com.example.test.trim

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.SparseIntArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.common.DensityTools
import com.example.common.EdLog
import com.example.common.KeyboardUtil
import com.example.common.SystemUtility
import com.example.namespace.R
import com.example.namespace.databinding.TimelineDialogMusicTrimBinding
import com.example.test.view.BaseDialogFragment
import com.example.test.view.customwaveview.AudioFrameMeta
import com.example.test.view.customwaveview.DragSelect
import com.example.test.view.customwaveview.OnFrameChangeListener
import com.example.test.view.customwaveview.OnSideDragListener
import com.example.test.view.customwaveview.OnWaveScrollChangeListener
import com.example.test.view.customwaveview.PlayState
import com.example.test.view.customwaveview.Style
import com.example.test.view.customwaveview.StyleGravity
import kotlin.math.abs


class TimelineMusicTrimDialog(private val iconPath: String?, val name: String?,val musicFromVideo:Boolean) :
    BaseDialogFragment(), View.OnClickListener {
    private var lineTopBottomPaintColor: Int? = null
    private var frameBgColor: Int? = null
    private var waveColor: Int? = null
    private var leftDragResId: Int? = null
    private var rightDragResId: Int? = null
    private var dragWidthDp: Int? = null
    private var dragHeightDp: Int? = null
    private var isRestarted = false
    private var needPurchase = false
    private var musicTrimStart = 0
    private var musicTrimEnd = 0
    private var musicTrimDuration = 1
    private var musicCurrent = 0
    private var isScaled = true
    private var musicTrimDialogListener: MusicTrimDialogListener? = null

    private var binding: TimelineDialogMusicTrimBinding? = null
    private var mOnGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var keyboardShowBeforeContent1 = ""
    private var keyboardShowBeforeContent2 = ""
    private var keyboardShowBeforeContent3 = ""
    private var keyboardShowBeforeContent4 = ""
    private var keyboardShowBeforeContent5 = ""
    private var keyboardShowBeforeContent6 = ""
    private var isKeyboardShow = false
    private var isKeyboardDone = false

    companion object {
        const val TAG = "TimelineMusicTrimDialog"
    }

    override fun layoutId(): Int {
        return R.layout.timeline_dialog_music_trim
    }

    override fun isFullScreen(): Boolean {
        return true
    }

    override fun initView(rootView: View) {
        binding = TimelineDialogMusicTrimBinding.bind(rootView)
        binding?.apply {
            dialog?.setCancelable(true)
            dialog?.setCanceledOnTouchOutside(true)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val decorView = activity?.window?.decorView
            decorView?.let {
                mOnGlobalLayoutListener = KeyboardUtil.observeSoftKeyboard(
                    decorView
                ) { _, visible ->
                    if (visible) {
                        isKeyboardShow = true
                        keyboardShowBeforeContent1 =
                            tvDialogMusicSettingTimeStart1.text?.toString() ?: ""
                        keyboardShowBeforeContent2 =
                            tvDialogMusicSettingTimeStart2.text?.toString() ?: ""
                        keyboardShowBeforeContent3 =
                            tvDialogMusicSettingTimeStart3.text?.toString() ?: ""
                        keyboardShowBeforeContent4 =
                            tvDialogMusicSettingTimeEnd1.text?.toString() ?: ""
                        keyboardShowBeforeContent5 =
                            tvDialogMusicSettingTimeEnd2.text?.toString() ?: ""
                        keyboardShowBeforeContent6 =
                            tvDialogMusicSettingTimeEnd3.text?.toString() ?: ""
                    } else {
                        if (isKeyboardShow && !isKeyboardDone) {
                            tvDialogMusicSettingTimeStart1.setText(keyboardShowBeforeContent1)
                            tvDialogMusicSettingTimeStart2.setText(keyboardShowBeforeContent2)
                            tvDialogMusicSettingTimeStart3.setText(keyboardShowBeforeContent3)
                            tvDialogMusicSettingTimeEnd1.setText(keyboardShowBeforeContent4)
                            tvDialogMusicSettingTimeEnd2.setText(keyboardShowBeforeContent5)
                            tvDialogMusicSettingTimeEnd3.setText(keyboardShowBeforeContent6)
                        }
                        isKeyboardDone = false
                        isKeyboardShow = false
                    }
                }
            }

            tvDialogMusicSettingTimeStart1.imeOptions = EditorInfo.IME_ACTION_DONE
            tvDialogMusicSettingTimeStart1.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyboardDone = true
                    if (TextUtils.isEmpty(v.getText())) {
                        tvDialogMusicSettingTimeStart1.setText("00")
                    }
                    if (checkInputAvailable()) {
                        KeyboardUtil.hideKeyboard(context, v)
                    }
                    return@setOnEditorActionListener true
                }
                false
            }
            tvDialogMusicSettingTimeStart2.imeOptions = EditorInfo.IME_ACTION_DONE
            tvDialogMusicSettingTimeStart2.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyboardDone = true
                    if (TextUtils.isEmpty(v.getText())) {
                        tvDialogMusicSettingTimeStart2.setText("00")
                    }
                    if (checkInputAvailable()) {
                        KeyboardUtil.hideKeyboard(context, v)
                    }
                    return@setOnEditorActionListener true
                }
                false
            }

            tvDialogMusicSettingTimeStart3.imeOptions = EditorInfo.IME_ACTION_DONE
            tvDialogMusicSettingTimeStart3.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyboardDone = true
                    if (TextUtils.isEmpty(v.getText())) {
                        tvDialogMusicSettingTimeStart3.setText("0")
                    }
                    if (checkInputAvailable()) {
                        KeyboardUtil.hideKeyboard(context, v)
                    }
                    return@setOnEditorActionListener true
                }
                false
            }

            tvDialogMusicSettingTimeEnd1.imeOptions = EditorInfo.IME_ACTION_DONE
            tvDialogMusicSettingTimeEnd1.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyboardDone = true
                    if (TextUtils.isEmpty(v.getText())) {
                        tvDialogMusicSettingTimeEnd1.setText("00")
                    }
                    if (checkInputAvailable()) {
                        KeyboardUtil.hideKeyboard(context, v)
                    }
                    return@setOnEditorActionListener true
                }

                false
            }

            tvDialogMusicSettingTimeEnd2.imeOptions = EditorInfo.IME_ACTION_DONE
            tvDialogMusicSettingTimeEnd2.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyboardDone = true
                    if (TextUtils.isEmpty(v.getText())) {
                        tvDialogMusicSettingTimeEnd2.setText("00")
                    }
                    if (checkInputAvailable()) {
                        KeyboardUtil.hideKeyboard(context, v)

                    }
                    return@setOnEditorActionListener true
                }
                false
            }

            tvDialogMusicSettingTimeEnd3.imeOptions = EditorInfo.IME_ACTION_DONE
            tvDialogMusicSettingTimeEnd3.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isKeyboardDone = true
                    if (TextUtils.isEmpty(v.getText())) {
                        tvDialogMusicSettingTimeEnd3.setText("0")
                    }
                    if (checkInputAvailable()) {
                        KeyboardUtil.hideKeyboard(context, v)
                    }
                    return@setOnEditorActionListener true
                }
                false
            }


            tvDialogMusicSettingTimeStart1.setOnClickListener(this@TimelineMusicTrimDialog)
            viewDialogMusicSettingCancel.setOnClickListener(this@TimelineMusicTrimDialog)
            tvDialogMusicSettingUse.setOnClickListener(this@TimelineMusicTrimDialog)
            ivDialogMusicSettingAvatar.setOnClickListener(this@TimelineMusicTrimDialog)

            tvDialogMusicSettingTimeStart1.setOnTouchListener { _, _ ->
                setPlayButtonPauseState()


                return@setOnTouchListener false
            }
            tvDialogMusicSettingTimeStart2.setOnTouchListener { _, _ ->
                setPlayButtonPauseState()
                return@setOnTouchListener false
            }
            tvDialogMusicSettingTimeStart3.setOnTouchListener { _, _ ->
                setPlayButtonPauseState()

                return@setOnTouchListener false
            }
            tvDialogMusicSettingTimeEnd1.setOnTouchListener { _, _ ->
                setPlayButtonPauseState()
                return@setOnTouchListener false
            }
            tvDialogMusicSettingTimeEnd2.setOnTouchListener { _, _ ->
                setPlayButtonPauseState()
                return@setOnTouchListener false
            }
            tvDialogMusicSettingTimeEnd3.setOnTouchListener { _, _ ->
                setPlayButtonPauseState()
                return@setOnTouchListener false
            }

            ivDialogMusicSettingAvatar.let {
                if (iconPath.isNullOrBlank()) {
                    it.setImageResource(R.drawable.timeline_shape_bg_music_trim_play)
                } else {
                    if (musicFromVideo){
                        Glide.with(rootView)
                            .asBitmap()
                            .load(iconPath)
                            .apply(
                                RequestOptions()
                                    .transform(CenterCrop(), RoundedCorners(12))
                            )
                            .into(it)
                    }else{
                        Glide.with(rootView)
                            .load(iconPath)
                            .apply(
                                RequestOptions()
                                    .transform(CenterCrop(), RoundedCorners(50))
                            )
                            .into(it)
                    }

                }
            }


            ivDialogMusicSettingPlay.setSelected(true)
            tvDialogMusicSettingName.text = name
            initAudioLine()
            musicTrimStart = 0
            musicTrimEnd = musicTrimDialogListener?.getDuration() ?: 0
            updateTrimTime(0, musicTrimStart)
            updateTrimTime(1, musicTrimEnd)
        }

    }

    private fun initAudioLine() {
        binding?.apply {
            audioLineScrollView.audioLineView.displayStyle = Style.RECT
            audioLineScrollView.audioLineView.styleGravity = StyleGravity.CENTER
            if (lineTopBottomPaintColor != null) {
                audioLineScrollView.audioLineView.updateTopBottomPaintColor(lineTopBottomPaintColor!!)
            }
            if (frameBgColor != null) {
                audioLineScrollView.audioLineView.updateFrameBgColor(frameBgColor!!)
            }
            if (waveColor != null) {
                audioLineScrollView.audioLineView.updateFramePaintColor(waveColor!!)
            }
            if (leftDragResId != null && rightDragResId != null && dragWidthDp != null && dragHeightDp != null) {
                audioLineScrollView.audioLineView.updateDragResId(
                    leftDragResId!!,
                    rightDragResId!!,
                    dragWidthDp!!,
                    dragHeightDp!!
                )
            }

            audioLineScrollView.setAudioPadding()
            val lp = audioLineScrollView.audioLineView.layoutParams as ViewGroup.MarginLayoutParams
            lp.height = DensityTools.dp2px(context, 38f)
            lp.topMargin = DensityTools.dp2px(context, 8f)
            lp.bottomMargin = DensityTools.dp2px(context, 8f)
            audioLineScrollView.audioLineView.layoutParams = lp
            updateAudioLinePlayState(PlayState.PLAY)

            audioLineScrollView.addLeftDragListener(object : OnSideDragListener {
                override fun onDrag(v: View, select: DragSelect, x: Int, rawX: Int, time: Long) {
                    updateTrimTime(0, time.toInt())
                    setPlayButtonPauseState()
                }

                override fun onDraged(v: View, select: DragSelect, x: Int, time: Long) {
                    musicTrimStart = time.toInt()
                    seekToPlay(true)

                }

            })

            audioLineScrollView.addRightDragListener(object : OnSideDragListener {
                override fun onDrag(v: View, select: DragSelect, x: Int, rawX: Int, time: Long) {
                    updateTrimTime(1, time.toInt())
                    setPlayButtonPauseState()
                }

                override fun onDraged(v: View, select: DragSelect, x: Int, time: Long) {
                    musicTrimEnd = Math.min(time.toInt(), musicTrimDuration)
                    seekToPlay(false)
                }

            })
            audioLineScrollView.addFrameChangeListener(object : OnFrameChangeListener {
                override fun onScale(v: View?, scale: Float, x: Int, newCount: Int) {
                    isScaled = false
                    setPlayButtonPauseState()
                    musicTrimDialogListener?.onScaleListener(newCount, musicTrimDuration)
                    audioLineScrollView.scrollToTime(musicCurrent.toLong())
                }

                override fun onScaled(v: View?) {
                    isScaled = true
                }

            })
            audioLineScrollView.addWaveScrollChangeListener(object : OnWaveScrollChangeListener {
                override fun onScroll(
                    view: View?, dx: Int, dy: Int, byHand: Boolean, centerTime: Long,
                ) {
                    if (byHand) {
                        setPlayButtonPauseState()
                        tvDialogMusicSettingTimeTip.text =
                            SystemUtility.getTimeMinSecFormt(centerTime.toInt())
                    }

                    if (isScaled) {
                        musicCurrent = centerTime.toInt()
                    }
                }


            })
        }


    }


    fun addFrames(arrayList: List<AudioFrameMeta>) {
        binding?.audioLineScrollView?.addFrames(arrayList)
    }

    fun setBeats(beats: SparseIntArray) {
        binding?.audioLineScrollView?.setBeats(beats)
    }

    fun updateFrames(audioFrameMetas: List<AudioFrameMeta>) {
        binding?.audioLineScrollView?.audioLineView?.updateFrames(audioFrameMetas)
    }

    private fun updateCurrentTime(currentTime: String) {
        binding?.tvDialogMusicSettingTimeTip?.text = currentTime
    }

    fun updateDuration(musicDuration: Int) {
        this.musicTrimDuration = musicDuration
        if (musicTrimEnd == 0)
            musicTrimEnd = musicDuration
        EdLog.d(TAG, "wave LOAD $musicTrimEnd   duration:$musicDuration")
        updateTrimTime(1, musicTrimEnd)
    }

    /**
     * 设置播放按钮暂停状态
     */
    private fun setPlayButtonPauseState() {
        if (musicTrimDialogListener?.isPlaying() == true) {
            musicTrimDialogListener?.pausePlay()
            updateAudioLinePlayState(PlayState.PAUSE)
            binding?.ivDialogMusicSettingPlay?.isSelected = false
        }
    }

    private fun seekToPlay(isStart: Boolean) {
        val pos = musicTrimDialogListener?.getCurrentPosition() ?: 0
        if (pos < musicTrimStart || pos >= musicTrimEnd || musicTrimDialogListener?.isPlaying() != true) {
            isRestarted = true
            val toLoc = if (isStart) musicTrimStart else musicTrimEnd
            musicTrimDialogListener?.seekTo(musicTrimStart)
            binding?.audioLineScrollView?.scrollToTime(toLoc.toLong())
        }
    }

    private fun resumePlay() {
        isRestarted = true
        if (musicCurrent < musicTrimStart || musicCurrent > musicTrimEnd) {
            musicTrimDialogListener?.seekTo(musicTrimStart + 400)
        } else {
            if (abs((musicTrimDialogListener?.getCurrentPosition() ?: 0) - musicCurrent) > 400) {
                musicTrimDialogListener?.seekTo(musicCurrent + 400)//防止计算误差
            }
        }
        musicTrimDialogListener?.startPlay()
        updateAudioLinePlayState(PlayState.PLAY)
    }


    fun dismissMusicSettingDialog() {
        val decorView = activity?.window?.decorView
        decorView?.let {
            KeyboardUtil.removeSoftKeyboardObserver(decorView, mOnGlobalLayoutListener)
        }
        binding?.audioLineScrollView?.resetData()
        isRestarted = false

        musicTrimDialogListener?.stopPlay()
        updateAudioLinePlayState(PlayState.STOP)
        dialog?.dismiss()
    }


    fun setMusicSeekBarProgress(musicPosition: Int) {
        if (binding?.audioLineScrollView == null) {
            return
        }
        binding?.tvDialogMusicSettingTimeTip?.text = SystemUtility.getTimeMinSecFormt(musicPosition)

        binding?.ivDialogMusicSettingPlay?.isSelected = musicTrimDialogListener?.isPlaying() == true

        if (musicPosition < musicTrimStart && !isRestarted) {
            isRestarted = true
            musicTrimDialogListener?.seekTo(musicTrimStart + 400)//底层播放器有400ms误差所以添加此值
            binding?.audioLineScrollView?.scrollToTime(musicTrimStart.toLong())
            return
        }

        if (musicTrimEnd in 1..musicPosition) {                 //播放完毕后位置
            isRestarted = true
            musicTrimDialogListener?.seekTo(musicTrimStart)
            binding?.audioLineScrollView?.scrollToTime(musicTrimStart.toLong())
            return
        }

        if (musicPosition - musicCurrent != 100) {       //拖动后位置
            binding?.audioLineScrollView?.scrollToTime(musicPosition.toLong())
            isRestarted = false
            return
        }

        if (!isRestarted && musicPosition - musicTrimStart >= 100) { //开始位置
            binding?.audioLineScrollView?.scrollToTime(musicPosition.toLong())
        }
    }

    fun updateTrimTime(index: Int, trimTime: Int) {
        val time = SystemUtility.getTimeMinSecMsFormt(trimTime, "%02d:%02d:%01d")
        if (index == 1) {
            if (time.isNotBlank()) {
                val timeArray = time.split(":")
                if (timeArray.size == 3) {
                    binding?.apply {
                        tvDialogMusicSettingTimeEnd1.setText(timeArray[0])
                        tvDialogMusicSettingTimeEnd2.setText(timeArray[1])
                        tvDialogMusicSettingTimeEnd3.setText(timeArray[2])
                    }
                }
            }

        } else {
            if (time.isNotBlank()) {
                val timeArray = time.split(":")
                if (timeArray.size == 3) {
                    binding?.apply {
                        tvDialogMusicSettingTimeStart1.setText(timeArray[0])
                        tvDialogMusicSettingTimeStart2.setText(timeArray[1])
                        tvDialogMusicSettingTimeStart3.setText(timeArray[2])
                    }

                }
            }
        }

    }

    fun updateAudioLinePlayState(playState: PlayState) {
        binding?.audioLineScrollView?.audioLineView?.updatePlayState(playState)
    }

    fun updateProgress(musicPosition: Int) {
        if ((musicPosition - musicTrimStart > 0) && (musicTrimEnd - musicTrimStart > 0) && (musicPosition <= musicTrimEnd)) {
            updateCurrentTime(SystemUtility.getTimeMinSecFormt(musicPosition))
            setMusicSeekBarProgress(musicPosition)
        }
    }

    fun setOnClickListener(musicTrimDialogListener: MusicTrimDialogListener?) {
        this.musicTrimDialogListener = musicTrimDialogListener
    }

    fun getMusicStart(): Int {
        return musicTrimStart
    }

    fun getMusicEnd(): Int {
        return musicTrimEnd
    }

    fun setNeedPurchase(purchase: Boolean) {
        needPurchase = purchase
    }

    fun updateTopBottomPaintColor(@ColorInt color: Int) {
        lineTopBottomPaintColor = color
    }

    fun updateFrameBgColor(@ColorInt color: Int) {
        frameBgColor = color
    }

    fun updateWaveColor(@ColorInt color: Int) {
        waveColor = color
    }

    fun updateDragResId(leftResId: Int, rightResId: Int, dragWidthDp: Int, dragHeightDp: Int) {
        leftDragResId = leftResId
        rightDragResId = rightResId
        this.dragWidthDp = dragWidthDp
        this.dragHeightDp = dragHeightDp
    }

    override fun onClick(v: View?) {
        val i = v?.id // 播放传递到碎片中去
        if (i == R.id.viewDialogMusicSettingCancel) {

            dismissMusicSettingDialog()
        } else if (i == R.id.tvDialogMusicSettingUse) { // 傻瓜自动模式 (去除无节奏点音乐)
            if (checkInputAvailable()) {
                KeyboardUtil.hideKeyboard(context, v)
                musicTrimDialogListener?.onConfirmClickListener(musicTrimStart, musicTrimEnd)
                dismissMusicSettingDialog()
            }
        } else if (i == R.id.ivDialogMusicSettingAvatar) {
            if (musicTrimDialogListener?.isPlaying() == true) {
                setPlayButtonPauseState()
            } else {
                resumePlay()
                binding?.ivDialogMusicSettingPlay!!.isSelected = true
            }
        }
    }

    private fun checkInputAvailable(): Boolean {
        binding?.apply {
            val startMin = tvDialogMusicSettingTimeStart1.getText().toString().toInt()
            val startSec = tvDialogMusicSettingTimeStart2.getText().toString().toInt()
            val startMs = tvDialogMusicSettingTimeStart3.getText().toString().toInt()

            val endMin = tvDialogMusicSettingTimeEnd1.getText().toString().toInt()
            val endSec = tvDialogMusicSettingTimeEnd2.getText().toString().toInt()
            val endMs = tvDialogMusicSettingTimeEnd3.getText().toString().toInt()

            val startTime = (startMin * 60 + startSec) * 1000 + startMs * 100
            val endTime = (endMin * 60 + endSec) * 1000 + endMs * 100
            if (startTime >= endTime) {
                Toast.makeText(context,R.string.duration_input_startgtend_errinfo, Gravity.BOTTOM)
            } else if (endTime > musicTrimDuration) {
                Toast.makeText(context,R.string.duration_input_endtimeout_errinfo, Gravity.BOTTOM)
            } else {
                musicTrimStart = startTime
                musicTrimEnd = endTime
                audioLineScrollView.moveLeftDragByTime(musicTrimStart.toLong())
                audioLineScrollView.moveRightDragByTime(musicTrimEnd.toLong())
                if (musicCurrent < musicTrimStart) {
                    musicCurrent = musicTrimStart
                    seekToPlay(true)
                }
                if (musicCurrent > musicTrimEnd) {
                    musicCurrent = musicTrimEnd
                    seekToPlay(false)
                }

                return true
            }
        }
        return false
    }

    interface MusicTrimDialogListener {
        fun onConfirmClickListener(musicStart: Int, musicEnd: Int)
        fun onScaleListener(newCount: Int, musicDuration: Int)
        fun seekTo(process: Int)
        fun stopPlay()
        fun startPlay()
        fun pausePlay()
        fun isPlaying(): Boolean
        fun getDuration(): Int
        fun getCurrentPosition(): Int
    }


}