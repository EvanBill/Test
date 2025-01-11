package com.example.test.view.customwaveview

import android.content.Context
import android.util.TypedValue

/**
 *
 * @Author: hjj
 * @CreateDate: 2021/8/16 15:47
 * @Description:
 */
object EnTool {
    fun dp2px(context: Context?, dpVal: Int): Int {
        if (context == null) {
            return 0
        }

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal.toFloat(), context.getResources().getDisplayMetrics()).toInt()
    }
}