package com.example.test.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment : DialogFragment() {

    var TAG = javaClass.simpleName



    open fun isFullScreen() = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialog = dialog
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onStart() {
        super.onStart()
        if (isFullScreen()) {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } else {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    override fun show(
        manager: FragmentManager,
        tag: String?
    ) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commitAllowingStateLoss()
            manager.beginTransaction().add(this, tag).commitAllowingStateLoss()
            // super.show(manager, tag);
        } catch (e: Throwable) {
            //同一实例使用不同的tag会异常,这里捕获一下
            e.printStackTrace()
        }
    }

    open fun show(manager: FragmentManager) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commitAllowingStateLoss()
            manager.beginTransaction().add(this, TAG).commitAllowingStateLoss()
            //super.show(manager, TAG);
        } catch (e: Throwable) {
            //同一实例使用不同的tag会异常,这里捕获一下
            e.printStackTrace()
        }
    }
    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    protected abstract fun initView(rootView: View)

    protected abstract fun layoutId(): Int


}