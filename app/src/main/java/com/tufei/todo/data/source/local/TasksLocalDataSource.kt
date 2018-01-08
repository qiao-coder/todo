package com.tufei.todo.data.source.local

import com.tufei.todo.data.Task
import com.tufei.todo.data.source.TasksDataSource
import com.tufei.todo.util.AppExecutors

/**
 *
 *
 * @author tufei
 * @date 2018/1/7.
 */

class TasksLocalDataSource(
        val appExecutors: AppExecutors,
        val tasksDao: TasksDao
) : TasksDataSource {

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {

    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearCompletedTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, tasksDao: TasksDao): TasksLocalDataSource {
            return INSTANCE ?: TasksLocalDataSource(appExecutors, tasksDao)
                    .apply { INSTANCE = this }
        }
    }
}