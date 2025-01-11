package com.example.test.view.customwaveview

import android.view.View

/**
 * 点击监听
 */
interface OnFrameSelectListener {
    //视图和是否选中
    fun onSelect(v: View, index: Int, time: Long): Boolean
}