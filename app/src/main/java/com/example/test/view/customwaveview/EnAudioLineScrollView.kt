package com.example.test.view.customwaveview

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import androidx.annotation.RequiresApi
import com.example.test.view.customwaveview.EnTool
import java.util.*


class EnAudioLineScrollView : HorizontalScrollView, OnSideDragListener {

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initData(context, attrs, defStyleAttr)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initData(context, attrs, defStyleAttr)
    }

    lateinit var audioLineView: EnAudioLineView
    private var leftDragListener: OnSideDragListener? = null
    private var rightDragListener: OnSideDragListener? = null
    private var changeScaleListener: OnChangeScaleListener? = null
    private var onClickListener: OnViewClickListener? = null

    //是否手动滚动
    var isScrollingByHand: Boolean = false
        private set
    private var scrollChangeListener: OnWaveScrollChangeListener? = null

    /**
     * 绘制的有效区域
     */
    private var viewValidLeft: Int = 5
    private var viewValidTop: Int = 0
    private var viewValidRight: Int = 0
    private var viewValidBottom: Int = 0

    //屏幕宽度
    private var screenWidth = 0
    var selectPointScale = 0F
        private set

    //中间线
    var centerLineX: Float = -10F
        private set
    private var centerLinePos = -10F
    private var centerLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var centerLineWidth = 2F
    private var centerLineTop: Float = 0F
    private var centerLineBottom: Float = 0F

    //边缘检测滚动值
    private var AUTO_SCROLL_MARGIN = 38
    private var AUTO_SCROLL_STEP = 11

    //继续滚动刷新
    private var mTimer: Timer? = null
    private var isScrollable: Boolean = true

    //缩放完延迟期间
    private var isScaledDelay = false

    //缩放事件
    private var isScaling = false
    private var SCALE_DELAY_MS: Long = 300


    fun initData(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        //屏幕宽
        var displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
        } else {
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        screenWidth = displayMetrics.widthPixels

        AUTO_SCROLL_MARGIN = EnTool.dp2px(context, 38)
        AUTO_SCROLL_STEP = EnTool.dp2px(context, 11)
        centerLineWidth = EnTool.dp2px(context, 2).toFloat()
        //中间线
        centerLinePaint.setColor(Color.WHITE)
        centerLinePaint.style = Paint.Style.FILL
        centerLinePaint.setStrokeCap(Paint.Cap.ROUND)
        centerLinePaint.strokeWidth = centerLineWidth
        Log.e("dddd","-----------------父View-height:$height")
        audioLineView = EnAudioLineView(context)
        setAudioPadding()

        addView(audioLineView)
        audioLineView.leftDragListener = this
        audioLineView.rightDragListener = this
    }

    fun setAudioPadding() {
        audioLineView.post(Runnable {

            audioLineView.setPadding(
                (screenWidth / 2 - audioLineView.dragBtnWidth - centerLineWidth / 2).toInt(), 0,
                (screenWidth / 2 - audioLineView.dragBtnWidth - centerLineWidth / 2).toInt(), 0
            )
            centerLinePos = screenWidth / 2 - centerLineWidth / 2
        })

    }

    fun initArea() {
        viewValidLeft = paddingLeft
        viewValidTop = paddingTop
        viewValidRight = width - paddingRight
        viewValidBottom = height - paddingBottom
    }

    /**
     * 监听子波形图滑块滑动
     */
    fun addLeftDragListener(listener: OnSideDragListener?) {
        leftDragListener = listener
    }

    fun addRightDragListener(listener: OnSideDragListener?) {
        rightDragListener = listener
    }

    fun addChangeScaleListener(listener: OnChangeScaleListener?) {
        changeScaleListener = listener
    }

    fun addViewClickListener(listener: OnViewClickListener?) {
        onClickListener = listener
    }

    fun addFrameChangeListener(listener: OnFrameChangeListener?) {
        audioLineView.frameScaleListener = listener
    }

    fun addWaveScrollChangeListener(listener: OnWaveScrollChangeListener?) {
        scrollChangeListener = listener
    }

    fun moveLeftDragByScale(scale: Float) {
        isScrollable = false
        audioLineView.moveLeftDragByScale(scale)
    }

    fun moveLeftDragByTime(time: Long) {
        isScrollable = false
        audioLineView.moveLeftDragByTime(time)
    }

    fun moveRightDragByScale(scale: Float) {
        isScrollable = false
        audioLineView.moveRightDragByScale(scale)
    }

    fun moveRightDragByTime(time: Long) {
        isScrollable = false
        audioLineView.moveRightDragByTime(time)
    }

    /**
     * 添加音频数据
     */
    fun addFrames(frames: List<AudioFrameMeta>) {
        audioLineView.addFrames(frames)
    }

    /**
     * 添加音频数据
     */
    fun addFrames(frames: List<AudioFrameMeta>, leftDragTempTime: Long, rightDragTempTime: Long) {
        audioLineView.addFrames(frames, leftDragTempTime, rightDragTempTime)
    }

    /**
     * 更新音频数据
     */
    fun updateFrames(frames: List<AudioFrameMeta>) {
        audioLineView.updateFrames(frames)
    }

    /**
     * 设置节拍
     */
    fun setBeats(beats: SparseIntArray?) {
        audioLineView.setBeats(beats)
    }

    /**
     * 移动到指定比例位置
     */
    fun scrollToPosition(scale: Float) {
        var frameWidth = audioLineView.frameAreaRight - audioLineView.frameAreaLeft
        var frameLeft = frameWidth * scale
        if (frameLeft > 0) {
            frameLeft = frameLeft / EnAudioLineView.getFrameStep() * EnAudioLineView.getFrameStep()
        }

        Log.d(
            "scrollChanged",
            "scroll：scale:" + scale + " frameLeft:" + frameLeft + " frameWidth:" + frameWidth
        )
        smoothScrollTo(frameLeft.toInt(), 0)
        selectPointScale = scale
    }

    /**
     * 移动到指定时间点
     */
    fun scrollToTime(time: Long) {
        var space = audioLineView.getSpaceFromTime(time)
        smoothScrollTo(space, 0)
    }

    /**
     * 获取中间线所在的比例
     */
    fun getCenterLineXScale(): Float {
        return centerLineX / width
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        initArea()
        centerLineTop = paddingTop.toFloat()
        centerLineBottom = height.toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        Log.e("dddd","------------dispatchDraw-----父View-height:$height------paddingTop:${paddingTop}------paddingBottom:${paddingBottom}")
        var delta = EnTool.dp2px(context, 2)
        /**
         * 绘制中间线
         */
        canvas?.drawLine(centerLineX, centerLineTop, centerLineX, centerLineBottom, centerLinePaint)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        Log.d(
            "scrollChanged",
            " l:" + l + " oldl:" + oldl + "  left:" + left + "  right:" + right + "  width:" + width
        )

        centerLineX = centerLinePos + l
        scorlledX = l
        scorlledY = t

        audioLineView.moveSelectPoint(centerLineX.toInt())

        //滚动回调
        var time = audioLineView.getTimeFromX((centerLineX - paddingLeft).toInt())
        scrollChangeListener?.onScroll(this, l - oldl, t - oldt, isScrollingByHand, time)
    }

    private var downX1: Float = 0F
    private var downY1: Float = 0F
    private var downX2: Float = 0F
    private var downY2: Float = 0F
    private var downX: Float = 0F
    private var initDist: Float = 0F

    //滚动的相对值
    private var scorlledX: Int = 0
    private var scorlledY: Int = 0

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        //缩放后防止有拖动发生
        if (isScaledDelay) {
            return true
        }

        when (ev?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                //两指缩放
                if (ev.pointerCount == 2) {
                    initPointersDistance(ev)
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                isScrollingByHand = true
                //两指缩放
                if (ev.pointerCount == 2) {
                    if (initDist > 0) {
                        var moveDist = spacing(ev)
                        var scale = Math.abs(moveDist / initDist)
                        changeScale(scale, (initDist - moveDist).toInt(), false)
                        return true
                    } else {

                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isScrollingByHand = false

                var x = ev.x
                if (Math.abs(x - downX) < 15) {
                    //点击了
                    clickEvent(x, ev.y)
                    onClickListener?.onClicked()
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                isScrollingByHand = false
                //缩放结束了
                if (ev.pointerCount == 2) {
                    changeScale(0F, 0, true)
                    return true
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun clickEvent(x: Float, y: Float) {
        for (i in 0..childCount) {
            val view = getChildAt(i)
            if (view is OnFrameClickListener) {
                (view as OnFrameClickListener).onClicked(this, scorlledX + x, y + scorlledY)
            }
        }
    }

    fun getTime(): Long {
        val time = audioLineView.getTimeFromX((centerLineX - paddingLeft).toInt())
        return time
    }

    /**
     * 初始化两点间距离
     */
    private fun initPointersDistance(ev: MotionEvent) {
        downX1 = ev.getX(0)
        downY1 = ev.getY(0)
        downX2 = ev.getX(1)
        downY2 = ev.getX(1)
        initDist = spacing(ev)
    }

    /**
     * 计算两个点水平的距离
     *
     * @param event
     * @return
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        return Math.abs(x)
    }

    private fun changeScale(scale: Float, x: Int, isFinished: Boolean = false) {
        isScrollable = false

        if (!isFinished) {
            for (i in 0..childCount) {
                val view = getChildAt(i)
                if (view is OnChangeScaleListener) {
                    isScaledDelay = false
                    (view as OnChangeScaleListener).onScale(this, scale, x)
                    changeScaleListener?.onScale(this, scale, x)
                }
            }
        } else {
            isScaledDelay = true
            for (i in 0..childCount) {
                val view = getChildAt(i)
                if (view is OnChangeScaleListener) {
                    (view as OnChangeScaleListener).onScaled(this)
                    changeScaleListener?.onScaled(this)
                }
            }

            postDelayed({
                isScaledDelay = false
            }, SCALE_DELAY_MS)
        }
    }

    override fun onDrag(v: View, select: DragSelect, x: Int, rawX: Int, time: Long) {
        Log.d("drag_scroll", "x: " + x + "  rawX: " + rawX)

        when (select) {
            DragSelect.LEFT -> {
                if (rawX <= AUTO_SCROLL_MARGIN) {
                    startScroll(DragSelect.LEFT, -AUTO_SCROLL_STEP)
                } else if (rawX >= width - AUTO_SCROLL_MARGIN) {
                    startScroll(DragSelect.LEFT, AUTO_SCROLL_STEP)
                } else {
                    stopScroll()
                }
                leftDragListener?.onDrag(v, select, x, rawX, time)
            }

            DragSelect.RIGHT -> {
                if (rawX <= AUTO_SCROLL_MARGIN) {
                    startScroll(DragSelect.RIGHT, -AUTO_SCROLL_STEP)
                } else if (rawX >= width - AUTO_SCROLL_MARGIN) {
                    startScroll(DragSelect.RIGHT, AUTO_SCROLL_STEP)
                } else {
                    stopScroll()
                }
                rightDragListener?.onDrag(v, select, x, rawX, time)
            }

            else -> {

            }
        }

    }

    override fun onDraged(v: View, select: DragSelect, x: Int, time: Long) {
        stopScroll()

        when (select) {
            DragSelect.LEFT -> {
                leftDragListener?.onDraged(v, select, x, time)
            }

            DragSelect.RIGHT -> {
                rightDragListener?.onDraged(v, select, x, time)
            }

            else -> {

            }
        }
    }

    /**
     * 滚动刷新
     */
    private fun startScroll(select: DragSelect, scrollStep: Int) {
        if (isScrollable && mTimer == null) {
            if (mTimer == null) {
                mTimer = Timer()
            }
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    post {
                        smoothScrollBy(scrollStep, 0)
                        if (select == DragSelect.LEFT) {
                            audioLineView.moveLeftDragByDx(scrollStep)
                        } else if (select == DragSelect.RIGHT) {
                            audioLineView.moveRightDragByDx(scrollStep)
                        }
                    }
                }

            }, 100L, 100L)
        }
    }

    private fun stopScroll() {
        isScrollable = true
        mTimer?.cancel()
        mTimer = null
    }

    /**
     * 重置数据
     */
    fun resetData() {
        centerLineX = -10F
        audioLineView.resetFrames()
        isScaledDelay = false

        stopScroll()
    }

}