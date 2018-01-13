package com.tufei.todo.tasks

import android.app.Activity
import com.tufei.todo.addedittask.AddEditTaskActivity
import com.tufei.todo.data.Task
import com.tufei.todo.data.source.TasksDataSource
import com.tufei.todo.data.source.TasksRepository

/**
 * Listens to user actions from the UI ([TasksFragment]), retrieves the data and updates the
 * UI as required.
 * @author tufei
 * @date 2018/1/12.
 */
class TasksPresenter(val tasksRepository: TasksRepository, val tasksView: TasksContract.View) : TasksContract.Presenter {
    private var mFirstLoad: Boolean = false
    override var currentFiltering: TasksFilterType
        get() = TasksFilterType.ALL_TASKS
        set(value) {}

    init {
        tasksView.presenter = this
    }

    override fun start() {
        loadTasks(false)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode
                && Activity.RESULT_OK == resultCode) {
            tasksView.showSuccessfullySavedMessage()
        }
    }

    override fun loadTasks(forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoad, true)
        mFirstLoad = false
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            tasksRepository.refreshTasks()
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        //EspressoIdlingResource.increment() // App is busy until further notice

        tasksRepository.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = ArrayList<Task>()

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                //if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                //    EspressoIdlingResource.decrement(); // Set app as idle.
                //}

                // We filter the tasks based on the requestType
                tasks.forEach {
                    when (TasksFilterType.ALL_TASKS) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(it)
                        TasksFilterType.ACTIVE_TASKS -> if (it.isActive) {
                            tasksToShow.add(it)
                        }
                        TasksFilterType.COMPLETED_TASKS -> if (it.isCompleted) {
                            tasksToShow.add(it)
                        }
                    }
                }

                // The view may not be able to handle UI updates anymore
                if(!tasksView.isActive){
                    return
                }
                if(showLoadingUI){
                    tasksView.setLoadingIndicator(false)
                }

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                if(!tasksView.isActive){
                    return
                }
                tasksView.showLoadingTasksError()
            }
        })

    }

    private fun processTasks(tasks: List<Task>) = if(tasks.isEmpty()){
        // Show a message indicating there are no tasks for that filter type.
        processEmptyTasks();
    }else{
        //Show the list of tasks
        tasksView.showTasks(tasks)
        //Set the filter label's text
        showFilterLabel()
    }

    private fun showFilterLabel() {
        when(currentFiltering){
            TasksFilterType.ACTIVE_TASKS->tasksView.showActiveFilterLabel()
            TasksFilterType.COMPLETED_TASKS->tasksView.showCompletedFilterLabel()
            else->tasksView.showAllFilterLabel()
        }
    }

    private fun processEmptyTasks() {
        when(currentFiltering){
            TasksFilterType.ACTIVE_TASKS->tasksView.showNoActiveTasks()
            TasksFilterType.COMPLETED_TASKS->tasksView.showNoCompletedTasks()
            else->tasksView.showNoTasks()
        }
    }

    override

    fun addNewTask() {
        tasksView.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Task) {
        tasksView.showTaskDetailsUi(requestedTask.id)
    }

    override fun completeTask(completedTask: Task) {
        tasksRepository.completeTask(completedTask)
        tasksView.showTaskMarkedComplete()
        loadTasks(false,false)
    }

    override fun activateTask(activeTask: Task) {
        tasksRepository.activateTask(activeTask)
        tasksView.showTaskMarkedActive()
        loadTasks(false,false)
    }

    override fun clearCompletedTasks() {
        tasksRepository.clearCompletedTasks()
        tasksView.showCompletedTasksCleared()
        loadTasks(false,false)
    }
}