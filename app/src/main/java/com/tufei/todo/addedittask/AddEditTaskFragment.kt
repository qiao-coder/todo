package com.tufei.todo.addedittask

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tufei.todo.R
import com.tufei.todo.util.showToast
import kotlinx.android.synthetic.main.addtask_act.*
import kotlinx.android.synthetic.main.addtask_frag.*

/**
 * @author tufei
 * @date 2018/1/14.
 */
class AddEditTaskFragment : Fragment(), AddEditTaskContract.View {
    override val isActive: Boolean = isAdded
    override lateinit var presenter: AddEditTaskContract.Presenter

    lateinit var title: TextView
    lateinit var description: TextView

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(activity.fab_edit_task_done) {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.saveTask(title.text.toString(), description.text.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.addtask_frag, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        title = add_task_title
        description = add_task_description
        setHasOptionsMenu(true)
    }

    override fun showEmptyTaskError() {
        title.showToast(getString(R.string.empty_task_message))
    }

    override fun showTasksList() {
        with(activity) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setDescription(description: String) {
        this.description.text = description
    }


    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        //不要写fragment单例  静态context  会导致内存泄露
//        @Volatile
//        private lateinit var INSTANCE: AddEditTaskFragment
//
//        fun newInstance(taskId: String?): AddEditTaskFragment {
//            if (INSTANCE == null) {
//                synchronized(AddEditTaskFragment::class) {
//                    if (INSTANCE == null) {
//                        INSTANCE = AddEditTaskFragment()
//                    }
//                }
//            }
//            val bundle = Bundle()
//            bundle.putString(ARGUMENT_EDIT_TASK_ID, taskId)
//            INSTANCE.arguments = bundle
//            return INSTANCE
//        }

        fun newInstance(taskId: String?): AddEditTaskFragment {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_EDIT_TASK_ID, taskId)
            val addEditTaskFragment = AddEditTaskFragment()
            addEditTaskFragment.arguments = bundle
            return addEditTaskFragment
        }
    }
}