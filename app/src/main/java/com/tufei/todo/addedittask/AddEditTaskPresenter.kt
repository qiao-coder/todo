package com.tufei.todo.addedittask

import com.tufei.todo.data.Task
import com.tufei.todo.data.source.TasksDataSource
import com.tufei.todo.data.source.TasksRepository

/**
 * @author tufei
 * @date 2018/1/22.
 */
class AddEditTaskPresenter(val taskId: String?,
                           val tasksRepository: TasksRepository,
                           val view: AddEditTaskContract.View,
                           val shouldLoadDataFromRepo: Boolean)
    : AddEditTaskContract.Presenter, TasksDataSource.GetTaskCallback {

    override var isDataMissing: Boolean = false

    init {
        view.presenter = this
    }

    override fun start() {
        if (!isNewTask() && isDataMissing) {
            populateTask()
        }
    }

    private fun isNewTask(): Boolean {
        return taskId == null
    }

    override fun saveTask(title: String, description: String) {
        if (isNewTask()) {
            createTask(title, description)
        } else {
            updateTask(title, description)
        }
    }

    override fun populateTask() {
        if (isNewTask()) {
            throw RuntimeException("populateTask() was called but task is new.")
        }
        taskId?.let { tasksRepository.getTask(it, this@AddEditTaskPresenter) }
    }

    override fun onTaskLoaded(task: Task) {
        if (view.isActive) {
            with(view) {
                setTitle(task.title)
                setDescription(task.description)
            }
            isDataMissing = false
        }
    }

    override fun onDataNotAvailable() {
        if (view.isActive) {
            view.showEmptyTaskError()
        }
    }

    private fun createTask(title: String, description: String) {
        val newTask = Task(title, description)
        if (newTask.isEmpty) {
            view.showEmptyTaskError()
        } else {
            tasksRepository.saveTask(newTask)
            view.showTasksList()
        }
    }

    private fun updateTask(title: String, description: String) {
        if (isNewTask()) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        taskId?.let {
            tasksRepository.saveTask(Task(title, description, it))
            view.showTasksList()
        }
    }
}