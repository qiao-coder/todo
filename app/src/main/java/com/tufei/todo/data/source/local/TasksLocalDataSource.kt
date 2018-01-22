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
        var runnable = Runnable {
            val tasks = tasksDao.getTasks()
            appExecutors.mainThread.execute {
                if (tasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(tasks)
                }
            }
        }
        appExecutors.diskIO.execute(runnable)
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        var runnable = Runnable {
            val task = tasksDao.getTaskById(taskId)
            appExecutors.mainThread.execute {
                if (task != null) {
                    callback.onTaskLoaded(task)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
        appExecutors.diskIO.execute(runnable)
    }

    override fun saveTask(task: Task) {
        val saveRunnable = Runnable { tasksDao.insertTask(task) }
        appExecutors.diskIO.execute(saveRunnable)
    }

    override fun completeTask(task: Task) {
        val completeRunnable = Runnable { tasksDao.updateCompleted(task.id, true) }
        appExecutors.diskIO.execute(completeRunnable)
    }

    override fun completeTask(taskId: String) {

    }

    override fun activateTask(task: Task) {
        val activateRunnable = Runnable { tasksDao.updateCompleted(task.id, false) }
        appExecutors.diskIO.execute(activateRunnable)
    }

    override fun activateTask(taskId: String) {

    }

    override fun clearCompletedTasks() {
        val clearTasksRunnable = Runnable { tasksDao.deleteCompletedTasks() }
        appExecutors.diskIO.execute(clearTasksRunnable)
    }

    override fun refreshTasks() {

    }

    override fun deleteAllTasks() {
        val deleteRunnable = Runnable { tasksDao.deleteTasks() }
        appExecutors.diskIO.execute(deleteRunnable)
    }

    override fun deleteTask(taskId: String) {
        val deleteRunnable = Runnable { tasksDao.deleteTaskById(taskId) }
        appExecutors.diskIO.execute(deleteRunnable)
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