package com.tufei.todo.data

import android.content.Context
import com.tufei.todo.data.source.TasksRepository
import com.tufei.todo.data.source.local.TasksLocalDataSource
import com.tufei.todo.data.source.local.ToDoDatabase
import com.tufei.todo.data.source.remote.TasksRemoteDataSource
import com.tufei.todo.util.AppExecutors

/**
 * 注意，不是用的class关键字，而是object
 *
 * 1）全都是静态方法的情况 : class ClassName改为 object ClassName 即可
 * 2）一部分是静态方法的情况 : 将方法用 companion object { } 包裹即可
 *
 * @author tufei
 * @date 2018/1/7.
 */
object Injection {
    fun provideTasksRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(
                TasksLocalDataSource.getInstance(AppExecutors(), database.tasksDao()),
                TasksRemoteDataSource.getInstance())
    }
}