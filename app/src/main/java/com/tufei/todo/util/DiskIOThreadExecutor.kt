package com.tufei.todo.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author tufei
 * @date 2018/1/8.
 */
class DiskIOThreadExecutor : Executor {
    private val diskIO = Executors.newSingleThreadExecutor()
    override fun execute(command: Runnable?) {
        diskIO.execute(command)
    }
}