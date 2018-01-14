package com.tufei.todo.taskdetail

import com.tufei.todo.BasePresenter
import com.tufei.todo.BaseView

/**
 * @author tufei
 * @date 2018/1/14.
 */
interface TaskDetailContract {
    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingTask()

        fun hideTitle()

        fun showTitle(title: String)

        fun hideDescription()

        fun showDescription(description: String)

        fun showCompletionStatus(complete: Boolean)

        fun showEditTask(taskId: String)

        fun showTaskDeleted()

        fun showTaskMarkedComplete()

        fun showTaskMarkedActive()
    }

    interface Presenter : BasePresenter {

        fun editTask()

        fun deleteTask()

        fun completeTask()

        fun activateTask()
    }
}