package com.tufei.todo.data.source

import com.tufei.todo.data.Task


/**
 * 1)主构造函数的参数，要声明为val或var，不然无法在类中直接当成员变量使用
 * 不声明也是可以的，但如果你要在类中使用该参数，必须在init块中，将其引用拷贝给对应的成员变量
 * (相当于java在构造方法中，对成员变量初始化)
 * 2)注意，val变量声明时，通常你会发现，不给它赋值，编译器会直接报错，但是，如果你在init块中，
 * 有给它赋值，就不会报错了(次构造函数里面给它赋值的话，不行)
 *
 * @author tufei
 * @date 2018/1/7.
 */
class TasksRepository private constructor(
        val tasksLocalDataSource: TasksDataSource,
        val tasksRemoteDataSource: TasksDataSource
) : TasksDataSource {

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    //var mCachedTasks: Map<String, Task>? = null  这里不要使用Map,Map是只读的
    var mCachedTasks: MutableMap<String, Task> = LinkedHashMap()
    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var mCacheIsDirty = false

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        //如果编译器判断mCachedTasks有可能为null,编译器就会报错，所以mCachedTasks一开始有初始化
        //不然使用mCachedTasks都要在后面加!!
        if (mCachedTasks.isNotEmpty() && !mCacheIsDirty) {
            //ArrayList<Task>(mCachedTasks.values)中的<Task>可以去掉
            callback.onTasksLoaded(ArrayList(mCachedTasks.values))
            return
        }
        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callback)
        } else {
            // Query the local storage if available. If not, query the network.
            tasksLocalDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    callback.onTasksLoaded(ArrayList(mCachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        getTaskWithId(taskId)
    }

    override fun saveTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearCompletedTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadTasksCallback) {
        tasksRemoteDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    private fun refreshCache(tasks: List<Task>) {
        mCachedTasks.clear()
        for (task in tasks) {
            mCachedTasks.put(task.id, task)
        }
        mCacheIsDirty = false
    }

    /*private fun getTaskWithId(taskId: String):Task? {
        //在 Kotlin 中，if是一个表达式，即它会返回一个值。 因此就不需要三元运算符
        return if(mCachedTasks.isEmpty())null else mCachedTasks[taskId]
    }*/
    //这样写更简洁
    private fun getTaskWithId(taskId: String) = mCachedTasks[taskId]

    //kotlin静态方法
    //1）全都是静态方法的情况 : class ClassName改为 object ClassName 即可
    //2）一部分是静态方法的情况 : 将方法用 companion object { } 包裹即可
    companion object {
        var INSTANCE: TasksRepository? = null
        fun getInstance(tasksLocalDataSource: TasksDataSource, tasksRemoteDataSource: TasksDataSource): TasksRepository {
            if (INSTANCE == null) {
                INSTANCE = TasksRepository(tasksLocalDataSource, tasksRemoteDataSource)
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}