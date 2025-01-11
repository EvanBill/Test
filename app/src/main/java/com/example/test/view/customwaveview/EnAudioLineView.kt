package com.example.test.view.customwaveview

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import com.example.namespace.R
import com.example.test.view.customwaveview.EnTool


import java.util.*

/**
 * 声纹显示轴
 */
class EnAudioLineView : View, OnChangeScaleListener, OnFrameClickListener {

    //每帧数据对应的时间
    private var timerPerFrame: Long = 100
    private var timerPerFrameScale: Long = timerPerFrame

    //步幅
    var widthPerTime: Int = 4
        set(value) {
            if (value <= 0) {
                field = 1
            } else {
                field = value
            }

            //最大5倍速度
            if (widthPerTime > 5 * (metaWidth + sperateWidth)) {
                field = 5 * (metaWidth + sperateWidth)
            }
        }

    //步幅
    private var moveXAdjust: Float = 0F

    /**
     * 样式选择
     */
    //样式
    var displayStyle: Style = Style.RECT

    //绘制重心点
    var styleGravity: StyleGravity = StyleGravity.BOTTOM

    //绘制单元间隔默认宽度，此为像素值
    private val SPERATE_WIDTH = 2

    //绘制单元默认宽度
    private val META_WIDTH = 2


    //绘制选择点的线的宽度
    private val FRAME_AREA_HEIGHT = 30


    //拖块扫过区域圆角半径宽度
    private val DRAG_OUT_SIDE_RADIUS = 5

    //裁剪区域边框圆角半径宽度
    private val TRIM_AREA_BOARD_RADIUS = 8

    //最少波形帧数
    private val MIN_FRAME_SIZE = 40

    /**
     * 选择线
     */
    //当前点的x坐标
    var selectPointX = -1

    /**
     * 音频数据
     */
    //音频帧：间隔0.1毫秒的帧数据
    private var audioFrames = mutableListOf<AudioFrameMeta>()

    //节拍时间点
    private var beatsTimes: SparseIntArray? = null

    //原始时间个数
    private var originalCount = 0

    //缩放后时间实时个数
    private var currentCount = 0

    //开始时间
    private var originalStartTime: Long = 0

    //显示区域左侧音频时间戳，时间步幅
    private var leftTime: Long = -1

    //显示区域右侧音频时间戳
    private var rightTime: Long = -1

    //最初的组件宽度
    private var initFrameWidth = 0

    /**
     * 波形数据
     */
    var frameAreaLeft = 0
        private set
    var frameAreaRight = 0
        private set
    private var frameAreaTop = 0
    private var frameAreaBottom = 0


    //波形区域高度
    private var frameAreaHeight = 0
    private var frameMarginTop = 0

    //最高振幅值
    private var maxFrameHeight = 0

    //最高振幅占高度比
    var maxFrameHeightScale = 0.9

    //振幅相对自身缩放比例
    private var frameHeightScale = 1F

    //波形空心边框颜色
    var borderColor: Int = Color.parseColor("#D8C8FF")
        set(value) {
            field = value
            initFramePaint()
        }

    //波形实心填充颜色
    var innerColor: Int = Color.parseColor("#D8C8FF")
        set(value) {
            field = value
            initFramePaint()
        }

    //画帧的画刷
    private var framePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //背景区域颜色
    var frameBgColor: Int = Color.parseColor("#39393F")
        set(value) {
            field = value
            initFrameBgPaint()
        }
    private var frameBgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var frameSelectListener: OnFrameSelectListener? = null

    /**
     * 缩放相关
     */
    var frameScaleMax: Float = 1F
    var frameScaleMin: Float = 0.01F

    //实时缩放比率
    private var frameScaleReal: Float = 1.0F
    var frameScaleListener: OnFrameChangeListener? = null

    //缩放标准X线
    private var scaleStandardX = 0

    //缩放系数
    private var scaleFactor: Float = 1F

    var viewDesireWidth: Int = 0

    //播放状态
    var playState: PlayState = PlayState.UNSPECIFIED

    /**
     * 滑块相关
     */
    //是否显示滑块
    var showDragBtn = true
        set(value) {
            if (value) {
                loadDragBitmap(false)
            } else {
                realeaseDragBitmap()
            }
            field = value
        }

    //按钮的画笔
    private var dragBtnPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //底部和顶部的两条线的画笔
    private val lineTopBottomPaint: Paint by lazy {
        var temp = Paint(Paint.ANTI_ALIAS_FLAG)
        temp.color = Color.parseColor("#9265FF")
        temp.style = Paint.Style.STROKE
        temp.strokeWidth = 4f
        temp
    }

    //滑块尺寸
    var dragBtnWidth: Int = 0
    var dragBtnHeight: Int = 0
        private set
    private var dragOutSidePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //    var dragOutSideColor = Color.parseColor("#88000000")
    var dragOutSideColor = Color.parseColor("#8839393F")
        set(value) {
            field = value
            initDragOutSizePaint()
        }
    var dragOutSideRadius = 2
    private var trimAreaBoardRadius = 8f

    //左滑块矩形区域
    private var leftDragRect: Rect = Rect()
    var leftDragResId = R.drawable.btn_wave_timeline_left
    private var leftDragBitmap: Bitmap? = null
    var leftDragListener: OnSideDragListener? = null

    //左滑块左侧相对左侧距离
    private var leftDragTime: Long = 0

    //右滑块矩形区域
    private var rightDragRect: Rect = Rect()
    private var rightDragBitmap: Bitmap? = null
    var rightDragResId = R.drawable.btn_wave_timeline_right
    var rightDragListener: OnSideDragListener? = null

    //右滑块右侧相对右边界距离
    private var rightDragTime: Long = 0

    private var screenWidth = 0

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
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initData(context, attrs, defStyleAttr)
    }

    fun updateTopBottomPaintColor(@ColorInt color: Int) {
        lineTopBottomPaint.color = color
    }

    fun updateFrameBgColor(@ColorInt color: Int) {
        frameBgColor = color
        initFramePaint()
    }

    fun updateDragResId(leftResId: Int, rightResId: Int, dragWidthDp: Int, dragHeightDp: Int) {
        leftDragResId = leftResId
        rightDragResId = rightResId
        loadDragBitmap(true, dragWidthDp, dragHeightDp)
    }

    fun updateFramePaintColor(@ColorInt color: Int) {
        innerColor = color
        borderColor = color
        initFramePaint()
    }

    fun initData(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        var arr: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.EnAudioLineView, defStyleAttr, 0)

        arr.recycle()

        sperateWidth = SPERATE_WIDTH
        metaWidth = EnTool.dp2px(context, META_WIDTH)

        frameAreaHeight = EnTool.dp2px(context, FRAME_AREA_HEIGHT)

        dragOutSideRadius = EnTool.dp2px(context, DRAG_OUT_SIDE_RADIUS)


        trimAreaBoardRadius = EnTool.dp2px(context, TRIM_AREA_BOARD_RADIUS).toFloat()

//        initHeight()
        initFramePaint()
        initFrameBgPaint()

        initDragBtnPaint()
        initDragOutSizePaint()

        loadDragBitmap(true)

        var displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
        } else {
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        screenWidth = displayMetrics.widthPixels
    }

    companion object {
        //绘制单元的间隔
        var sperateWidth = 0

        //绘制单元的宽度
        var metaWidth = 0

        fun getFrameStep(): Int {
            return sperateWidth + metaWidth
        }
    }

    private fun initHeight() {
        val h = height - paddingTop - paddingBottom
        frameAreaHeight = h
        frameAreaTop = paddingTop
        frameAreaBottom = frameAreaTop + frameAreaHeight
        scaleFrameAreaWidth()
        frameMarginTop = frameAreaTop


    }

    private fun scaleFrameAreaWidth() {
        //波形区域外框整体的边界
        frameAreaLeft = paddingLeft + dragBtnWidth
        frameAreaRight = width - dragBtnWidth - paddingRight
    }

    /**
     * 初始化帧画刷
     */
    private fun initFramePaint() {
        if (borderColor == innerColor) {
            framePaint.style = Paint.Style.FILL
            framePaint.color = innerColor
        } else {
            framePaint.style = Paint.Style.STROKE
            framePaint.strokeWidth = 3F
            framePaint.color = borderColor
        }
    }

    private fun initFrameBgPaint() {
        frameBgPaint.style = Paint.Style.FILL
        frameBgPaint.color = frameBgColor
    }

    private fun initDragBtnPaint() {

        //要关闭软件绘制，否则在数据量大时，会出现：kotlin  not displayed because it is too large to fit into a software layer (or drawing cache)
//        setLayerType(LAYER_TYPE_SOFTWARE,dragBtnPaint)
    }

    private fun initDragOutSizePaint() {
        dragOutSidePaint.style = Paint.Style.FILL
        dragOutSidePaint.color = dragOutSideColor
    }

    private fun loadDragBitmap(isForce: Boolean, dragWidthDp: Int = 25, dragHeightDp: Int = 45) {
        if (!(!showDragBtn || isForce)) {
            return
        }

        dragBtnWidth = EnTool.dp2px(context, dragWidthDp)
        dragBtnHeight = EnTool.dp2px(context, dragHeightDp)

        leftDragBitmap = BitmapFactory.decodeResource(resources, leftDragResId)
        rightDragBitmap = BitmapFactory.decodeResource(resources, rightDragResId)

        initHeight()
        invalidate()
    }

    private fun realeaseDragBitmap() {
        if (!showDragBtn) {
            return
        }
        leftDragBitmap?.recycle()
        rightDragBitmap?.recycle()
        dragBtnWidth = 0

        initHeight()
        invalidate()
    }

    /**
     * 设置滑块
     */
    private fun updateDragPosition() {
        val leftDragRight = getSpaceFromTime(leftDragTime) + frameAreaLeft

        //top和bottom加减2像素是为了给边线留出空间
        leftDragRect =
            Rect(
                leftDragRight - dragBtnWidth,
                frameAreaTop + (frameAreaHeight - dragBtnHeight).shr(1),
                leftDragRight,
                frameAreaTop + (frameAreaHeight + dragBtnHeight).shr(1)
            )
        val rightDragLeft = getSpaceFromTime(rightDragTime, true) + frameAreaLeft
        rightDragRect =
            Rect(
                rightDragLeft,
                frameAreaTop + (frameAreaHeight - dragBtnHeight).shr(1),
                rightDragLeft + dragBtnWidth,
                frameAreaTop + (frameAreaHeight + dragBtnHeight).shr(1)
            )
    }

    fun addFrames(frames: List<AudioFrameMeta>) {
        addFrames(frames, 0L, 0L)
    }

    /**
     * 添加音频数据
     */
    fun addFrames(frames: List<AudioFrameMeta>, leftDragTempTime: Long, rightDragTempTime: Long) {
        //提取新加的最大振幅
        var audioFrameMeta = frames.maxByOrNull { it.originalHeight } ?: return

        var tmpFrames = frames.toList()
        post {
            initHeight()

            synchronized(audioFrames) {
                //如果最新振幅比原有大，重置所有振幅值，否则只重置
                if (audioFrameMeta.originalHeight > maxFrameHeight) {
                    audioFrames.addAll(tmpFrames)

                    frameHeightScale =
                        (frameAreaHeight * maxFrameHeightScale / audioFrameMeta.originalHeight).toFloat()

                    audioFrames.map {
                        it.height = (it.originalHeight * frameHeightScale).toInt()
                    }
                } else {
                    tmpFrames.map {
                        it.height = (it.originalHeight * frameHeightScale).toInt()
                    }
                    audioFrames.addAll(tmpFrames)
                }
            }
            if (leftTime < 0) {
                leftTime = audioFrames[0].time
                originalStartTime = leftTime
            }

            rightTime = audioFrames.last().time

            leftDragTime = if (leftDragTempTime == 0L) leftTime else leftDragTempTime
            rightDragTime = if (rightDragTempTime == 0L) rightTime else rightDragTempTime

            //设置节拍
            setBeats(beatsTimes)

            originalCount = audioFrames.size
            currentCount = audioFrames.size
            initFrameWidth = currentCount * (sperateWidth + metaWidth)

            //根据音频时间长度调整缩放系数
            scaleFactor = Math.min(Math.max(audioFrames.size / 600 * 0.1F, 0.1F), 1F)
            requestLayout()
        }

    }

    /**
     * 更新音频数据
     */
    fun updateFrames(frames: List<AudioFrameMeta>) {
        //提取新加的最大振幅
        var audioFrameMeta = frames.maxByOrNull { it.originalHeight }
        if (audioFrameMeta == null) {
            return
        }
        frames.map {
            it.height = (it.originalHeight * frameHeightScale).toInt()
        }

        audioFrames.clear()
        audioFrames.addAll(frames)

        setBeats(beatsTimes)
        requestLayout()
        invalidate()
    }

    /**
     * 设置音频节拍
     */
    fun setBeats(beats: SparseIntArray?) {
        if (beats == null || beats.size() == 0) {
            return
        }

        //如果还没有音频波形数据，就暂时缓存一下
        if (beatsTimes == null || beatsTimes?.size() == 0) {
            beatsTimes = beats
        }

        if (audioFrames != null && audioFrames.isNotEmpty()) {
            synchronized(audioFrames) {

                var startIndex = 0
                for (i in 0 until beats.size()) {
                    var timePoint = beats[i]

                    //设置节拍点
                    for (f in startIndex until audioFrames.size - 1) {
                        if (audioFrames[f].time <= timePoint && audioFrames[f + 1].time > timePoint) {
                            audioFrames[f].isBeatPoint = true
                            startIndex = f
                            break
                        }
                    }
                }

                if (audioFrames[audioFrames.size - 1].time <= beats[beats.size() - 1]) {
                    audioFrames[audioFrames.size - 1].isBeatPoint = true
                }
            }
        }
    }

    /**
     * 重置数据
     */
    fun resetFrames() {
        audioFrames.clear()
        leftDragRect.setEmpty()
        rightDragRect.setEmpty()
        frameScaleReal = 1.0F
        leftDragTime = 0
        rightDragTime = 0
        selectPointX = -1
        frameScaleMin = 0.01F
        frameScaleMax = 1F
        scaleStandardX = 0
        frameScaleReal = 1.0F
        beatsTimes?.clear()
        beatsTimes = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //根据绘制帧数计算宽度
        viewDesireWidth = audioFrames.size * (sperateWidth + metaWidth)
        setPadding((screenWidth / 2 - dragBtnWidth - 1), 0, (screenWidth / 2 - dragBtnWidth - 1), 0)
        var widthSize = resolveSizeAndState(
            viewDesireWidth + paddingLeft + paddingRight + dragBtnWidth.shl(1),
            widthMeasureSpec,
            0
        )
        setMeasuredDimension(widthSize, getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updateDragPosition()
        scaleFrameAreaWidth()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (audioFrames.size == 0 || canvas == null) {
            return
        }

        //波形区域背景
        drawFrameBg(canvas)

        //可展示帧数
        val startIndex = getIndexFromTime(leftTime)
        var frames = Math.min(
            (frameAreaRight - frameAreaLeft) / (sperateWidth + metaWidth),
            audioFrames.size - startIndex
        )

        var xAdjust = (dragBtnWidth + paddingLeft - moveXAdjust).toInt()
        //循环绘制
        if (displayStyle == Style.RECT) {
            drawRect(canvas, frames, startIndex, xAdjust)
        } else {
            drawContinue(canvas, frames, startIndex, xAdjust)
        }
        //绘制拖动两侧前景色
        drawDragOutSideFg(canvas)
        if (showDragBtn && playState != PlayState.PLAY) {
            //绘制两边拖动块
            drawDragBtn(canvas)
        }
        drawTrimAreaBorder(canvas)

    }

    private fun drawFrameBg(canvas: Canvas) {
        canvas.drawRect(
            Rect(frameAreaLeft, frameAreaTop, frameAreaRight, frameAreaBottom),
            frameBgPaint
        )
    }

    /**
     * 矩形框显示
     */
    private fun drawRect(canvas: Canvas, frames: Int, startIndex: Int, xAdjust: Int) {
        var x = 0
        when (styleGravity) {
            StyleGravity.BOTTOM -> {
                var frameHeight = frameMarginTop + frameAreaHeight
                for (index in 0 until frames) {
                    var item = audioFrames[startIndex + index]
                    canvas.drawRect(
                        Rect(
                            x + xAdjust,
                            frameHeight - item.height,
                            x + xAdjust + metaWidth,
                            frameHeight
                        ), framePaint
                    )
                    x += metaWidth + sperateWidth
                }
            }

            StyleGravity.CENTER -> {
                for (index in 0 until frames) {
                    var item = audioFrames[startIndex + index]
                    canvas.drawRect(
                        Rect(
                            x + xAdjust,
                            frameMarginTop + (frameAreaHeight - item.height).shr(1),
                            x + xAdjust + metaWidth,
                            frameMarginTop + (frameAreaHeight + item.height).shr(1)
                        ), framePaint
                    )
                    x += metaWidth + sperateWidth
                }
            }

            StyleGravity.TOP -> {
                for (index in 0 until frames) {
                    var item = audioFrames[startIndex + index]
                    canvas.drawRect(
                        Rect(
                            x + xAdjust,
                            frameMarginTop,
                            x + xAdjust + metaWidth,
                            frameMarginTop + item.height
                        ), framePaint
                    )
                    x += metaWidth + sperateWidth
                }
            }
        }
    }

    /**
     * 波形图显示
     */
    private fun drawContinue(canvas: Canvas, frames: Int, startIndex: Int, xAdjust: Int) {
        var x = 0F
        var linePath = Path()
        when (styleGravity) {
            StyleGravity.BOTTOM -> {
                var frameHeight = frameMarginTop + frameAreaHeight
                linePath.moveTo(xAdjust.toFloat(), frameHeight.toFloat())
                for (index in 0 until frames) {
                    var item = audioFrames[startIndex + index]
                    linePath.lineTo(x + xAdjust, (frameHeight - item.height).toFloat())
                    x += metaWidth + sperateWidth
                }
                linePath.lineTo(x + xAdjust, frameHeight.toFloat())
                linePath.close()
            }

            StyleGravity.CENTER -> {
                linePath.moveTo(
                    xAdjust.toFloat(),
                    frameMarginTop + frameAreaHeight.shr(1).toFloat()
                )
                for (index in 0 until frames) {
                    var item = audioFrames[startIndex + index]
                    linePath.lineTo(
                        x + xAdjust,
                        frameMarginTop + (frameAreaHeight - item.height).shr(1).toFloat()
                    )
                    x += metaWidth + sperateWidth
                }

                linePath.lineTo(x + xAdjust, frameMarginTop + frameAreaHeight.shr(1).toFloat())
                linePath.close()

                //另一半翻转
                var opLinePath = Path()
                var matrix = Matrix()
                matrix.setScale(1F, -1F)
                //-1是防止因为计算误差导致中间出现一条空隙
                matrix.postTranslate(0F, 2 * frameMarginTop + frameAreaHeight.toFloat() - 1)
                opLinePath.addPath(linePath, matrix)
                canvas.drawPath(opLinePath, framePaint)
            }

            StyleGravity.TOP -> {
                linePath.moveTo(xAdjust.toFloat(), frameMarginTop.toFloat())
                for (index in 0 until frames) {
                    var item = audioFrames[startIndex + index]
                    linePath.lineTo(x + xAdjust, (frameMarginTop + item.height).toFloat())
                    x += metaWidth + sperateWidth
                }
                linePath.lineTo(x + xAdjust, frameMarginTop.toFloat())
                linePath.close()
            }
        }
        canvas.drawPath(linePath, framePaint)
    }


    /**
     * 绘制滑块
     */
    private fun drawDragBtn(canvas: Canvas) {
        if (leftDragBitmap != null) {
            canvas.drawBitmap(leftDragBitmap!!, null, leftDragRect, dragBtnPaint)
        }
        if (rightDragBitmap != null) {
            canvas.drawBitmap(rightDragBitmap!!, null, rightDragRect, dragBtnPaint)
        }

    }

    /**
     * 绘制裁剪区域边框
     */
    private fun drawTrimAreaBorder(canvas: Canvas) {
        canvas.drawRoundRect(
            leftDragRect.right.toFloat(),
            frameAreaTop.toFloat() + 4,
            rightDragRect.left.toFloat(),
            frameAreaBottom.toFloat() - 4,
            trimAreaBoardRadius,
            trimAreaBoardRadius,
            lineTopBottomPaint
        )
    }

    /**
     *
     */
    private fun drawDragOutSideFg(canvas: Canvas) {
        //绘制左侧层
        if (leftDragBitmap != null && leftDragRect.right > dragBtnWidth) {
            var rect = RectF(
                frameAreaLeft.toFloat(),
                frameAreaTop.toFloat(),
                leftDragRect.right.toFloat(),
                frameAreaBottom.toFloat()
            )
//            canvas.drawRoundRect(rect, dragOutSideRadius.toFloat(), dragOutSideRadius.toFloat(), dragOutSidePaint)
            canvas.drawRect(rect, dragOutSidePaint)
        }
        if (rightDragBitmap != null && rightDragRect.right < width - dragBtnWidth) {
            var rect = RectF(
                rightDragRect.left.toFloat(),
                frameAreaTop.toFloat(),
                frameAreaRight.toFloat(),
                frameAreaBottom.toFloat()
            )
//            canvas.drawRoundRect(rect, dragOutSideRadius.toFloat(), dragOutSideRadius.toFloat(), dragOutSidePaint)
            canvas.drawRect(rect, dragOutSidePaint)
        }
    }

    private var downX: Int = 0
    private var downY: Int = 0
    private var innerXAdjust = 0
    private var dragSelect: DragSelect = DragSelect.NONE

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("onTouchEvent", "action:  " + event!!.action)
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x.toInt()
                downY = event.y.toInt()

                //点击的是哪个拖块
                if (leftDragRect.contains(downX, downY)) {
                    dragSelect = DragSelect.LEFT
                    innerXAdjust = downX - leftDragRect.left
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else if (rightDragRect.contains(downX, downY)) {
                    dragSelect = DragSelect.RIGHT
                    innerXAdjust = downX - rightDragRect.left
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else {
                    dragSelect = DragSelect.NONE
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragSelect != DragSelect.NONE) {
                    dragEvent(event.x.toInt(), event.y.toInt(), event.rawX.toInt(), downX)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                var x = event.x
                if (dragSelect != DragSelect.NONE) {
                    dragEnd(dragSelect, x.toInt())
                }
                if (Math.abs(x - downX) < 15) {
                    //点击了
                    clickEvent(x.toInt(), event.y.toInt())
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun clickEvent(x: Int, y: Int) {

        var validFrameRect =
            Rect(leftDragRect.right, frameAreaTop, rightDragRect.left, frameAreaBottom)
        if (validFrameRect.contains(x, y)) {
            moveSelectPoint(x)
        }
    }

    private fun dragEvent(x: Int, y: Int, rawX: Int, oldX: Int) {
        when (dragSelect) {
            DragSelect.LEFT -> {
                var leftDragL = x - innerXAdjust
                moveLeftDrag(leftDragL)
                leftDragListener?.onDrag(this, dragSelect, x, rawX, leftDragTime)
            }

            DragSelect.RIGHT -> {
                var rightDragL = x - innerXAdjust
                moveRightDrag(rightDragL)
                rightDragListener?.onDrag(this, dragSelect, x, rawX, rightDragTime)
            }

            else -> {


            }
        }
    }

    private fun dragEnd(sel: DragSelect, x: Int) {
        when (dragSelect) {
            DragSelect.LEFT -> {
                leftDragListener?.onDraged(this, dragSelect, x, getTimeFromX(leftDragRect.right))
            }

            DragSelect.RIGHT -> {
                rightDragListener?.onDraged(this, dragSelect, x, getTimeFromX(rightDragRect.left))
            }

            else -> {


            }
        }
        dragSelect = DragSelect.NONE
    }

    fun getLeftDragTime(): Long {
        return leftDragTime
    }

    fun moveLeftDragByScale(scale: Float) {
        dragSelect = DragSelect.LEFT
        var x = (width - dragBtnWidth.shl(1) - paddingLeft - paddingRight) * scale + frameAreaLeft
        //相当于点击右侧边缘
        innerXAdjust = dragBtnWidth
        dragEvent(x.toInt(), 0, 0, 0)
    }

    private fun moveLeftDrag(leftX: Int) {
        var leftDragL = leftX
        if (leftDragL < paddingLeft) {
            leftDragL = paddingLeft
        } else {
            //最右侧限制
            if (leftDragL + dragBtnWidth > rightDragRect.left) {
                leftDragL = rightDragRect.left - dragBtnWidth
            }
        }

        leftDragRect.right = (leftDragL + dragBtnWidth) / getFrameStep() * getFrameStep()
        leftDragRect.left = leftDragRect.right - dragBtnWidth

        leftDragTime = getTimeFromX(leftDragRect.right)
        invalidate()
    }

    fun moveLeftDragByDx(dx: Int) {
        var leftDragL = leftDragRect.left + dx
        moveLeftDrag(leftDragL)
    }

    fun moveLeftDragByTime(time: Long) {
        leftDragTime = time
        updateDragPosition()
        invalidate()
    }

    fun getRightDragTime(): Long {
        return rightDragTime
    }

    fun updatePlayState(playState: PlayState) {
        this.playState = playState
        invalidate()
    }

    fun moveRightDragByScale(scale: Float) {
        dragSelect = DragSelect.RIGHT
        var x = (width - dragBtnWidth.shl(1) - paddingLeft - paddingRight) * scale + frameAreaLeft
        //相当于点击左侧边缘
        innerXAdjust = 0
        dragEvent(x.toInt(), 0, 0, 0)
    }

    fun moveRightDragByDx(dx: Int) {
        var rightDragL = rightDragRect.left + dx
        moveRightDrag(rightDragL)
    }

    private fun moveRightDrag(rightX: Int) {
        var rightDragL = rightX
        //最右不能超过右边界
        if (rightDragL > width - paddingRight - dragBtnWidth) {
            rightDragL = width - paddingRight - dragBtnWidth
        } else {
            //最左不能超过左滑块右侧
            if (rightDragL < leftDragRect.right) {
                rightDragL = leftDragRect.right
            }
        }
        rightDragRect.left = rightDragL / getFrameStep() * getFrameStep()
        rightDragRect.right = rightDragRect.left + dragBtnWidth
        rightDragTime = getTimeFromX(rightDragRect.left)
        invalidate()
    }

    fun moveRightDragByTime(time: Long) {
        rightDragTime = time
        updateDragPosition()
        invalidate()
    }

    /**
     * 时间戳转化为对应索引点
     */
    fun getIndexFromTime(time: Long): Int {
        if (time == 0L) {
            return 0
        }

        var minTime = Math.min(time, rightTime)
        return ((minTime - originalStartTime) / timerPerFrame).toInt()
    }

    /**
     * 从起始位置获取索引值
     */
    fun getIndexFromStartTime(time: Long): Int {
        if (audioFrames.isEmpty()) {
            return 0
        }

        for (index in 0 until audioFrames.size - 1) {
            var item = audioFrames[index]
            var nextItem = audioFrames[index + 1]
            if (time >= item.time && time < nextItem.time) {
                return index
            }
        }

        //最后一个位置
        if (time >= audioFrames.last().time) {
            return audioFrames.size - 1
        }

        return 0
    }

    /**
     * 计算宽度值
     * isRight:  如果是右边的话，需要index+1个
     */
    fun getSpaceFromTime(time: Long, isRight: Boolean = false): Int {
        var space = getIndexFromStartTime(time) * (metaWidth + sperateWidth)
        return if (isRight) space + metaWidth else space
    }

    /**
     * 移动选择线
     */
    fun moveSelectPoint(x: Int) {
        selectPointX = x
        invalidate()
    }

    fun updateTime(left: Long) {
        leftTime = left
        invalidate()
    }


    /**
     * 将坐标转化为时间戳
     */
    fun getIndexFromX(x: Int): Int {
        var index = ((x - frameAreaLeft).toFloat() / (sperateWidth + metaWidth)).toInt()
        if (index < 0) {
            return 0
        } else if (index >= audioFrames.size) {
            return audioFrames.size - 1
        } else {
            return index
        }
    }

    fun getTimeFromX(x: Int): Long {
        var index = getIndexFromX(x)
        if (index >= 0 && index < audioFrames.size) {
            return audioFrames[index].time
        }

        return 0
    }

    /**
     * 缩放
     */
    override fun onScale(v: View?, scale: Float, dx: Int) {
        val minWidth = metaWidth + sperateWidth
        //滑动距离太小不处理
        var adjustDx = (dx * scaleFactor).toInt()
        val dxValue: Int = Math.abs(adjustDx) / minWidth
        if (dxValue < 1) {
            return
        }

        //新的总数
        currentCount -= adjustDx / minWidth
        //最大值
        if (currentCount > originalCount) {
            currentCount = originalCount
        } else if (currentCount < MIN_FRAME_SIZE) {   //最小值
            currentCount = MIN_FRAME_SIZE
        }

        frameScaleReal = currentCount / originalCount.toFloat()
        frameScaleListener?.onScale(this, frameScaleReal, dx, currentCount)

        requestLayout()
        invalidate()
    }


    override fun onScaled(v: View?) {
        Log.d("scale", "changeScale:  real scale:" + frameScaleReal + "  size: " + audioFrames.size)
        frameScaleListener?.onScaled(this)
    }

    override fun onClicked(v: View, x: Float, y: Float): Boolean {
        Log.d("scale", "x:  " + x + "  y:  " + y)
        var validFrameRect =
            Rect(leftDragRect.right, frameAreaTop, rightDragRect.left, frameAreaBottom)
        if (validFrameRect.contains(x.toInt(), y.toInt())) {
            moveSelectPoint(x.toInt())
        }
        return true
    }


}