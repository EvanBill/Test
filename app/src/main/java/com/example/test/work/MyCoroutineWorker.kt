package com.example.test.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import timber.log.Timber

class MyCoroutineWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        Timber.e("doWork-----Thread${Thread.currentThread().name}")
        Log.e("ddd","doWork-----Thread${Thread.currentThread().name}")
        return Result.success()
    }
}