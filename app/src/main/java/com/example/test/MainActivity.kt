package com.example.test

import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.namespace.BuildConfig
import com.example.namespace.R
import com.example.namespace.databinding.ActivityMainBinding
import com.example.test.work.WorkManagerUtil
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnWork.setOnClickListener {

            Timber.tag("fff").e("--主Thread${Looper.getMainLooper().thread.name}")
            WorkManagerUtil.startWork(this)
        }

    }
}