package com.tufei.todo.addedittask

import com.tufei.todo.BasePresenter
import com.tufei.todo.BaseView

/**
 * @author tufei
 * @date 2018/1/22.
 */
/**
 * This specifies the contract between the view and the presenter.
 */
interface AddEditTaskContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun showEmptyTaskError()

        fun showTasksList()

        fun setTitle(title: String)

        fun setDescription(description: String)
    }

    interface Presenter : BasePresenter {

        var isDataMissing: Boolean

        fun saveTask(title: String, description: String)

        fun populateTask()
    }
}
