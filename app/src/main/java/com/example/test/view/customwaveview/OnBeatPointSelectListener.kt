package com.example.test.view.customwaveview

import android.view.View

/**
 * 节拍是否选中
 */
interface OnBeatPointSelectListener {
    //视图和是否选中
    fun onSelected(v: View, isSelected: Boolean)
}