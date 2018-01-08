package com.tufei.todo.data.source.remote

import android.os.Handler
import com.google.common.collect.Lists
import com.tufei.todo.data.Task
import com.tufei.todo.data.source.TasksDataSource

/**
 * Implementation of the data source that adds a latency simulating network.
 * @author tufei
 * @date 2018/1/7.
 */
class TasksRemoteDataSource private constructor() : TasksDataSource {

    //必须标注这个L  不会自动提升类型为Long
    val SERVICE_LATENCY_IN_MILLIS = 5000L
    var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private fun addTask(title: String, description: String) {
        cacheAndPerform(Task(title, description)) {}
    }

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        perform(task)
        val cachedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        TASKS_SERVICE_DATA.put(cachedTask.id, cachedTask)
    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        val tasks = Lists.newArrayList(TASKS_SERVICE_DATA.values)
        Handler().postDelayed({
            callback.onTasksLoaded(tasks)
        }, SERVICE_LATENCY_IN_MILLIS)
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskId]
//        Handler().postDelayed({
//            callback.onTaskLoaded(task!!)
//        },SERVICE_LATENCY_IN_MILLIS)

        with(Handler()) {
            if (task != null) {
                postDelayed({ callback.onTaskLoaded(task) }, SERVICE_LATENCY_IN_MILLIS)
            } else {
                postDelayed({ callback.onDataNotAvailable() }, SERVICE_LATENCY_IN_MILLIS)
            }
        }
    }

    override fun saveTask(task: Task) {
        cacheAndPerform(task) {}
    }

    override fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
        }
    }

    override fun completeTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {
        cacheAndPerform(task) {}
    }

    override fun activateTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    companion object {

        private var INSTANCE: TasksRemoteDataSource? = null

        @JvmStatic
        fun getInstance(): TasksRemoteDataSource {
            return INSTANCE ?: TasksRemoteDataSource().apply { INSTANCE = this }
        }
    }
}