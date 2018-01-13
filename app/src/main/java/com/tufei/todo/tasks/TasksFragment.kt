package com.tufei.todo.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.tufei.todo.R
import com.tufei.todo.data.Task

/**
 *  * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 * @author tufei
 * @date 2018/1/7.
 */
class TasksFragment : Fragment(), TasksContract.View {
    override var isActive: Boolean = false
        get() = isAdded

    override lateinit var presenter: TasksContract.Presenter

    private lateinit var listAdapter: TasksAdapter

    private lateinit var noTasksView: View

    private lateinit var noTaskIcon: ImageView

    private lateinit var noTaskMainView: TextView

    private lateinit var noTaskAddView: TextView

    private lateinit var tasksView: TextView

    private lateinit var filteringLabelView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TasksAdapter(ArrayList(0), itemListener)
    }

    private class TasksAdapter(tasks: List<Task>, private val itemListener: TaskItemListener)
        : BaseAdapter() {

        var tasks: List<Task> = tasks//这里是初始化，直接写入幕后字段
            set(tasks) {
                field = tasks
                notifyDataSetChanged()
            }

        override fun getItem(position: Int) = tasks[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = tasks.size

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
            val task = getItem(position)
            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.task_item, viewGroup, false)
            with(rowView.findViewById<TextView>(R.id.title)) {
                //相当于setText(task.titleForList)
                text = task.titleForList
            }

            //Active/completed task UI
            with(rowView.findViewById<CheckBox>(R.id.complete)) {
                isChecked = task.isCompleted
                val rowViewBackground =
                        if (task.isCompleted) R.drawable.list_completed_touch_feedback
                        else R.drawable.touch_feedback
                rowView.setBackgroundResource(rowViewBackground)
                setOnClickListener {
                    if (!task.isCompleted) {
                        itemListener.onCompleteTaskClick(task)
                    } else {
                        itemListener.onActivateTaskClick(task)
                    }
                }
            }

            //对比上面，这里的又长又丑
//            if(task.isCompleted){
//                rowView.setBackgroundDrawable(viewGroup.context.resources
//                        .getDrawable(R.drawable.list_completed_touch_feedback))
//            }else{
//                rowView.setBackgroundDrawable(viewGroup.context.resources
//                        .getDrawable(R.drawable.touch_feedback))
//            }
            return rowView
        }
    }


    val itemListener = object : TaskItemListener {
        override fun onTaskClick(clickedTask: Task) {
            presenter.openTaskDetails(clickedTask)
        }

        override fun onCompleteTaskClick(completedTask: Task) {
            presenter.completeTask(completedTask)
        }

        override fun onActivateTaskClick(activatedTask: Task) {
            presenter.activateTask(activatedTask)
        }

    }

    override fun setLoadingIndicator(active: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showTasks(tasks: List<Task>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAddTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showTaskDetailsUi(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showTaskMarkedComplete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showTaskMarkedActive() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showCompletedTasksCleared() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoadingTasksError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNoTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showActiveFilterLabel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showCompletedFilterLabel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAllFilterLabel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNoActiveTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNoCompletedTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSuccessfullySavedMessage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showFilteringPopUpMenu() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface TaskItemListener {
        fun onTaskClick(clickedTask: Task)

        fun onCompleteTaskClick(completedTask: Task)

        fun onActivateTaskClick(activatedTask: Task)
    }

    companion object {

        fun newInstance() = TasksFragment()
    }
}