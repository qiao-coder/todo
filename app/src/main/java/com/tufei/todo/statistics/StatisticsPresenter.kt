package com.tufei.todo.statistics

import com.tufei.todo.data.Task
import com.tufei.todo.data.source.TasksDataSource
import com.tufei.todo.data.source.TasksRepository

/**
 * @author tufei
 * @date 2018/1/22.
 */
class StatisticsPresenter(val tasksRepository: TasksRepository,
                          val view: StatisticsContract.View)
    : StatisticsContract.Presenter {
    init {
        view.presenter = this
    }

    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        view.setProgressIndicator(true)
        tasksRepository.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                var activeTasks = 0
                var completedTasks = 0

                tasks.forEach {
                    if (it.isCompleted) {
                        completedTasks += 1
                    } else {
                        activeTasks += 1
                    }
                }

                with(view) {
                    if (!isActive) {
                        return
                    }
                    setProgressIndicator(false)
                    showStatistics(activeTasks, completedTasks)
                }


            }

            override fun onDataNotAvailable() {
                with(view) {
                    if (!isActive) {
                        return
                    }
                    showLoadingStatisticsError()
                }
            }
        })
    }
}