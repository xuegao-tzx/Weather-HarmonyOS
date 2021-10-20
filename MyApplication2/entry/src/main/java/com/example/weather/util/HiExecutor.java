package com.example.weather.util;

import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;

public class HiExecutor {
    /*
     * 切换任务到主线程执行
     * @param runnable
     * */
    public static void runUI(Runnable runnable){
        // 切换到主线程
        EventRunner runner = EventRunner.getMainEventRunner();
        EventHandler eventHandler = new EventHandler(runner);
        //切换任务
        eventHandler.postSyncTask(runnable);
    }

    /*
     * 在子线程执行任务
     * @param runnable
     * */
    public static void runBG(Runnable runnable){
        //开启一个线程
        EventRunner runner = EventRunner.create(true);
        EventHandler eventHandler = new EventHandler(runner);
        // 执行任务
        eventHandler.postTask(runnable, 0, EventHandler.Priority.IMMEDIATE);
    }
}
