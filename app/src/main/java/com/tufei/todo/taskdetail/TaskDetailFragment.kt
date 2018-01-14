package com.tufei.todo.taskdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.tufei.todo.R
import com.tufei.todo.addedittask.AddEditTaskActivity
import com.tufei.todo.addedittask.AddEditTaskFragment
import com.tufei.todo.util.showToast
import kotlinx.android.synthetic.main.taskdetail_act.*
import kotlinx.android.synthetic.main.taskdetail_frag.*

/**
 * @author tufei
 * @date 2018/1/14.
 */
class TaskDetailFragment : Fragment(), TaskDetailContract.View {
    override val isActive: Boolean
        get() = isAdded

    override lateinit var presenter: TaskDetailContract.Presenter

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.taskdetail_frag, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        activity.fab_edit_task.setOnClickListener {
            presenter.editTask()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val deletePressed = item.itemId == R.id.menu_delete
        if (deletePressed) presenter.deleteTask()
        return deletePressed
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            task_detail_title.text = ""
            task_detail_description.text = getString(R.string.loading)
        }
    }

    override fun showMissingTask() {
        task_detail_title.text = ""
        task_detail_description.text = getString(R.string.no_data)
    }

    override fun hideTitle() {
        task_detail_title.visibility = View.GONE
    }

    override fun showTitle(title: String) {
        task_detail_title.text = title
    }

    override fun hideDescription() {
        task_detail_description.visibility = View.GONE
    }

    override fun showDescription(description: String) {
        task_detail_description.visibility = View.VISIBLE
        task_detail_description.text = description
    }

    override fun showCompletionStatus(complete: Boolean) {
        with(task_detail_complete) {
            isChecked = complete
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    presenter.completeTask()
                } else {
                    presenter.activateTask()
                }
            }
        }
    }

    override fun showEditTask(taskId: String) {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    override fun showTaskDeleted() {
        activity.finish()
    }

    override fun showTaskMarkedComplete() {
        view?.showToast(getString(R.string.task_marked_complete))
    }

    override fun showTaskMarkedActive() {
        view?.showToast(getString(R.string.task_marked_active))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                activity.finish()
            }
        }
    }

    companion object {
        private const val ARGUMENT_TASK_ID = "TASK_ID"
        private const val REQUEST_EDIT_TASK = 1
        fun newInstance(taskId: String): TaskDetailFragment {
            val arguments = Bundle()
            arguments.putString(ARGUMENT_TASK_ID, taskId)
            val fragment = TaskDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}