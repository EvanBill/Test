package com.example.test.view.customwaveview

import android.view.View

/**
 * 点击监听
 */
internal interface OnFrameClickListener {
    //视图和是否选中
    fun onClicked(v: View, x: Float, y: Float): Boolean
}