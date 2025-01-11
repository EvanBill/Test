package com.example.test.view.customwaveview

import android.view.View

/**
 *
 * @Author: hjj
 * @CreateDate: 2021/8/16 15:48
 * @Description:
 */
interface OnChangeSizeListener {
    fun onScale(v: View?, scale: Float, x: Int)
    fun onScaled(v: View?)
}