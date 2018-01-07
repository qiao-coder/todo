package com.tufei.todo.data.source

import com.tufei.todo.data.Task


/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 *
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

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [TasksDataSource.LoadTasksCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
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

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [TasksDataSource.GetTaskCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val cachedTask = getTaskWithId(taskId)
        // Respond immediately with cache if available
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask)
            return
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        tasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                mCachedTasks.put(task.id, task)
                callback.onTaskLoaded(task)
            }

            override fun onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
                    override fun onTaskLoaded(task: Task) {
                        mCachedTasks.put(task.id, task)
                        callback.onTaskLoaded(task)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }

                })
            }

        })
    }

    override fun saveTask(task: Task) {
        tasksRemoteDataSource.saveTask(task)
        tasksLocalDataSource.saveTask(task)

        // Do in memory cache update to keep the app UI up to date
        mCachedTasks.put(task.id, task)

    }

    override fun completeTask(task: Task) {
        tasksRemoteDataSource.completeTask(task)
        tasksLocalDataSource.completeTask(task)

        val completedTask = Task(task.title, task.description)

        // Do in memory cache update to keep the app UI up to date
        mCachedTasks.put(task.id, completedTask)
    }

    override fun completeTask(taskId: String) {
        //这样写不行  编译器报错 因为getTaskWithId(taskId)返回的可能为空
        //completeTask(getTaskWithId(taskId))
        getTaskWithId(taskId)?.let { completeTask(it) }
    }

    override fun activateTask(task: Task) {
        tasksRemoteDataSource.activateTask(task)
        tasksLocalDataSource.activateTask(task)

        val activeTask = Task(task.title, task.description, task.id)

        // Do in memory cache update to keep the app UI up to date
        mCachedTasks.put(task.id, activeTask)
    }

    override fun activateTask(taskId: String) {
        getTaskWithId(taskId)?.let { activateTask(it) }
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        // Do in memory cache update to keep the app UI up to date
        /*
        java:
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
         */
        mCachedTasks = mCachedTasks.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {
        mCacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        mCachedTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)
        mCachedTasks.remove(taskId)
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

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param tasksRemoteDataSource the backend data source
         * *
         * @param tasksLocalDataSource  the device storage data source
         * *
         * @return the [TasksRepository] instance
         */
        @JvmStatic
        fun getInstance(tasksLocalDataSource: TasksDataSource, tasksRemoteDataSource: TasksDataSource): TasksRepository {
            if (INSTANCE == null) {
                INSTANCE = TasksRepository(tasksLocalDataSource, tasksRemoteDataSource)
            }
            return INSTANCE!!
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}