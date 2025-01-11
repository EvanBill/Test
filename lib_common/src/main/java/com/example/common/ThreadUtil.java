package com.example.common;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liean.cao on 2017/6/12.
 */

public class ThreadUtil {
    public final static String TAG = "ThreadUtil";
    public final static int THREAD_POOL_TYPE_CACHED = 1; //后台任务默认使用
    public final static int THREAD_POOL_TYPE_FIXED = 2; //界面显示相关任务优先使用
    public final static int THREAD_POOL_TYPE_SCHEDULED = 3;
    public final static int THREAD_POOL_TYPE_SINGLE = 4;
    public final static int THREAD_POOL_TYPE_SINGLE_SCHEDULED = 5;

    public final static int MAX_THREAD_COUNT_PER_PROCESS = 1; //每个处理器核心对应可初始化的最大线程数
    private static final int MAX_QUEUED_TASK = 4 ;
    private static final long KEEP_ALIVE_TIME = 20L ;

    /**
     * Java通过Executors提供四种线程池，分别为：
     * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
     * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
     * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     * newSingleThreadScheduledExecutor 结合scheduled 及 single 二者特性的线程池
     *
     * @param poolType 线程池类型
     * @return
     */
//    private static final HashMap<String, WeakReference<ExecutorService>> executorServiceHashMap = new HashMap<>();
    private static final int processors = Runtime.getRuntime().availableProcessors() ;
    //控制性能和效率，避免创建太多的线程，一样会卡盾
    private static final ThreadPoolExecutor cachedThreadPool = new ThreadPoolExecutor(0 , MAX_THREAD_COUNT_PER_PROCESS * processors, 20L , TimeUnit.SECONDS, new LinkedBlockingQueue<>());

//    private static final ThreadPoolExecutor cachedThreadPool = new ThreadPoolExecutor(0
//            , MAX_THREAD_COUNT_PER_PROCESS * processors
//            , KEEP_ALIVE_TIME , TimeUnit.SECONDS
//            , new ArrayBlockingQueue<>(MAX_QUEUED_TASK)
//            , Executors.defaultThreadFactory()
//            ,new ThreadPoolExecutor.DiscardOldestPolicy(){
//                @Override
//                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
//                    int activeCount = cachedThreadPool.getActiveCount() ;
//                    int poolSize = cachedThreadPool.getPoolSize() ;
//                    long taskCount = cachedThreadPool.getTaskCount() ;
//                    int queueSize = cachedThreadPool.getQueue().size() ;
//                    EdLog.e(TAG,"Rejecting task !" + r
//                            + " activeCount = " + activeCount
//                            + " ; poolSize = " + poolSize
//                            + " ; taskCount = " + taskCount
//                            + "; queueSize = " + queueSize  ) ;
//                }
//    });

    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);// 处理一些比较重要的任务


    public static ExecutorService getThreadPool(int poolType) {
        EdLog.e(TAG , "getThreadPool ,type is " + poolType ) ;
        switch (poolType) {
            case THREAD_POOL_TYPE_CACHED:
            default:
                return cachedThreadPool ;
            case THREAD_POOL_TYPE_FIXED:
                return fixedThreadPool ;
//                threadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT_PER_PROCESS);
//            case THREAD_POOL_TYPE_SCHEDULED:
//                threadPool = Executors.newScheduledThreadPool(MAX_THREAD_COUNT_PER_PROCESS * Runtime.getRuntime().availableProcessors());
//                break;
//            case THREAD_POOL_TYPE_SINGLE:
//                threadPool = Executors.newSingleThreadExecutor();
//                break;
//            case THREAD_POOL_TYPE_SINGLE_SCHEDULED:
//                threadPool = Executors.newSingleThreadScheduledExecutor();
//                break;
        }

//        ExecutorService threadPool = null;
//        String key = poolType + "_";//+ category;
//        WeakReference<ExecutorService> threadRef = executorServiceHashMap.get(key) ;
//        if(threadRef != null ){
//            if(threadRef.get() == null){
//                executorServiceHashMap.remove(key) ;
//            }else{
//                return threadRef.get() ;
//            }
//        }
//        switch (poolType) {
//            case THREAD_POOL_TYPE_CACHED:
//            default:
//                threadPool = new ThreadPoolExecutor(MAX_THREAD_COUNT_PER_PROCESS , MAX_THREAD_COUNT_PER_PROCESS * processors * 2, 20L , TimeUnit.SECONDS, new LinkedBlockingQueue<>());
//                break;
//            case THREAD_POOL_TYPE_FIXED:
//                threadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT_PER_PROCESS);
//                break;
//            case THREAD_POOL_TYPE_SCHEDULED:
//                threadPool = Executors.newScheduledThreadPool(MAX_THREAD_COUNT_PER_PROCESS * Runtime.getRuntime().availableProcessors());
//                break;
//            case THREAD_POOL_TYPE_SINGLE:
//                threadPool = Executors.newSingleThreadExecutor();
//                break;
//            case THREAD_POOL_TYPE_SINGLE_SCHEDULED:
//                threadPool = Executors.newSingleThreadScheduledExecutor();
//                break;
//        }
//        if (threadPool != null) {
//            executorServiceHashMap.put(key, new WeakReference<>(threadPool));
//            EdLog.i(TAG, "getThreadPool threadPool:" + threadPool.toString());
//        }
//        return threadPool;
    }

//    public static void testCachedThread() {
//        ExecutorService executorService = ThreadUtil.getThreadPool(ThreadUtil.THREAD_POOL_TYPE_CACHED);
//        for (int i = 0; i < 10; i++) {
//            final int index = i;
//            executorService.execute(new Runnable() {
//
//                @Override
//                public void run() {
//                    EdLog.d(TAG, "Cached Thread index:" + index);
//                }
//            });
//        }
//
//
//    }
//
//
//    public static void testFixedThread() {
//        ExecutorService executorService = ThreadUtil.getThreadPool(ThreadUtil.THREAD_POOL_TYPE_FIXED);
//        for (int i = 0; i < 100; i++) {
//            final int index = i;
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        EdLog.d(TAG, "Fixed Thread index:" + index);
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//
//    }
//
//    public static void testScheduledThread() {
//        ExecutorService executorService = ThreadUtil.getThreadPool(ThreadUtil.THREAD_POOL_TYPE_SCHEDULED);
//        if (executorService instanceof ScheduledThreadPoolExecutor) {
////            ((ScheduledThreadPoolExecutor) executorService).schedule(new Runnable() {
////                @Override
////                public void run() {
////                    EdLog.d(TAG, "Scheduled Thread delay 3 seconds");
////                }
////            }, 3, TimeUnit.SECONDS);
//
//            ((ScheduledThreadPoolExecutor) executorService).scheduleAtFixedRate(new Runnable() {
//                @Override
//                public void run() {
//                    EdLog.d(TAG, "Scheduled Thread delay 1 seconds, and excute every 3 seconds");
//                }
//            }, 1, 3, TimeUnit.SECONDS);
//        }
//    }
//
//    public static void testSingleThread() {
//        ExecutorService executorService = ThreadUtil.getThreadPool(ThreadUtil.THREAD_POOL_TYPE_SINGLE);
//        for (int i = 0; i < 10; i++) {
//            final int index = i;
//            executorService.execute(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        EdLog.d(TAG, "Single Thread index:" + index);
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//
//    }


    /**
     * @return
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


}
