package com.example.test.work

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import timber.log.Timber
import java.util.UUID

object WorkManagerUtil {
    /**
     * 正在处理的worker，处理完成的移除
     */
    var processingRequest: MutableMap<String, UUID> = mutableMapOf()

    @SuppressLint("RestrictedApi")
    fun startWork(context: Context, params: MutableMap<String, Any>) {
        // 定义任务输入数据（可选）
        val inputData = Data(params)

        // 配置任务
        val workRequest =
            OneTimeWorkRequestBuilder<MyCoroutineWorker>().setInputData(inputData) // 设置输入数据
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED) // 网络条件
//                    .build()
//            )
                //            .setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)//配置了这个前台服务为必须
                .build()
        val funType = params["funType"] as String
        val workerId = workRequest.id
        processingRequest[funType] = workerId
        Log.e("ddd", "doWork---enqueue--主Thread:${Looper.getMainLooper().thread.name}")
        Log.e("ddd", "doWork---enqueue--Thread:${Thread.currentThread().name}")
        // 启动任务
        WorkManager.getInstance(context).enqueue(workRequest)
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id)
            .observeForever { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    when (workInfo.state) {
                        WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> {///正在运行或排队等待

                        }

                        else -> {
                            processingRequest.remove(funType)
                        }
                    }
                }
            }
    }
}