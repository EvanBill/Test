package com.example.test.view.customwaveview

import android.view.View

/**
 *
 * @Author: hjj
 * @CreateDate: 2021/8/16 15:49
 * @Description:
 */
interface OnFrameChangeListener {
    /**
     * newCount：新的缩放后的时间片段总数值
     * scale：缩放比例
     * x：移动量
     */
    fun onScale(v: View?, scale: Float, x: Int, newCount: Int)
    fun onScaled(v: View?)
}