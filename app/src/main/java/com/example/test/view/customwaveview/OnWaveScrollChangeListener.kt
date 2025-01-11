package com.example.test.view.customwaveview

import android.view.View

/**
 *
 * @Author: hjj
 * @CreateDate: 2021/8/16 15:49
 * @Description:
 */
interface OnWaveScrollChangeListener {
    //手动滚动
    fun onScroll(view: View?, dx: Int, dy: Int, byHand: Boolean, centerTime: Long)
}