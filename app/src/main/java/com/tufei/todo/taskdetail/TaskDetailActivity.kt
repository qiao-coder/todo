package com.tufei.todo.taskdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tufei.todo.R
import com.tufei.todo.data.Injection
import com.tufei.todo.util.replaceFragmentInActivity
import com.tufei.todo.util.setupActionBar
import kotlinx.android.synthetic.main.taskdetail_act.*

/**
 * @author tufei
 * @date 2018/1/13.
 */
class TaskDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskdetail_act)

        //Set up the toolbar.
        setupActionBar(toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //Get the requested task id
        var taskId = intent.getStringExtra(EXTRA_TASK_ID)

        val taskDetailFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as? TaskDetailFragment ?: TaskDetailFragment.newInstance(taskId)
                .also { replaceFragmentInActivity(it, R.id.contentFrame) }

        //Create the presenter
        TaskDetailPresenter(
                taskId,
                Injection.provideTasksRepository(applicationContext),
        taskDetailFragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}