package com.example.test.view.customwaveview

import android.view.View
import com.example.test.view.customwaveview.DragSelect

/**
 * 拖动监听
 */
interface OnSideDragListener {
    //当前x坐标及上一次oldX坐标
    fun onDrag(v: View, select: DragSelect, x: Int, rawX: Int, time: Long)
    //拖动结束
    fun onDraged(v: View, select: DragSelect, x: Int, time: Long)
}