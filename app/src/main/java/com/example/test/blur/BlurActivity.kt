package com.example.test.blur

import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.namespace.databinding.ActivityBlurBinding
import com.example.namespace.databinding.ActivityMainBinding
import com.example.test.work.WorkManagerUtil
import timber.log.Timber

class BlurActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBlurBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}