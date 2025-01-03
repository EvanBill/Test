package com.example.test.work

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import timber.log.Timber

object WorkManagerUtil {
    fun startWork(context: Context) {
        // 定义任务输入数据（可选）
        val inputData = Data.Builder()
            .putString("input_key", "Start Background Task")
            .build()

        // 配置任务
        val workRequest = OneTimeWorkRequestBuilder<MyCoroutineWorker>()
            .setInputData(inputData) // 设置输入数据
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED) // 网络条件
//                    .build()
//            )
            .build()
        Timber.e("fff","doWork---enqueue--主Thread${Looper.getMainLooper().thread.name}")
        Log.e("ddd","doWork---enqueue--主Thread${Looper.getMainLooper().thread.name}")

        Timber.e("doWork---enqueue--Thread${Thread.currentThread().name}")
        Log.e("ddd","doWork---enqueue--Thread${Thread.currentThread().name}")
        // 启动任务
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}