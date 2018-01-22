package com.tufei.todo.addedittask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tufei.todo.R
import com.tufei.todo.data.Injection
import com.tufei.todo.util.replaceFragmentInActivity
import com.tufei.todo.util.setupActionBar
import kotlinx.android.synthetic.main.addtask_act.*

/**
 * @author tufei
 * @date 2018/1/13.
 */
class AddEditTaskActivity : AppCompatActivity() {
    lateinit var addEditTaskPresenter: AddEditTaskPresenter

    companion object {
        const val REQUEST_ADD_TASK = 1
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        val taskId = intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)

        setupActionBar(toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)
        }

        val addEditTaskFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as? AddEditTaskFragment ?: AddEditTaskFragment.newInstance(taskId).also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        var shouldLoadDataFromRepo = true

        savedInstanceState?.run {
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY)
        }

        addEditTaskPresenter = AddEditTaskPresenter(
                taskId,
                Injection.provideTasksRepository(applicationContext),
                addEditTaskFragment,
                shouldLoadDataFromRepo)

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY,addEditTaskPresenter.isDataMissing)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}