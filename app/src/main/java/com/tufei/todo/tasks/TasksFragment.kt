package com.tufei.todo.tasks

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import com.tufei.todo.R
import com.tufei.todo.addedittask.AddEditTaskActivity
import com.tufei.todo.data.Task
import com.tufei.todo.taskdetail.TaskDetailActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_item.*
import kotlinx.android.synthetic.main.tasks_act.*
import kotlinx.android.synthetic.main.tasks_frag.*
import kotlinx.android.synthetic.main.tasks_frag.view.*
import java.util.*

/**
 *  * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 * @author tufei
 * @date 2018/1/7.
 */
class TasksFragment : Fragment(), TasksContract.View {
    override var isActive: Boolean = isAdded

    override lateinit var presenter: TasksContract.Presenter

    private lateinit var rvAdapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rvAdapter = TasksAdapter(ArrayList(0), itemListener)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    //自动生成的inflater: LayoutInflater?带问号，去掉即可
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.tasks_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Set up tasks view
        with(rvTask) {
            val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            layoutManager = linearLayoutManager
            adapter = rvAdapter
        }

        noTaskAddView.setOnClickListener {
            showAddTask()
        }
        //这个是activity的控件
        with(activity.fab_add_task) {
            setImageResource(R.drawable.ic_add)
            setOnClickListener { presenter.addNewTask() }
        }

        //Set up progress indicator
        with(refresh_layout) {
            setColorSchemeColors(
                    ContextCompat.getColor(activity, R.color.colorPrimary),
                    ContextCompat.getColor(activity, R.color.colorAccent),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            )
            //Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = rvTask
            setOnRefreshListener { presenter.loadTasks(false) }
        }

        setHasOptionsMenu(true)
    }

    private val itemListener = object : TaskItemListener {
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
        view ?: return
        with(refresh_layout) {
            post {
                isRefreshing = active
            }
        }
        //也可以
//        refresh_layout.post {
//            refresh_layout.isRefreshing = active
//        }
    }

    override fun showTasks(tasks: List<Task>) {
        rvAdapter.tasks = tasks
        rvAdapter.notifyDataSetChanged()
    }

    override fun showAddTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK)
    }

    override fun showTaskDetailsUi(taskId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }

    override fun showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete))
    }

    private fun showMessage(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active))
    }

    override fun showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared))
    }

    override fun showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    override fun showNoTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        )
    }

    private fun showNoTasksViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        tasksView.visibility = GONE
        noTasksView.visibility = VISIBLE

        noTasksMain.text = mainText
        noTaskIcon.setImageDrawable(resources.getDrawable(iconRes))
        noTaskAddView.visibility = if (showAddView) VISIBLE else GONE
    }

    override fun showActiveFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabelView.text = resources.getString(R.string.label_all)
    }

    override fun showNoActiveTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false
        )
    }

    override fun showNoCompletedTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false
        )
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message))
    }

    override fun showFilteringPopUpMenu() {
        PopupMenu(context, activity.findViewById(R.id.menu_filter))
                .apply {
                    menuInflater.inflate(R.menu.filter_tasks, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.active -> presenter.currentFiltering = TasksFilterType.ACTIVE_TASKS
                            R.id.completed -> presenter.currentFiltering = TasksFilterType.COMPLETED_TASKS
                            else -> presenter.currentFiltering = TasksFilterType.ALL_TASKS
                        }
                        presenter.loadTasks(false)
                        true
                    }
                    show()
                }
    }

    private class TasksAdapter(tasks: List<Task>, private val itemListener: TaskItemListener)
        : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {


        var tasks: List<Task> = tasks//这里是初始化，直接写入幕后字段
                //这里其实就相当于set方法，
                //而且，这里的tasks其实跟上面的tasks无关，可以随便命名
            set(tasks) {
                field = tasks
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val root = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
            return TaskViewHolder(root)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.title.text = task.titleForList

            with(holder.complete) {
                isChecked = task.isCompleted
                val rowViewBackground =
                        if (task.isCompleted) R.drawable.list_completed_touch_feedback
                        else R.drawable.touch_feedback
                holder.containerView.setBackgroundResource(rowViewBackground)
                setOnClickListener {
                    if (!task.isCompleted) {
                        itemListener.onCompleteTaskClick(task)
                    } else {
                        itemListener.onActivateTaskClick(task)
                    }
                }
            }
        }

        override fun getItemCount() = tasks.size

        override fun getItemId(position: Int) = position.toLong()

        class TaskViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
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