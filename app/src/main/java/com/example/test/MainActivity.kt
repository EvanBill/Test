package com.example.test

import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.namespace.BuildConfig
import com.example.namespace.R
import com.example.namespace.databinding.ActivityMainBinding
import com.example.test.work.AiWorkFunType
import com.example.test.work.WorkManagerUtil
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnWork.setOnClickListener {

            Timber.tag("fff").e("--主Thread${Looper.getMainLooper().thread.name}")
            val params = mutableMapOf<String, Any>()

            params["funType"] = AiWorkFunType.TEXT_TO_VIDEO_NEW
            params["params"] = "123456"

            WorkManagerUtil.startWork(this,params)
        }


    }
}