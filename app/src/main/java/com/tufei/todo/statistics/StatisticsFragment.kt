package com.tufei.todo.statistics

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tufei.todo.R
import kotlinx.android.synthetic.main.statistics_frag.*

/**
 * @author tufei
 * @date 2018/1/22.
 */
class StatisticsFragment : Fragment(), StatisticsContract.View {
    override val isActive = isAdded

    override lateinit var presenter: StatisticsContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_frag, container, false)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        with(statistics) {
            if (active) {
                text = getString(R.string.loading)
            } else {
                text = ""
            }
        }
    }

    override fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        with(statistics) {
            if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks==0) {
                text = resources.getString(R.string.statistics_no_tasks)
            } else {
                val displayString = (resources.getString(R.string.statistics_active_tasks) + " "
                        + numberOfIncompleteTasks + "\n" + resources.getString(
                        R.string.statistics_completed_tasks) + " " + numberOfCompletedTasks)
                text = displayString
            }
        }
    }

    override fun showLoadingStatisticsError() {
        statistics.text = resources.getString(R.string.statistics_error)
    }


    companion object {
        fun newInstance() = StatisticsFragment()
    }
}