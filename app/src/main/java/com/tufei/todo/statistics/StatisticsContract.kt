package com.tufei.todo.statistics

import com.tufei.todo.BasePresenter
import com.tufei.todo.BaseView


/**
 * @author tufei
 * @date 2018/1/22.
 */
interface StatisticsContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setProgressIndicator(active: Boolean)

        fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun showLoadingStatisticsError()
    }

    interface Presenter : BasePresenter
}