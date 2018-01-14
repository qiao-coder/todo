package com.tufei.todo.taskdetail

import com.tufei.todo.data.Task
import com.tufei.todo.data.source.TasksDataSource
import com.tufei.todo.data.source.TasksRepository

/**
 * @author tufei
 * @date 2018/1/14.
 */
class TaskDetailPresenter(val taskId: String, private val tasksRepository: TasksRepository, val view: TaskDetailContract.View) : TaskDetailContract.Presenter {

    init {
        view.presenter = this
    }

    override fun start() {
        openTask()
    }

    private fun openTask() {
        if (taskId.isEmpty()) {
            view.showMissingTask()
            return
        }

        view.setLoadingIndicator(true)
        tasksRepository.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                if (view.isActive) return
                view.setLoadingIndicator(false)
                showTask(task)
            }

            override fun onDataNotAvailable() {
                if (!view.isActive) return
                view.showMissingTask()
            }

        })
    }

    private fun showTask(task: Task) {
//        val title = task.title
//        val description = task.description
//        if (title.isEmpty()) {
//            view.hideTitle()
//        } else {
//            view.showTitle(title)
//        }
//        if (description.isEmpty()) {
//            view.hideDescription()
//        } else {
//            view.showDescription(description)
//        }
//        view.showCompletionStatus(task.isCompleted)

        with(view) {
            if (task.isEmpty) {
                hideTitle()
                hideDescription()
            } else {
                showTitle(task.title)
                showDescription(task.description)
            }
            showCompletionStatus(task.isCompleted)
        }
    }

    override fun editTask() {
        if (taskId.isEmpty()) {
            view.showMissingTask()
            return
        }
        view.showEditTask(taskId)
    }

    override fun deleteTask() {
        if (taskId.isEmpty()) {
            view.showMissingTask()
            return
        }
        tasksRepository.deleteTask(taskId)
        view.showTaskDeleted()
    }

    override fun completeTask() {
        if (taskId.isEmpty()) {
            view.showMissingTask()
            return
        }
        tasksRepository.completeTask(taskId)
        view.showTaskMarkedComplete()
    }

    override fun activateTask() {
        if (taskId.isEmpty()) {
            view.showMissingTask()
            return
        }
        tasksRepository.activateTask(taskId)
    }
}